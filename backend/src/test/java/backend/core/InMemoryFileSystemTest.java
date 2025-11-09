package backend.core;

import ch.supsi.fscli.backend.core.InMemoryFileSystem;
import ch.supsi.fscli.backend.core.exception.*;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFileSystemTest {

    private InMemoryFileSystem fs;

    @BeforeEach
    void setUp() {
        fs = new InMemoryFileSystem();
    }

    @Test
    void testInitialPwd() {
        assertEquals("/", fs.pwd());
    }

    @Test
    void testGetRoot() {
        DirectoryNode root = fs.getRoot();
        assertNotNull(root);
    }

    @Test
    void testGetCwd() {
        DirectoryNode cwd = fs.getCwd();
        assertNotNull(cwd);
        assertEquals(fs.getRoot(), cwd);
    }

    @Test
    void testMkdir() throws FSException {
        fs.mkdir("test");
        List<String> files = fs.ls(".", false);
        assertTrue(files.contains("test/"));
    }

    @Test
    void testMkdirNested() throws FSException {
        fs.mkdir("dir1");
        fs.cd("dir1");
        fs.mkdir("dir2");
        
        List<String> files = fs.ls(".", false);
        assertTrue(files.contains("dir2/"));
    }

    @Test
    void testMkdirAlreadyExists() throws FSException {
        fs.mkdir("test");
        assertThrows(AlreadyExistsException.class, () -> fs.mkdir("test"));
    }

    @Test
    void testMkdirEmptyPath() {
        assertThrows(InvalidPathException.class, () -> fs.mkdir(""));
    }

    @Test
    void testMkdirNullPath() {
        assertThrows(InvalidPathException.class, () -> fs.mkdir(null));
    }

    @Test
    void testRmdir() throws FSException {
        fs.mkdir("test");
        fs.rmdir("test");
        
        List<String> files = fs.ls(".", false);
        assertFalse(files.stream().anyMatch(f -> f.contains("test")));
    }

    @Test
    void testRmdirNonEmpty() throws FSException {
        fs.mkdir("test");
        fs.cd("test");
        fs.touch("file.txt");
        fs.cd("..");
        
        assertThrows(FSException.class, () -> fs.rmdir("test"));
    }

    @Test
    void testRmdirRoot() {
        assertThrows(FSException.class, () -> fs.rmdir("/"));
    }

    @Test
    void testTouch() throws FSException {
        fs.touch("file.txt");
        List<String> files = fs.ls(".", false);
        assertTrue(files.contains("file.txt"));
    }

    @Test
    void testTouchExisting() throws FSException {
        fs.touch("file.txt");
        assertDoesNotThrow(() -> fs.touch("file.txt"));
    }

    @Test
    void testRm() throws FSException {
        fs.touch("file.txt");
        fs.rm("file.txt");
        
        List<String> files = fs.ls(".", false);
        assertFalse(files.contains("file.txt"));
    }

    @Test
    void testRmDirectory() throws FSException {
        fs.mkdir("test");
        assertThrows(FSException.class, () -> fs.rm("test"));
    }

    @Test
    void testMv() throws FSException {
        fs.touch("old.txt");
        fs.mv("old.txt", "new.txt");
        
        List<String> files = fs.ls(".", false);
        assertFalse(files.contains("old.txt"));
        assertTrue(files.contains("new.txt"));
    }

    @Test
    void testMvDestExists() throws FSException {
        fs.touch("file1.txt");
        fs.touch("file2.txt");
        
        assertThrows(AlreadyExistsException.class, () -> fs.mv("file1.txt", "file2.txt"));
    }

    @Test
    void testLnSymbolic() throws FSException {
        fs.touch("target.txt");
        fs.ln("target.txt", "link.txt", true);
        
        List<String> files = fs.ls(".", false);
        assertTrue(files.stream().anyMatch(f -> f.contains("link.txt")));
    }

    @Test
    void testLnAlreadyExists() throws FSException {
        fs.touch("target.txt");
        fs.touch("link.txt");
        
        assertThrows(AlreadyExistsException.class, () -> fs.ln("target.txt", "link.txt", true));
    }

    @Test
    void testLs() throws FSException {
        fs.mkdir("dir1");
        fs.touch("file1.txt");
        fs.touch("file2.txt");
        
        List<String> files = fs.ls(".", false);
        
        assertEquals(3, files.size());
        assertTrue(files.contains("dir1/"));
        assertTrue(files.contains("file1.txt"));
        assertTrue(files.contains("file2.txt"));
    }

    @Test
    void testLsWithInodes() throws FSException {
        fs.touch("file.txt");
        
        List<String> files = fs.ls(".", true);
        
        assertFalse(files.isEmpty());
        assertTrue(files.get(0).matches("\\d+ .*"));
    }

    @Test
    void testLsFile() throws FSException {
        fs.touch("file.txt");
        
        List<String> result = fs.ls("file.txt", false);
        
        assertEquals(1, result.size());
        assertEquals("file.txt", result.get(0));
    }

    @Test
    void testCd() throws FSException {
        fs.mkdir("test");
        fs.cd("test");
        
        assertEquals("/test", fs.pwd());
    }

    @Test
    void testCdParent() throws FSException {
        fs.mkdir("test");
        fs.cd("test");
        fs.cd("..");
        
        assertEquals("/", fs.pwd());
    }

    @Test
    void testCdAbsolute() throws FSException {
        fs.mkdir("dir1");
        fs.mkdir("dir2");
        fs.cd("dir1");
        fs.cd("/dir2");
        
        assertEquals("/dir2", fs.pwd());
    }

    @Test
    void testCdNotADirectory() throws FSException {
        fs.touch("file.txt");
        assertThrows(NotADirectoryException.class, () -> fs.cd("file.txt"));
    }

    @Test
    void testPwd() {
        String pwd = fs.pwd();
        assertEquals("/", pwd);
    }

    @Test
    void testExpWildcard() throws FSException {
        List<String> result = fs.expWildcard("*.txt", fs.getCwd());
        assertNotNull(result);
    }

    @Test
    void testResolveNode() throws FSException {
        fs.mkdir("test");
        FileSystemNode node = fs.resolveNode("test", false);
        
        assertNotNull(node);
        assertTrue(node.isDirectory());
    }

    @Test
    void testCreateNode() throws FSException {
        DirectoryNode dir = new DirectoryNode();
        fs.createNode("newdir", dir);
        
        List<String> files = fs.ls(".", false);
        assertTrue(files.contains("newdir/"));
    }

    @Test
    void testCreateNodeAlreadyExists() throws FSException {
        fs.mkdir("test");
        DirectoryNode dir = new DirectoryNode();
        
        assertThrows(AlreadyExistsException.class, () -> fs.createNode("test", dir));
    }

    @Test
    void testDeleteNode() throws FSException {
        fs.touch("file.txt");
        fs.deleteNode("file.txt");
        
        List<String> files = fs.ls(".", false);
        assertFalse(files.contains("file.txt"));
    }

    @Test
    void testDeleteNodeRoot() {
        assertThrows(FSException.class, () -> fs.deleteNode("/"));
    }

    @Test
    void testGetParentDirectory() throws FSException {
        fs.mkdir("test");
        DirectoryNode parent = fs.getParentDirectory("test");
        
        assertNotNull(parent);
        assertEquals(fs.getRoot(), parent);
    }

    @Test
    void testListNodes() throws FSException {
        fs.mkdir("dir1");
        fs.touch("file1.txt");
        
        List<FileSystemNode> nodes = fs.listNodes(fs.getCwd());
        
        assertEquals(2, nodes.size());
    }

    @Test
    void testExtractFileName() {
        assertEquals("file.txt", fs.extractFileName("/home/user/file.txt"));
        assertEquals("file.txt", fs.extractFileName("file.txt"));
        assertEquals("/", fs.extractFileName("/"));
    }

    @Test
    void testExtractParentPath() {
        assertEquals("/home/user", fs.extractParentPath("/home/user/file.txt"));
        assertEquals("", fs.extractParentPath("file.txt"));
        assertEquals("", fs.extractParentPath("/"));
    }

    @Test
    void testComplexPathOperations() throws FSException {
        fs.mkdir("a");
        fs.cd("a");
        fs.mkdir("b");
        fs.cd("b");
        fs.mkdir("c");
        
        assertEquals("/a/b", fs.pwd());
        
        fs.cd("c");
        assertEquals("/a/b/c", fs.pwd());
        
        fs.cd("../..");
        assertEquals("/a", fs.pwd());
    }

    @Test
    void testMkdirWithParentPath() throws FSException {
        fs.mkdir("parent");
        fs.mkdir("parent/child");
        
        fs.cd("parent");
        List<String> files = fs.ls(".", false);
        assertTrue(files.contains("child/"));
    }

    @Test
    void testTouchWithParentPath() throws FSException {
        fs.mkdir("parent");
        fs.touch("parent/file.txt");
        
        fs.cd("parent");
        List<String> files = fs.ls(".", false);
        assertTrue(files.contains("file.txt"));
    }

    @Test
    void testRmWithAbsolutePath() throws FSException {
        fs.mkdir("dir");
        fs.touch("dir/file.txt");
        fs.rm("/dir/file.txt");
        
        fs.cd("dir");
        List<String> files = fs.ls(".", false);
        assertFalse(files.contains("file.txt"));
    }
}
