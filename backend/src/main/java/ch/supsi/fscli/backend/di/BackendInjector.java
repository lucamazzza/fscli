package ch.supsi.fscli.backend.di;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Centralized Guice injector for the backend.
 * Provides access to dependency injection throughout the application.
 */
public class BackendInjector {
    private static Injector injector;
    
    private BackendInjector() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initializes the Guice injector with all backend modules.
     * Should be called once at application startup.
     */
    public static void initialize() {
        if (injector == null) {
            injector = Guice.createInjector(
                new BackendModule(),
                new CommandModule()
            );
        }
    }
    
    /**
     * Gets the Guice injector instance.
     * @return The initialized injector
     * @throws IllegalStateException if initialize() hasn't been called
     */
    public static Injector getInjector() {
        if (injector == null) {
            throw new IllegalStateException("BackendInjector not initialized. Call initialize() first.");
        }
        return injector;
    }
    
    /**
     * Convenience method to get an instance of a class.
     * @param type The class type to get
     * @return An instance of the requested type
     */
    public static <T> T getInstance(Class<T> type) {
        return getInjector().getInstance(type);
    }
}
