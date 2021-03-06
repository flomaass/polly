package de.skuzzle.polly.sdk.resources;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.Set;


/**
 * @author Simon Taddiken
 * @since 1.1
 */
public class Resources {
    
    /** Lock object for locale initialization */
    final static Object MUTEX = new Object();

    /** This field will be set by polly during initialization */
    static Locale pollyLocale;
    
    /** Reads property files in UTF-8 */
    private final static Control UTF8 = new UTF8Control();
    
    /** Contains {@link Constants} subclasses which have already been initialized */
    private static final Set<Class<? extends Constants>> initialized = new HashSet<>();
    
    
    
    public static PollyBundle get(String family, ClassLoader cl) {
        final ResourceBundle r = ResourceBundle.getBundle(
                family, getLocale(), cl, UTF8);
        return new PollyBundle(r);
    }
    
    
    
    public static void init(String family, Class<? extends Constants> constants) {
        synchronized (initialized) {
            if (!initialized.add(constants)) {
                // have already been initialized
                return;
            }
        }
        
        final PollyBundle bundle = get(family, constants.getClassLoader());
        for (final Field f : constants.getFields()) {
            if (Modifier.isStatic(f.getModifiers()) && 
                Modifier.isPublic(f.getModifiers()) &&
                !Modifier.isFinal(f.getModifiers()) && 
                f.getType() == String.class) {
                
                f.setAccessible(true);
                // this is a public static string field, load value associated with the 
                // field name from the resource bundle
                final String value = bundle.get(f.getName()).intern();
                try {
                    f.set(null, value);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    throw new RuntimeException(
                            "Resource initialization failed. Family: " + family +  //$NON-NLS-1$
                            ", name = " + f.getName() + ", value = " + value, e); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }
    
    
    
    /**
     * Gets the locale for which polly has been configured in <tt>polly.cfg</tt>
     * @return The polly locale.
     */
    public static Locale getLocale() {
        synchronized (MUTEX) {
            if (pollyLocale == null) {
                return Locale.getDefault();
            }
            return pollyLocale;
        }
    }
}
