package polly.rx.captcha;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import polly.rx.captcha.ImgUtil.BoundingBox;


public class RxCaptchaKiller {

    private final static String fileExtension = ".png"; //$NON-NLS-1$
    private final static String CAPTCHA_URL = "http://www.revorix.info/gfx/code/code.png"; //$NON-NLS-1$
    private final ImageDatabase db;
    private final File captchaHistoryDir;
    private final String tessdataPrefix;


    public final static class CaptchaResult {
        final File tempFile;
        final Mat capthaImg;
        String captcha;

        CaptchaResult(File tempFile, Mat capthaImg) {
            this.tempFile = tempFile;
            this.capthaImg = capthaImg;
        }
    }



    public RxCaptchaKiller(ImageDatabase db, File captchaHistory, String tessdataPrefix) {
        this.db = db;
        this.captchaHistoryDir = captchaHistory;
        this.tessdataPrefix = tessdataPrefix;
    }



    public String decodeCurrentCaptcha() {
        final CaptchaResult captcha = readCaptcha();
        final StringBuilder b = new StringBuilder();
        
        boolean solvedViaOcr = false;
        // try alternate decoding via ocr
        String ocrResult = decodeViaOCR(captcha.tempFile);
        if(!ocrResult.isEmpty() && ocrResult.length() == 4) {
            solvedViaOcr = true;
            b.setLength(0);
            b.append(ocrResult);
        }
        if(!solvedViaOcr)
        {
            b.setLength(0); // reset StringBuilder
            final List<BoundingBox> boxes = new ArrayList<>();
            ImgUtil.extractFeatures(captcha.capthaImg, boxes);
    
            boolean needClassification = false;
            for (final BoundingBox bb : boxes) {
                final Mat extracted = ImgUtil.imageFromBoundingBox(bb);
    
                final String c = this.db.tryClassify(extracted, bb);
                needClassification |= c.equals("?"); //$NON-NLS-1$
                b.append(c);
            }
            if (needClassification) {
                captcha.captcha = b.toString();
                this.db.needsClassification(captcha);
            } else {
                final String fileName = b.toString()  + fileExtension;
                final Path source = captcha.tempFile.toPath();
                final Path target = this.captchaHistoryDir.toPath().resolve(fileName);
                if (!Files.exists(target)) {
                    tryMove(source, target);
                }
            }
        }
        return b.toString();
    }

    private void tryMove(Path source, Path target) {
        try {
            Files.move(source, target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private CaptchaResult readCaptcha() {
        try {
            // HACK: download captcha to file, because conversion to IplImage does
            // not work properly
            final URL url = new URL(CAPTCHA_URL);
            final byte[] buffer = new byte[1024];
            final File tempFile = File.createTempFile("captcha_",  //$NON-NLS-1$
                    "" + System.nanoTime() + fileExtension);  //$NON-NLS-1$
            tempFile.deleteOnExit();

            final HttpURLConnection c = Anonymizer.openConnection(url);
            try (final InputStream in = c.getInputStream();
                    OutputStream out = new FileOutputStream(tempFile)) {
                int length = 0;
                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }
                out.flush();
            }

            final Mat serverImg = Highgui.imread(tempFile.toString(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
            return new CaptchaResult(tempFile, serverImg);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    private String decodeViaOCR(File file) {
        // alternate classification via OCR
        // first some image processing
        // load image
        Mat image = Highgui.imread(file.toString(), Highgui.CV_LOAD_IMAGE_GRAYSCALE);
        // enlarge
        Imgproc.pyrUp(image, image);
        // edge detection
        final double thresh = 100;
        Mat cannied = image.clone();
        Imgproc.Canny(image, cannied, thresh, thresh * 3);
        // contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(cannied, contours, new Mat(), Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(image, contours, -1, new Scalar(255));
        // cool threshold image
        Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_OTSU);
        // black font on white background
        Imgproc.threshold(image, image, 111, 255, Imgproc.THRESH_BINARY_INV);
        // convert Mat to buffered Image for ocr engine
        final BufferedImage afterImgproc = ImgUtil.Mat2BufferedImage(image);
        // set up OCR engine
        ITesseract instance = new Tesseract(); // JNA Interface Mapping
        instance.setDatapath(tessdataPrefix);
        instance.setPageSegMode(8);
        instance.setLanguage("rx"); //$NON-NLS-1$
        try {
            String ocrResult = instance.doOCR(afterImgproc);
            return ocrResult.toLowerCase().trim().replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (TesseractException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String();
    }
}
