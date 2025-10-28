package backend.data;

import ch.supsi.fscli.backend.data.FileNode;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class FSNodeTest {
    @Test
    void idGenerationIncrementsTest() {
        FileNode a = new FileNode();
        FileNode b = new FileNode();
        assertTrue(b.getId() > a.getId(), "IDs should be monotonic increasing");
    }

    @Test
    void linkCountInvariants() {
        FileNode f = new FileNode();
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
    void touchUpdatesTimes() {
        FileNode f = new FileNode();
        Instant prevC = f.getCTime();
        Instant prevM = f.getMTime();
        f.touch();
        assertFalse(f.getCTime().isBefore(prevC));
        assertFalse(f.getMTime().isBefore(prevM));
    }

    @Test
    void toStringIncludesTypeAndIdAndLinks() {
        FileNode f = new FileNode();
        String s = f.toString();
        assertTrue(s.contains("file"));
        assertTrue(s.contains("id="));
        assertTrue(s.contains("links="));
    }
}
