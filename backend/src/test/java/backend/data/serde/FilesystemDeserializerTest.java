package backend.data.serde;

import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.serde.Deserializer;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.FileNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FilesystemDeserializerTest {

    @Test
    void testDeserializeFileNode() throws Exception {
        String json = """
                {
                  "nodeType": "file",
                  "id": 1,
                  "linkCount": 1,
                  "ctime": "2025-11-02T23:00:00Z",
                  "mtime": "2025-11-02T23:00:00Z",
                  "atime": "2025-11-02T23:00:00Z"
                }
                """;

        Deserializer<FileSystemNode> deserializer = new Deserializer<>();
        FileSystemNode node = deserializer.deserialize(json, FileSystemNode.class);

        assertNotNull(node);
        assertInstanceOf(FileNode.class, node);
        assertFalse(node.isDirectory());
        assertFalse(node.isLink());
        assertEquals("file", node.typeName());
        assertEquals(1, node.getLinkCount());
    }

    @Test
    void testDeserializeDirectoryNode() throws Exception {
        String json = """
                {
                  "nodeType": "directory",
                  "id": 2,
                  "linkCount": 1,
                  "ctime": "2025-11-02T23:00:00Z",
                  "mtime": "2025-11-02T23:00:00Z",
                  "atime": "2025-11-02T23:00:00Z",
                  "children": {}
                }
                """;

        Deserializer<FileSystemNode> deserializer = new Deserializer<>();
        FileSystemNode node = deserializer.deserialize(json, FileSystemNode.class);

        assertNotNull(node);
        assertInstanceOf(DirectoryNode.class, node);
        assertTrue(node.isDirectory());
        assertFalse(node.isLink());
        assertEquals("directory", node.typeName());
    }

    @Test
    void testDeserializeDirectoryWithChildren() throws Exception {
        String json = """
                {
                  "nodeType": "directory",
                  "id": 3,
                  "linkCount": 1,
                  "ctime": "2025-11-02T23:00:00Z",
                  "mtime": "2025-11-02T23:00:00Z",
                  "atime": "2025-11-02T23:00:00Z",
                  "children": {
                    "file1.txt": {
                      "nodeType": "file",
                      "id": 4,
                      "linkCount": 1,
                      "ctime": "2025-11-02T23:00:00Z",
                      "mtime": "2025-11-02T23:00:00Z",
                      "atime": "2025-11-02T23:00:00Z"
                    },
                    "subdir": {
                      "nodeType": "directory",
                      "id": 5,
                      "linkCount": 1,
                      "ctime": "2025-11-02T23:00:00Z",
                      "mtime": "2025-11-02T23:00:00Z",
                      "atime": "2025-11-02T23:00:00Z",
                      "children": {}
                    }
                  }
                }
                """;

        Deserializer<FileSystemNode> deserializer = new Deserializer<>();
        FileSystemNode node = deserializer.deserialize(json, FileSystemNode.class);

        assertNotNull(node);
        assertInstanceOf(DirectoryNode.class, node);
        
        DirectoryNode dir = (DirectoryNode) node;
        assertTrue(dir.contains("file1.txt"));
        assertTrue(dir.contains("subdir"));
        
        FileSystemNode file = dir.get("file1.txt");
        assertInstanceOf(FileNode.class, file);
        
        FileSystemNode subdir = dir.get("subdir");
        assertInstanceOf(DirectoryNode.class, subdir);
    }
}
