package ch.supsi.fscli.backend.di;

import ch.supsi.fscli.backend.core.command.*;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Guice module for Command registration.
 * Uses Multibinder to auto-discover and register all commands.
 */
public class CommandModule extends AbstractModule {
    
    @Override
    protected void configure() {
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
}
