package backend.business;

import ch.supsi.fscli.backend.business.InvalidPathException;
import ch.supsi.fscli.backend.business.NotFoundException;
import ch.supsi.fscli.backend.business.PathResolver;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FSNode;
import ch.supsi.fscli.backend.data.FileNode;
import ch.supsi.fscli.backend.data.SymlinkNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathResolverTest {

    private PathResolver resolver;
    private DirectoryNode root;
    private DirectoryNode home;
    private DirectoryNode user;

    @BeforeEach
    void setUp() {
        resolver = PathResolver.getInstance();
        
        // Create filesystem structure:
        // /
        // ├── home/
        // │   └── user/
        // │       ├── file.txt
        // │       ├── docs/
        // │       │   └── readme.md
        // │       └── link -> /home/user/docs
        // └── tmp/
        //     └── symlink -> /home/user/file.txt
        
        root = new DirectoryNode();
        root.setParent(root);
        
        home = new DirectoryNode();
        root.add("home", home);
        
        user = new DirectoryNode();
        home.add("user", user);
        
        FileNode file = new FileNode();
        user.add("file.txt", file);
        
        DirectoryNode docs = new DirectoryNode();
        user.add("docs", docs);
        
        FileNode readme = new FileNode();
        docs.add("readme.md", readme);
        
        SymlinkNode link = new SymlinkNode("/home/user/docs");
        user.add("link", link);
        
        DirectoryNode tmp = new DirectoryNode();
        root.add("tmp", tmp);
        
        SymlinkNode symlink = new SymlinkNode("/home/user/file.txt");
        tmp.add("symlink", symlink);
    }

    @Test
    void testResolveAbsolutePath() throws Exception {
        FSNode result = resolver.resolve(user, "/home/user/file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveRelativePath() throws Exception {
        FSNode result = resolver.resolve(user, "file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveNestedPath() throws Exception {
        FSNode result = resolver.resolve(user, "docs/readme.md", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveRootPath() throws Exception {
        FSNode result = resolver.resolve(user, "/", false);
        assertNotNull(result);
        assertSame(root, result);
        assertTrue(result.isDirectory());
    }

    @Test
    void testResolveCurrentDirectory() throws Exception {
        FSNode result = resolver.resolve(user, ".", false);
        assertNotNull(result);
        assertSame(user, result);
    }

    @Test
    void testResolveParentDirectory() throws Exception {
        FSNode result = resolver.resolve(user, "..", false);
        assertNotNull(result);
        assertSame(home, result);
    }

    @Test
    void testResolveParentFromRoot() throws Exception {
        FSNode result = resolver.resolve(root, "..", false);
        assertNotNull(result);
        assertSame(root, result);
    }

    @Test
    void testResolveWithMultipleParents() throws Exception {
        DirectoryNode docs = (DirectoryNode) user.get("docs");
        FSNode result = resolver.resolve(docs, "../../..", false);
        assertNotNull(result);
        assertSame(root, result);
    }

    @Test
    void testResolvePathWithDots() throws Exception {
        FSNode result = resolver.resolve(user, "./docs/./readme.md", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveSymlinkWithFollowTrue() throws Exception {
        FSNode result = resolver.resolve(user, "link", true);
        assertNotNull(result);
        assertInstanceOf(DirectoryNode.class, result);
        DirectoryNode dir = (DirectoryNode) result;
        assertTrue(dir.contains("readme.md"));
    }

    @Test
    void testResolveSymlinkWithFollowFalse() throws Exception {
        FSNode result = resolver.resolve(user, "link", false);
        assertNotNull(result);
        assertInstanceOf(SymlinkNode.class, result);
    }

    @Test
    void testResolveSymlinkInMiddleOfPath() throws Exception {
        FSNode result = resolver.resolve(user, "link/readme.md", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveAbsoluteSymlink() throws Exception {
        FSNode result = resolver.resolve(root, "/tmp/symlink", true);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolvePathWithTrailingSlash() throws Exception {
        FSNode result = resolver.resolve(user, "docs/", false);
        assertNotNull(result);
        assertInstanceOf(DirectoryNode.class, result);
    }

    @Test
    void testResolveEmptyPathThrowsException() {
        assertThrows(NotFoundException.class, () -> {
            resolver.resolve(user, "", false);
        });
    }

    @Test
    void testResolveNullPathThrowsException() {
        assertThrows(NotFoundException.class, () -> {
            resolver.resolve(user, null, false);
        });
    }

    @Test
    void testResolveWithNullCwdThrowsException() {
        assertThrows(InvalidPathException.class, () -> {
            resolver.resolve(null, "/home", false);
        });
    }

    @Test
    void testResolveNonExistentPathThrowsException() {
        assertThrows(NotFoundException.class, () -> {
            resolver.resolve(user, "nonexistent", false);
        });
    }

    @Test
    void testResolveNonExistentNestedPathThrowsException() {
        assertThrows(NotFoundException.class, () -> {
            resolver.resolve(user, "docs/nonexistent.txt", false);
        });
    }

    @Test
    void testResolveCircularSymlinkThrowsException() {
        SymlinkNode link1 = new SymlinkNode("/home/user/link2");
        user.add("link1", link1);
        
        SymlinkNode link2 = new SymlinkNode("/home/user/link1");
        user.add("link2", link2);
        
        assertThrows(InvalidPathException.class, () -> {
            resolver.resolve(user, "link1", true);
        });
    }

    @Test
    void testResolveTooManySymlinkLevelsThrowsException() {
        DirectoryNode current = user;
        String previousLink = "/home/user/file.txt";
        
        for (int i = 0; i < 35; i++) {
            SymlinkNode link = new SymlinkNode(previousLink);
            current.add("link" + i, link);
            previousLink = "/home/user/link" + i;
        }
        
        assertThrows(InvalidPathException.class, () -> {
            resolver.resolve(user, "link34", true);
        });
    }

    @Test
    void testResolveFromDifferentWorkingDirectory() throws Exception {
        DirectoryNode docs = (DirectoryNode) user.get("docs");
        FSNode result = resolver.resolve(docs, "/home/user/file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveComplexPath() throws Exception {
        DirectoryNode docs = (DirectoryNode) user.get("docs");
        FSNode result = resolver.resolve(docs, "./../docs/../file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveSymlinkToSymlink() throws Exception {
        SymlinkNode link1 = new SymlinkNode("/tmp/symlink");
        user.add("link_to_link", link1);
        
        FSNode result = resolver.resolve(user, "link_to_link", true);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveMultipleSlashes() throws Exception {
        FSNode result = resolver.resolve(user, "//home//user//file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testGetInstanceReturnsSameInstance() {
        PathResolver instance1 = PathResolver.getInstance();
        PathResolver instance2 = PathResolver.getInstance();
        assertSame(instance1, instance2);
    }
}
