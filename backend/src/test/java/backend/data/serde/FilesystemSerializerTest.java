package backend.data.serde;

import ch.supsi.fscli.backend.data.DirectoryNode;
import ch.supsi.fscli.backend.data.FileSystemNode;
import ch.supsi.fscli.backend.data.FileNode;
import ch.supsi.fscli.backend.data.serde.Serializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FilesystemSerializerTest {

    @Test
    void testSerializeFileNode() throws Exception {
        FileNode file = new FileNode();
        
        Serializer<FileSystemNode> serializer = new Serializer<>();
        String json = serializer.serialize(file);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        assertEquals("file", node.get("nodeType").asText());
        assertTrue(node.has("id"));
        assertTrue(node.has("linkCount"));
        assertTrue(node.has("ctime"));
        assertTrue(node.has("mtime"));
        assertTrue(node.has("atime"));
        assertEquals(1, node.get("linkCount").asInt());
    }

    @Test
    void testSerializeDirectoryNode() throws Exception {
        DirectoryNode dir = new DirectoryNode();
        FileNode file = new FileNode();
        dir.add("test.txt", file);
        
        Serializer<FileSystemNode> serializer = new Serializer<>();
        String json = serializer.serialize(dir);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        assertEquals("directory", node.get("nodeType").asText());
        assertTrue(node.has("id"));
        assertTrue(node.has("linkCount"));
        assertTrue(node.has("children"));
        assertTrue(node.get("children").has("test.txt"));
    }

    @Test
    void testSerializeDirectoryWithMultipleChildren() throws Exception {
        DirectoryNode root = new DirectoryNode();
        root.add("file1.txt", new FileNode());
        root.add("file2.txt", new FileNode());
        
        DirectoryNode subdir = new DirectoryNode();
        subdir.add("file3.txt", new FileNode());
        root.add("subdir", subdir);
        
        Serializer<FileSystemNode> serializer = new Serializer<>();
        String json = serializer.serialize(root);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);

        assertEquals("directory", node.get("nodeType").asText());
        JsonNode children = node.get("children");
        assertTrue(children.has("file1.txt"));
        assertTrue(children.has("file2.txt"));
        assertTrue(children.has("subdir"));
        assertEquals("directory", children.get("subdir").get("nodeType").asText());
    }
}
