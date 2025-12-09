package ch.supsi.fscli.backend.provider.parser;

import ch.supsi.fscli.backend.core.exception.InvalidCommandException;
import ch.supsi.fscli.backend.i18n.BackendMessageProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizes command strings with support for quotes and escaping.
 */
public class CommandLexer {


    public List<String> tokenize(String input) throws InvalidCommandException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidCommandException(BackendMessageProvider.get("commandEmpty"));
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
            throw new InvalidCommandException(BackendMessageProvider.get("unclosedQuote"));
        }
        if (escaped) {
            throw new InvalidCommandException(BackendMessageProvider.get("trailingEscape"));
        }
        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }
        if (tokens.isEmpty()) {
            throw new InvalidCommandException(BackendMessageProvider.get("commandEmpty"));
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
