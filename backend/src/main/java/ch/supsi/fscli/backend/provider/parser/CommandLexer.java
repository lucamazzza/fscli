package ch.supsi.fscli.backend.provider.parser;

import ch.supsi.fscli.backend.core.exception.InvalidCommandException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CommandLexer {

    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("messages", Locale.getDefault());

    public List<String> tokenize(String input) throws InvalidCommandException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidCommandException(MESSAGES.getString("commandEmpty"));
        }
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean escaped = false;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (escaped) {
                current.append(c);
                escaped = false;
                continue;
            }
            if (c == '\\') {
                escaped = true;
                continue;
            }
            if (c == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
                continue;
            }
            if (c == '"' && !inSingleQuote) {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }
            if (Character.isWhitespace(c) && !inSingleQuote && !inDoubleQuote) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current = new StringBuilder();
                }
                continue;
            }
            current.append(c);
        }
        if (inSingleQuote || inDoubleQuote) {
            throw new InvalidCommandException(MESSAGES.getString("unclosedQuote"));
        }
        if (escaped) {
            throw new InvalidCommandException(MESSAGES.getString("trailingEscape"));
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        if (tokens.isEmpty()) {
            throw new InvalidCommandException(MESSAGES.getString("commandEmpty"));
        }
        return tokens;
    }

    public boolean validateCommandName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return name.matches("[a-zA-Z][a-zA-Z0-9_-]*");
    }
}
