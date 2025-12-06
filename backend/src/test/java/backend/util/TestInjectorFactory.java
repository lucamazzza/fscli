package backend.util;

import ch.supsi.fscli.backend.controller.*;
import ch.supsi.fscli.backend.core.FileSystem;
import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.core.command.*;
import ch.supsi.fscli.backend.data.serde.PreferencesFileManager;
import ch.supsi.fscli.backend.provider.parser.CommandParser;
import ch.supsi.fscli.backend.provider.resolver.PathResolver;
import ch.supsi.fscli.backend.service.FileSystemPersistenceService;
import ch.supsi.fscli.backend.service.FileSystemService;
import ch.supsi.fscli.backend.service.PreferencesService;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Factory for creating test injectors with proper dependencies.
 */
public class TestInjectorFactory {
    
    /**
     * Creates a test injector with all necessary bindings.
     */
    public static Injector createTestInjector() {
        return Guice.createInjector(new TestModule());
    }
    
    private static class TestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Bind services
            bind(FileSystemService.class).in(Singleton.class);
            bind(FileSystemPersistenceService.class);
            
            // Bind controllers
            bind(CommandExecutionController.class);
            bind(HistoryController.class);
            bind(FileSystemPersistenceController.class);
            
            // Bind providers
            bind(CommandParser.class);
            bind(PathResolver.class).toProvider(() -> PathResolver.getInstance()).in(Singleton.class);
            
            // Bind all commands
            Multibinder<Command> commandBinder = Multibinder.newSetBinder(binder(), Command.class);
            commandBinder.addBinding().to(CdCommand.class);
            commandBinder.addBinding().to(CpCommand.class);
            commandBinder.addBinding().to(LnCommand.class);
            commandBinder.addBinding().to(LsCommand.class);
            commandBinder.addBinding().to(MkdirCommand.class);
            commandBinder.addBinding().to(MvCommand.class);
            commandBinder.addBinding().to(PwdCommand.class);
            commandBinder.addBinding().to(RmCommand.class);
            commandBinder.addBinding().to(RmdirCommand.class);
            commandBinder.addBinding().to(TouchCommand.class);
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
        @Singleton
        PreferencesService providePreferencesService() {
            try {
                // Create a temporary preferences file for tests
                Path tempPrefsFile = Files.createTempFile("test_prefs_", ".json");
                tempPrefsFile.toFile().deleteOnExit();
                PreferencesFileManager fileManager = new PreferencesFileManager(tempPrefsFile);
                return new PreferencesService(fileManager);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create temporary preferences file for test", e);
            }
        }
        
        @Provides
        PreferencesController providePreferencesController(PreferencesService preferencesService) {
            return new PreferencesController(preferencesService);
        }
    }
}
