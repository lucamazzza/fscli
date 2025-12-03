package ch.supsi.fscli.backend.di;

import ch.supsi.fscli.backend.controller.*;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.provider.parser.CommandParser;
import ch.supsi.fscli.backend.provider.resolver.PathResolver;
import ch.supsi.fscli.backend.service.FileSystemPersistenceService;
import ch.supsi.fscli.backend.service.FileSystemService;
import ch.supsi.fscli.backend.service.PreferencesService;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Guice module for backend services, controllers, and core components.
 */
public class BackendModule extends AbstractModule {
    
    @Override
    protected void configure() {
        // Bind services
        bind(FileSystemService.class).in(Singleton.class);
        bind(PreferencesService.class).in(Singleton.class);
        bind(FileSystemPersistenceService.class);
        
        // Bind controllers
        bind(CommandExecutionController.class);
        bind(HistoryController.class);
        bind(FileSystemPersistenceController.class);
        
        // Bind providers
        bind(CommandParser.class);
        bind(PathResolver.class).toProvider(() -> PathResolver.getInstance()).in(Singleton.class);
    }
    
    @Provides
    @Singleton
    FileSystem provideFileSystem() {
        return new InMemoryFileSystem();
    }
    
    @Provides
    FileSystemController provideFileSystemController(
            FileSystemService service,
            CommandExecutionController commandExecutionController,
            HistoryController historyController,
            FileSystem fileSystem) {
        return new FileSystemController(service, commandExecutionController, historyController, fileSystem);
    }
    
    @Provides
    PreferencesController providePreferencesController(PreferencesService preferencesService) {
        return new PreferencesController(preferencesService);
    }
}
