package ch.supsi.fscli.backend.di;

import ch.supsi.fscli.backend.core.command.CdCommand;
import ch.supsi.fscli.backend.core.command.Command;
import ch.supsi.fscli.backend.core.command.CpCommand;
import ch.supsi.fscli.backend.core.command.FortuneCommand;
import ch.supsi.fscli.backend.core.command.LnCommand;
import ch.supsi.fscli.backend.core.command.LsCommand;
import ch.supsi.fscli.backend.core.command.MkdirCommand;
import ch.supsi.fscli.backend.core.command.MvCommand;
import ch.supsi.fscli.backend.core.command.PwdCommand;
import ch.supsi.fscli.backend.core.command.RmCommand;
import ch.supsi.fscli.backend.core.command.RmdirCommand;
import ch.supsi.fscli.backend.core.command.TouchCommand;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Guice module for filesystem command registration.
 * Uses Multibinder to register all available commands.
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
        commandBinder.addBinding().to(FortuneCommand.class);
    }
}
