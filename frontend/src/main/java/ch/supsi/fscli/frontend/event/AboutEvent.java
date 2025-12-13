package ch.supsi.fscli.frontend.event;

import java.util.Map;

public record AboutEvent(Map<String, String> appInfo) implements Event{}
