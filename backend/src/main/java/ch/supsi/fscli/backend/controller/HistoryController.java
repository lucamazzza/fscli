package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.controller.dto.CommandHistoryDTO;
import ch.supsi.fscli.backend.service.CommandHistoryEntry;
import ch.supsi.fscli.backend.service.FileSystemService;
import com.google.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller specialized in command history management.
 * Part of the Facade pattern implementation.
 * 
 * <p>This controller is responsible for:</p>
 * <ul>
 *   <li>Retrieving command history</li>
 *   <li>Searching through history</li>
 *   <li>Clearing history</li>
 *   <li>Converting history entries to DTO format</li>
 * </ul>
 * 
 * <p>Delegates all business logic to {@link FileSystemService}.</p>
 * 
 * @see FileSystemController
 * @see FileSystemService
 * @see CommandHistoryDTO
 */
public class HistoryController {
    /** The service layer that manages command history */
    private final FileSystemService service;
    
    /**
     * Constructs a new HistoryController with injected service.
     * 
     * @param service The service layer to delegate operations to
     */
    @Inject
    public HistoryController(FileSystemService service) {
        this.service = service;
    }
    
    /**
     * Retrieves the complete command history.
     * 
     * @return List of command history DTOs, ordered by execution time
     */
    public List<CommandHistoryDTO> getHistory() {
        return service.getHistory().stream()
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves the last N commands from history.
     * 
     * @param count Number of commands to retrieve (most recent)
     * @return List of last N command history DTOs
     */
    public List<CommandHistoryDTO> getLastCommands(int count) {
        return service.getLastCommands(count).stream()
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Searches command history for entries matching a pattern.
     * Search is case-insensitive and matches command strings.
     * 
     * @param pattern Search pattern (e.g., "ls" will find all ls commands)
     * @return List of matching command history DTOs
     */
    public List<CommandHistoryDTO> searchHistory(String pattern) {
        return service.searchHistory(pattern).stream()
                .map(this::convertHistoryToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Clears the entire command history.
     * This operation cannot be undone.
     */
    public void clearHistory() {
        service.clearHistory();
    }
    
    /**
     * Retrieves command history as simple string list.
     * Returns only the command strings without metadata.
     * 
     * @return List of command strings in execution order
     */
    public List<String> getHistoryCommands() {
        return service.getHistoryCommands();
    }
    
    /**
     * Converts internal history entry to DTO format for API consumption.
     * 
     * @param entry Internal command history entry
     * @return DTO formatted history entry with all metadata
     */
    private CommandHistoryDTO convertHistoryToDTO(CommandHistoryEntry entry) {
        return new CommandHistoryDTO(
                entry.getCommand(),
                entry.isSuccessful(),
                entry.getTimestamp()
        );
    }
}
