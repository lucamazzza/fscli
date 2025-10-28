package backend.data;

import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FSNode;
import ch.supsi.fscli.backend.data.FileNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DirectoryNodeTest {
    DirectoryNode d;

    @BeforeEach
    void setUp() {
        d = new DirectoryNode();
    }

    @AfterEach
    void tearDown() {
        d = null;
    }

    @Test
    void addRejectsNullOrEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> d.add(null, new FileNode()));
        assertThrows(IllegalArgumentException.class, () -> d.add("", new FileNode()));
    }

    @Test
    void addAndGetAndContainsAndParent() {
        FileNode f = new FileNode();
        Instant before = d.getMTime();
        d.add("file1", f);
        assertTrue(d.contains("file1"));
        assertSame(f, d.get("file1"));
        assertSame(d, f.getParent());
        assertFalse(d.getMTime().isBefore(before));
    }

    @Test
    void listNamesMaintainsInsertionOrderAndSnapshotIsCopy() {
        d.add("a", new FileNode());
        d.add("b", new FileNode());
        d.add("c", new FileNode());
        List<String> names = d.listNames();
        assertEquals(List.of("a", "b", "c"), names);
        Map<String, FSNode> snap = d.snapshot();
        assertEquals(3, snap.size());
        snap.remove("b");
        assertTrue(d.contains("b"));
    }

    @Test
    void removeUnlinksAndUnsetsParentAndUpdatesMTime() {
        FileNode f = new FileNode();
        d.add("x", f);
        Instant before = d.getMTime();
        FSNode removed = d.remove("x");
        FSNode removedNull = d.remove("x");
        assertNull(removedNull);
        assertSame(f, removed);
        assertNull(f.getParent());
        assertFalse(d.contains("x"));
        assertFalse(d.getMTime().isBefore(before));
    }

    @Test
    void isEmpty() {
        assertTrue(d.isEmpty());
        d.add("x", new FileNode());
        assertFalse(d.isEmpty());
    }
}
