package polly.tv.core;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.skuzzle.polly.sdk.time.DateUtils;

import polly.tv.entities.TVEntity;


public class TVInfoCrawlTask extends AbstractCrawlTask {

    private final static int TIME_GROUP = 1;
    private final static int TITLE_GROUP = 2;
    private final static int SUBTITLE_GROUP = 4;
    
    
    
    private static Pattern TV_PATTERN = Pattern.compile(
        "class=\"PGT\"\\D+(\\d+:\\d+).*?class=\"11px bold\">([^<]+)(.*?class=[\"']subTitle[\"']>([^<]+))?", Pattern.DOTALL
    );
    
    
    private Date base;
    private String channel;
    private int lastHour;
    
    
    
    public TVInfoCrawlTask(TVProgramIndexer indexer, String url, Date base, 
            String channel) {
        super(indexer, url);
        this.base = base;
        this.channel = channel;
    }
    
    

    @Override
    public List<TVEntity> parseResults(String page) {
        Matcher m = TV_PATTERN.matcher(page);
        List<TVEntity> result = new LinkedList<TVEntity>();
        
        while (m.find()) {
            String time = new String(
                page.substring(m.start(TIME_GROUP), m.end(TIME_GROUP)));
            String title = new String(
                page.substring(m.start(TITLE_GROUP), m.end(TITLE_GROUP)));
            String subtitle = "";
            
            if (m.start(SUBTITLE_GROUP) != -1) {
                subtitle = new String(
                    page.substring(m.start(SUBTITLE_GROUP), m.end(SUBTITLE_GROUP)));
            }
            
            TVEntity tve = new TVEntity(this.parseTimeString(time), new Date(), 
                this.channel, title, subtitle);
            result.add(tve);
        }
        
        return result;
    }
    
    
    
    private Date parseTimeString(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        
        if (hours < this.lastHour) {
            this.base = DateUtils.getDayAhead(this.base, 1);
        }
        this.lastHour = hours;
        
        return DateUtils.timeFor(this.base, hours, minutes, 0);
    }

}
