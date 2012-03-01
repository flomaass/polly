package polly.core.conversations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import polly.util.concurrent.ThreadFactoryBuilder;

import de.skuzzle.polly.sdk.AbstractDisposable;
import de.skuzzle.polly.sdk.Conversation;
import de.skuzzle.polly.sdk.ConversationManager;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.eventlistener.MessageEvent;
import de.skuzzle.polly.sdk.eventlistener.MessageListener;
import de.skuzzle.polly.sdk.exceptions.ConversationException;
import de.skuzzle.polly.sdk.exceptions.DisposingException;
import de.skuzzle.polly.sdk.model.User;



public class ConversationManagerImpl extends AbstractDisposable implements ConversationManager {
    
    private static Logger logger = Logger.getLogger(
                    ConversationManagerImpl.class.getName());

    
    
    private class ConversationImpl extends AbstractDisposable 
                implements Conversation, MessageListener {
        
        private List<MessageEvent> history;
        private BlockingQueue<MessageEvent> readQueue;
        protected String channel;
        private User user;
        private MyPolly myPolly;
        private Thread readThread;
        private long lastInput;
        private int idleTimeout;
        
        public ConversationImpl(MyPolly myPolly, User user, String channel, 
                int idleTimeout) {
            this.myPolly = myPolly;
            this.user = user;
            this.channel = channel;
            this.readQueue = new LinkedBlockingQueue<MessageEvent>();
            this.history = new ArrayList<MessageEvent>();
            
            /*
             * This is a constructor. The following text therefore is totally stupid o_O
             * 
             * Important:
             * Set lastInput before setting idelTimeout. Otherwise it may happen
             * that the timeout thread checks for idling right before idleTimout
             * has been set. That would cause #isIdle() to return true although
             * this conversation hasn't even started!
             */
            this.lastInput = System.currentTimeMillis();
            this.idleTimeout = idleTimeout;
        }
        
        
        
        private void checkClosed() {
            if (this.isDisposed()) {
                throw new IllegalStateException("ConversationImpl closed");
            }
        }
        
        
        
        @Override
        public String readStringLine() throws IOException, InterruptedException {
            return this.readLine().getMessage();
        }

        
        
        @Override
        public MessageEvent readLine() throws IOException, InterruptedException {
            this.checkClosed();
            
            // HACK: use the history list to synchronize on to save an extra attribute
            //       to lock on.
            synchronized (this.history) {
                if (this.readThread == null) {
                    this.readThread = Thread.currentThread();
                } else if (!this.readThread.equals(Thread.currentThread())) {
                    throw new IOException("invalid cross thread read");
                }
            }
            
            MessageEvent msg = this.readQueue.take();
            this.lastInput = System.currentTimeMillis();
            return msg;
        }

        
        
        @Override
        public void writeLine(String line) {
            this.checkClosed();
            this.myPolly.irc().sendMessage(this.channel, line, this);
        }

        
        
        @Override
        public List<MessageEvent> getHistory() {
            return this.history;
        }
        
        
        
        public boolean isIdle() {
            return System.currentTimeMillis() - this.lastInput > this.idleTimeout;
        }
        
        
        
        private synchronized void onMessage(final MessageEvent e) {
            assert !this.isDisposed() : "Listener should have been removed before closing";
            if (!e.getChannel().equals(this.channel) || 
                !e.getUser().getNickName().equals(this.user.getCurrentNickName())) {
                
                return;
            }
            
            this.history.add(e);
            ConversationManagerImpl.this.convExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ConversationImpl.this.readQueue.put(e);
                    } catch (InterruptedException e1) {
                        logger.warn("Interrupted while reading", e1);
                    }
                }
            });
        }
        
        

        @Override
        public void publicMessage(MessageEvent e) {
            this.onMessage(e);
        }

        
        
        @Override
        public void privateMessage(MessageEvent e) {
            this.onMessage(e);
        }
        
        
        
        @Override
        public void actionMessage(MessageEvent e) {}

        
        
        @Override
        protected void actualDispose() throws DisposingException {
            synchronized (crossMutex) {
                this.myPolly.irc().removeMessageListener(this);
                ConversationManagerImpl.this.cache.remove(this);
                
                if (this.readThread != null) {
                    this.readThread.interrupt();
                }
                
                this.history.clear();
                this.readQueue.clear();
            }
        }
        
        
        
        @Override
        public void close() {
            try {
                if (!this.isDisposed()) {
                    this.dispose();
                }
            } catch (DisposingException e) {
                logger.error("Error while disposing", e);
            }
        }
        
        
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ConversationImpl other = (ConversationImpl) obj;
            if (channel == null) {
                if (other.channel != null)
                    return false;
            } else if (!channel.equals(other.channel))
                return false;
            if (user == null) {
                if (other.user != null)
                    return false;
            } else if (!user.getCurrentNickName().equals(other.user.getCurrentNickName()))
                return false;
            return true;
        }
        
        
        @Override
        public String toString() {
            return "CONV " + this.channel + ": " + this.user.getCurrentNickName(); 
        }
    }
    

   
    
    // Object to synchronize on when closing or creating conversations
    private static Object crossMutex = new Object();
    
    
    private ExecutorService convExecutor;
    private ScheduledExecutorService timeoutSched;
    private List<Conversation> cache;
    
    
    
    public ConversationManagerImpl() {
        this.convExecutor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder("CONVERSATION_%n%"));
        this.timeoutSched = Executors.newScheduledThreadPool(1, 
            new ThreadFactoryBuilder("CONVERSATION_TIMEOUT"));
        this.cache = Collections.synchronizedList(new LinkedList<Conversation>());
        
        
        /*
         * Schedule a Runnable that iterates through all conversations and checks 
         * whether they are idle. If so, they are closed.
         */
        this.timeoutSched.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // XXX: This is an unsafe check for emptiness. This avoids unneeded
                //      synchronizations. 
                if (cache.isEmpty()) {
                    return;
                }
                
                synchronized (crossMutex) {
                    for (Conversation conv : cache) {
                        if (conv.isIdle()) {
                            logger.warn("Auto closing idling conversation: " + conv);
                            conv.close();
                        }
                    }
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);
    }
    
    

    @Override
    public Conversation create(MyPolly myPolly, User user, String channel)
            throws ConversationException {

        return this.create(myPolly, user, channel, 60);
    }
    
    
    
    public Conversation create(MyPolly myPolly, User user, String channel, int idleTimeout) 
            throws ConversationException {
        
        synchronized (crossMutex) {
            Conversation key = new ConversationImpl(myPolly, user, channel, idleTimeout);
            logger.info("Checking for existing conversation");
            if (this.cache.contains(key)) {
                throw new ConversationException("Conversation already active");
            }
            
            ConversationImpl c = new ConversationImpl(
                myPolly, user, channel, idleTimeout * 1000); // calc timeout in seconds
            myPolly.irc().addMessageListener(c);
            this.cache.add(c);
            logger.debug("Created new conversation with " + user.getCurrentNickName() + 
                    " on channel " + channel);
            return c;
        }
    }
    


    @Override
    protected void actualDispose() throws DisposingException {
        try {
            for (Conversation conv : this.cache) {
                conv.dispose();
            }
            this.convExecutor.shutdown();
            this.timeoutSched.shutdown();
            this.cache.clear();
        } catch (Exception e) {
            throw new DisposingException(e);
        }
    }
}
