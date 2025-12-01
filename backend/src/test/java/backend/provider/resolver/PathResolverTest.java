package backend.provider.resolver;

import ch.supsi.fscli.backend.core.exception.InvalidPathException;
import ch.supsi.fscli.backend.core.exception.NotFoundException;
import ch.supsi.fscli.backend.data.LinkNode;
import ch.supsi.fscli.backend.provider.resolver.PathResolver;
import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.FileNode;
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
        
        LinkNode link = new LinkNode("/home/user/docs");
        user.add("link", link);
        
        DirectoryNode tmp = new DirectoryNode();
        root.add("tmp", tmp);
        
        LinkNode symlink = new LinkNode("/home/user/file.txt");
        tmp.add("symlink", symlink);
    }

    @Test
    void testResolveAbsolutePath() throws Exception {
        FileSystemNode result = resolver.resolve(user, "/home/user/file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveRelativePath() throws Exception {
        FileSystemNode result = resolver.resolve(user, "file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveNestedPath() throws Exception {
        FileSystemNode result = resolver.resolve(user, "docs/readme.md", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveRootPath() throws Exception {
        FileSystemNode result = resolver.resolve(user, "/", false);
        assertNotNull(result);
        assertSame(root, result);
        assertTrue(result.isDirectory());
    }

    @Test
    void testResolveCurrentDirectory() throws Exception {
        FileSystemNode result = resolver.resolve(user, ".", false);
        assertNotNull(result);
        assertSame(user, result);
    }

    @Test
    void testResolveParentDirectory() throws Exception {
        FileSystemNode result = resolver.resolve(user, "..", false);
        assertNotNull(result);
        assertSame(home, result);
    }

    @Test
    void testResolveParentFromRoot() throws Exception {
        FileSystemNode result = resolver.resolve(root, "..", false);
        assertNotNull(result);
        assertSame(root, result);
    }

    @Test
    void testResolveWithMultipleParents() throws Exception {
        DirectoryNode docs = (DirectoryNode) user.get("docs");
        FileSystemNode result = resolver.resolve(docs, "../../..", false);
        assertNotNull(result);
        assertSame(root, result);
    }

    @Test
    void testResolvePathWithDots() throws Exception {
        FileSystemNode result = resolver.resolve(user, "./docs/./readme.md", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveSymlinkWithFollowTrue() throws Exception {
        FileSystemNode result = resolver.resolve(user, "link", true);
        assertNotNull(result);
        assertInstanceOf(DirectoryNode.class, result);
        DirectoryNode dir = (DirectoryNode) result;
        assertTrue(dir.contains("readme.md"));
    }

    @Test
    void testResolveSymlinkWithFollowFalse() throws Exception {
        FileSystemNode result = resolver.resolve(user, "link", false);
        assertNotNull(result);
        assertInstanceOf(LinkNode.class, result);
    }

    @Test
    void testResolveSymlinkInMiddleOfPath() throws Exception {
        FileSystemNode result = resolver.resolve(user, "link/readme.md", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveAbsoluteSymlink() throws Exception {
        FileSystemNode result = resolver.resolve(root, "/tmp/symlink", true);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolvePathWithTrailingSlash() throws Exception {
        FileSystemNode result = resolver.resolve(user, "docs/", false);
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
        LinkNode link1 = new LinkNode("/home/user/link2");
        user.add("link1", link1);
        
        LinkNode link2 = new LinkNode("/home/user/link1");
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
            LinkNode link = new LinkNode(previousLink);
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
        FileSystemNode result = resolver.resolve(docs, "/home/user/file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveComplexPath() throws Exception {
        DirectoryNode docs = (DirectoryNode) user.get("docs");
        FileSystemNode result = resolver.resolve(docs, "./../docs/../file.txt", false);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveSymlinkToSymlink() throws Exception {
        LinkNode link1 = new LinkNode("/tmp/symlink");
        user.add("link_to_link", link1);
        
        FileSystemNode result = resolver.resolve(user, "link_to_link", true);
        assertNotNull(result);
        assertInstanceOf(FileNode.class, result);
    }

    @Test
    void testResolveMultipleSlashes() throws Exception {
        FileSystemNode result = resolver.resolve(user, "//home//user//file.txt", false);
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
