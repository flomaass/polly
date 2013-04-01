package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.skuzzle.polly.sdk.Types;


public class ResourcePriceGrabber {

    private final static String API_URL = "http://qzone.servebbs.net/pollyPriceDaily.php";

    
    private String ts;
    private Map<String, Types> prices;
    private int refreshCounter;
    private final int refreshThreshold;
    
    
    
    public ResourcePriceGrabber(int refreshThreshold) {
        this.refreshThreshold = refreshThreshold;
        this.refreshCounter = refreshThreshold;
    }
    
    
    
    public synchronized Map<String, Types> getPrices() {
        if (this.refreshCounter++ % this.refreshThreshold == 0) {
            try {
                this.refresh();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.prices;
    }
    
    
    
    private void refresh() throws IOException {
        final URL url = new URL(API_URL);
        BufferedReader r = null;
        try {
            r = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = null;
            int i = 0;
            final Map<String, Types> result = new HashMap<String, Types>();
            while ((line = r.readLine()) != null) {
                ++i;
                switch (i) {
                case 1: break;
                case 2: break;
                case 3: break;
                case 4:
                    if (line.equals(this.ts)) {
                        result.putAll(this.prices);
                        return;
                    }
                    this.ts = line;
                case 5: break;
                default:
                    final String[] parts = line.split(";");
                    result.put(parts[0], new Types.NumberType(
                        Double.parseDouble(parts[1])));
                    break;
                }
            }
            this.prices = result;
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }
}
