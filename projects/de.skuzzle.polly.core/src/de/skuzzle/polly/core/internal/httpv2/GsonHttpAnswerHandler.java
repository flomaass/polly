package de.skuzzle.polly.core.internal.httpv2;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.http.api.answers.HttpAnswer;
import de.skuzzle.polly.http.api.answers.HttpAnswerHandler;
import de.skuzzle.polly.sdk.httpv2.GsonHttpAnswer;


public class GsonHttpAnswerHandler extends HttpAnswerHandler {

    @Override
    public void handleAnswer(HttpAnswer answer, HttpEvent e, OutputStream out)
            throws IOException {
        
        try {
            final GsonHttpAnswer gsonAnswer = (GsonHttpAnswer) answer;
            
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            // TODO: hardcoded reference to encoding
            final Writer w = new OutputStreamWriter(
                    new BufferedOutputStream(out), "UTF-8"); //$NON-NLS-1$
            w.write(gson.toJson(gsonAnswer.getValue()));
            w.flush();
            
        } catch (Exception e1) {
            e1.printStackTrace();
            throw e1;
        }
    }

}
