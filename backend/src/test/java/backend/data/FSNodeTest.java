package backend.data;

import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileNode;
import ch.supsi.fscli.backend.data.LinkNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class FSNodeTest {
    FileNode f;

    @BeforeEach
    void setUp() {
        f = new FileNode();
    }

    @AfterEach
    void tearDown() {
        f = null;
    }

    @Test
    void idGenerationIncrementsTest() {
        FileNode a = new FileNode();
        FileNode b = new FileNode();
        assertTrue(b.getId() > a.getId(), "IDs should be monotonic increasing");
    }

    @Test
    void linkCountInvariants() {
        int initial = f.getLinkCount();
        f.incrementLinkCount();
        assertEquals(initial + 1, f.getLinkCount());
        Instant before = f.getCTime();
        f.decrementLinkCount();
        assertEquals(initial, f.getLinkCount());
        for (int i = 0; i < 10; i++) f.decrementLinkCount();
        assertTrue(f.getLinkCount() >= 0);
        assertFalse(f.getCTime().isBefore(before));
    }

    @Test
    void addedTimes() {
        Instant before = f.getCTime();
        DirectoryNode d = new DirectoryNode();
        d.add("File", f);
        assertTrue(f.getATime().isAfter(before));
    }

    @Test
    void touchUpdatesTimes() {
        Instant prevC = f.getCTime();
        Instant prevM = f.getMTime();
        f.touch();
        assertFalse(f.getCTime().isBefore(prevC));
        assertFalse(f.getMTime().isBefore(prevM));
    }

    @Test
    void toStringIncludesTypeAndIdAndLinks() {
        String s = f.toString();
        assertTrue(s.contains("file"));
        assertTrue(s.contains("id="));
        assertTrue(s.contains("links="));
    }

    @Test
    void nodeIdentity() {
        DirectoryNode d = new DirectoryNode();
        assertTrue(d.isDirectory());
        assertFalse(d.isLink());
        FileNode f = new FileNode();
        assertFalse(f.isDirectory());
        assertFalse(f.isLink());
        LinkNode s = new LinkNode("->target");
        assertTrue(s.isLink());
        assertFalse(s.isDirectory());
        assertEquals("directory", d.typeName());
        assertEquals("file", f.typeName());
        assertEquals("symlink", s.typeName());
    }
}
