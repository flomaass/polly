package polly;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map.Entry;

import de.skuzzle.polly.sdk.Disposable;

public class PollyConfiguration extends Configuration 
        implements de.skuzzle.polly.sdk.Configuration, Disposable {

    public final static String LOG_CONFIG_FILE = "loggerSettings";
    public final static String PLUGIN_EXCLUDES = "pluginExcludes";
    public final static String CHANNELS = "channels";
    public final static String NICKNAME = "nickName";
    public final static String IDENT = "ident";
    public final static String SERVER = "server";
    public final static String PORT = "port";
    public final static String DB_URL = "dbUrl";
    public final static String DB_USER = "dbUser";
    public final static String DB_PASSWORD = "dbPassword";
    public final static String DB_DRIVER = "dbDriver";
    public final static String IRC_LOGGING = "ircLogging";
    public final static String ADMIN_NAME = "adminUserName";
    public final static String ADMIN_PASSWORD_HASH = "adminPasswordHash";
    public final static String ADMIN_USER_LEVEL = "adminUserLevel";
    public final static String DECLARATION_CACHE = "declarationCache";
    public final static String DATE_FORMAT = "dateFormat";
    public final static String NUMBER_FORMAT = "numberFormat";
    public final static String EVENT_THREADS = "eventThreads";
    public final static String IGNORED_COMMANDS ="ignoreCommands";
    public final static String ENCODING = "encoding";
    public final static String LINE_LENGTH = "lineLength";
    public final static String RECONNECT_DELAY = "reconnectDelay";
    public final static String ENABLE_TELNET = "enableTelnet";
    public final static String TELNET_PORT = "telnetPort";
    
	private String logConfigFile;
	private String[] pluginExcludes;
	private String[] channels;
	private String[] ignoreCommands;
	private String nickName;
	private String ident;
	private String server;
	private int port;
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	private String dbDriver;
	private boolean ircLogging;
	private String adminUserName;
	private String adminPasswordHash;
	private int adminUserlevel;
	private String declarationCache;
	private String dateFormatString;
	private String numberFormatString;
	private int eventThreads;
	private String encodingName;
	private Charset encoding;
	private int lineLength;
	private int reconnectDelay;
	private int telnetPort;
	private boolean enableTelnet;
	
	
	
	public PollyConfiguration(String filename) 
	        throws IOException, ConfigurationFileException {
	    
		super(filename);
		this.init();
	}
	
	
	public PollyConfiguration() throws ConfigurationFileException {
	    super();
	    this.init();
	}
	
	
	public PollyConfiguration(PollyConfiguration defaults) 
	        throws ConfigurationFileException {
	    super(defaults.props);
	    this.init();
	}
	
	

	@Override
	protected void init() throws ConfigurationFileException {
		this.logConfigFile = this.props.getProperty(LOG_CONFIG_FILE, "cfg/logging.cfg");
        this.nickName = this.props.getProperty(NICKNAME, "polly");
        this.ident = this.props.getProperty(IDENT, "pollyp");
        this.server = this.props.getProperty(SERVER, "server");
        this.dbUrl = this.props.getProperty(DB_URL, "jdbc:hsqldb:file:./db/polly");
        this.dbUser = this.props.getProperty(DB_USER, "polly");
        this.dbPassword = this.props.getProperty(DB_PASSWORD, "polly123");
        this.dbDriver = this.props.getProperty(DB_DRIVER, "org.hsqldb.jdbcDriver");
        this.adminUserName = this.props.getProperty(ADMIN_NAME, "C0mb4t:");
        this.adminPasswordHash = this.props.getProperty(ADMIN_PASSWORD_HASH);
        this.declarationCache = this.props.getProperty(DECLARATION_CACHE, "cache/");
        this.dateFormatString = this.props.getProperty(DATE_FORMAT, "dd.MM.yyyy@HH:mm:ss");
        this.numberFormatString = this.props.getProperty(NUMBER_FORMAT, "0.####");
        this.encodingName = this.props.getProperty(ENCODING, "ISO-8859-1");
        
            
        try {
            this.encoding = Charset.forName(this.encodingName);
        } catch (Exception e) {
            throw new ConfigurationFileException("Invalid encoding: " + 
                this.encodingName);
        }
        
        String tmp = this.props.getProperty(PLUGIN_EXCLUDES, "");
        this.pluginExcludes = tmp.split(",");
        
        tmp = this.props.getProperty(CHANNELS, "");
        this.channels = tmp.split(",");
        
        tmp = this.props.getProperty(IGNORED_COMMANDS, "");
        this.ignoreCommands = tmp.split(",");
        
        tmp = this.props.getProperty(PORT, "6669");
        this.port = this.parseInteger(tmp, 32, "Ung�ltige Portnummer");
        
        tmp = this.props.getProperty(IRC_LOGGING, "on");
        this.ircLogging = this.parseBoolean(tmp);
        
        tmp = this.props.getProperty(ADMIN_USER_LEVEL, "1337");
        this.adminUserlevel = this.parseInteger(tmp, 1, "Ung�ltiges Admin User Level.");
        
        tmp = this.props.getProperty(EVENT_THREADS, "4");
        this.eventThreads = this.parseInteger(tmp, 1, "Ung�ltige Eventthread Zahl.");
        
        tmp = this.props.getProperty(LINE_LENGTH, "300");
        this.lineLength = this.parseInteger(tmp, 200, "Attribut lineLength zu kurz.");
        
        tmp = this.props.getProperty(RECONNECT_DELAY, "2000");
        this.reconnectDelay = this.parseInteger(tmp, 1000, "Reconnect delay zu niedrig.");
        
        tmp = this.props.getProperty(ENABLE_TELNET, "true");
        this.enableTelnet = this.parseBoolean(tmp);
        
        tmp = this.props.getProperty(TELNET_PORT, "23");
        this.telnetPort = this.parseInteger(tmp, 0, "Ung�ltige Portnummer.");
	}
		

	
	public String[] getPluginExcludes() {
		return pluginExcludes;
	}
	
	
	
	public void setPluginExcludes(String excludes) {
	    this.pluginExcludes = excludes.split(",");
	    this.props.setProperty(PLUGIN_EXCLUDES, excludes);
	}


	
	public String getNickName() {
		return nickName;
	}


	
	public String getIdent() {
		return ident;
	}
	
	
	
	public String getServer() {
		return server;
	}
	
	

	public int getPort() {
		return port;
	}
	
	
	public String[] getChannels() {
		return this.channels;
	}

	

	public String getLogConfigFile() {
		return logConfigFile;
	}

	

	public String getDbUrl() {
        return dbUrl;
    }



    public String getDbUser() {
        return dbUser;
    }



    public String getDbPassword() {
        return dbPassword;
    }



    public String getDbDriver() {
        return dbDriver;
    }

    
    
    public boolean getIrcLogging() {
        return this.ircLogging;
    }
    
    
    
    public String getAdminUserName() {
        return this.adminUserName;
    }
    
    
    
    public String getAdminPasswordHash() {
        return this.adminPasswordHash;
    }
    
    
    
    public int getAdminUserLevel() {
        return this.adminUserlevel;
    }
    
    
    
    public String getDeclarationCachePath() {
        return this.declarationCache;
    }
    
    
    
    public String getDateFormatString() {
        return this.dateFormatString;
    }
    
    
    
    public String getNumberFormatString() {
        return this.numberFormatString;
    }
    
    
    
    public int getEventThreads() {
        return this.eventThreads;
    }
    
    
    
    public String[] getIgnoredCommands() {
        return this.ignoreCommands;
    }
    
    
    
    public String getEncodingName() {
        return this.encodingName;
    }
    
    
    
    public Charset getEncoding() {
        return this.encoding;
    }

    
    
    public int getLineLength() {
        return this.lineLength;
    }
    
    
    
    public int getReconnectDelay() {
        return this.reconnectDelay;
    }
    
    
    public boolean enableTelnet() {
        return this.enableTelnet;
    }
    
    
    
    public int getTelnetPort() {
        return this.telnetPort;
    }

    
    
    @Override
	public synchronized <T> void setProperty(String name, T value) {
		this.props.setProperty(name, value.toString());
		/*
		 * HACK: update all 'static' config fields.
		 */
		try {
		    this.init();
		} catch (ConfigurationFileException e) {
		    e.printStackTrace();
		}
	}


	
	@Override
	public String readString(String name) {
		return this.readString(name, "");
	}

	

	@Override
	public int readInt(String name) {
		 return this.readInt(name, 0);
	}


	
	@Override
	public synchronized String readString(String name, String defaultValue) {
		return this.props.getProperty(name, defaultValue);
	}


	
	@Override
	public synchronized int readInt(String name, int defaultValue) {
		String tmp = this.props.getProperty(name);
		int i = defaultValue;
		try {
			i = Integer.parseInt(tmp);
		} catch (Exception ignore) {}
		
		return i;	
	}



    @Override
    public void dispose() {
        try {
            this.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Entry<Object, Object> entry : this.props.entrySet()) {
            b.append(entry.getKey());
            b.append(" = ");
            b.append(entry.getValue());
            b.append("\n");
        }
        return b.toString();
    }
}
