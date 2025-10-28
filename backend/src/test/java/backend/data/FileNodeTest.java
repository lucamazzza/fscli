package backend.data;

import ch.supsi.fscli.backend.data.FileNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileNodeTest {
    FileNode f;

    @BeforeEach
     void setUp() {
        f = new FileNode();
    }

    @AfterEach
    void tearDown() {
        f = null;
    }
}
