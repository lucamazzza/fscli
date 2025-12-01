package backend.data;

import ch.supsi.fscli.backend.data.LinkNode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkNodeTest {
    LinkNode s;

    @BeforeEach
    void setUp() {
        s = new LinkNode("Target");
    }

    @AfterEach
    void tearDown() {
        s = null;
    }

    @Test
    void getSetTarget() {
        assertEquals("Target", s.getTarget());
        s.setTarget("target");
        assertEquals("target", s.getTarget());
    }

    @Test
    void toStringTest() {
        assertEquals(String.format("symlink (id=%d -> \"Target\")", s.getId()), s.toString());
        s.setTarget("target");
        assertEquals(String.format("symlink (id=%d -> \"target\")", s.getId()), s.toString());
    }
}
