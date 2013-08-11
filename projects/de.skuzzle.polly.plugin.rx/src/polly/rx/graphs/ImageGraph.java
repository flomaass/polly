package polly.rx.graphs;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import de.skuzzle.polly.tools.streams.FastByteArrayOutputStream;


public class ImageGraph extends Graph {

    private final BufferedImage image;
    
    
    
    public ImageGraph(int width, int height, int minY, int maxY, int stepY) {
        super(width, height, minY, maxY, stepY);
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
    }
    
    
    
    public void updateImage() {
        this.draw(this.image.createGraphics());
    }
    
    
    
    public void drawImageTo(Graphics2D g) {
        g.drawImage(this.image, 0, 0, new ImageObserver() {
            
            @Override
            public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4,
                int arg5) {
                return false;
            }
        });
    }
    
    
    
    public InputStream getBytes() {
        final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream("test.png");
            ImageIO.write(this.image, "png", out);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return out.getInputStreamForBuffer();
    }
}
