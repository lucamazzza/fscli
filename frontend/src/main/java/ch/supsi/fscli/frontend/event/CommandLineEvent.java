package ch.supsi.fscli.frontend.event;

public record CommandLineEvent(boolean successful, String currentDir, String output, String outputError) implements Event {}
