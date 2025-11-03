package ch.supsi.fscli.backend.service;

import ch.supsi.fscli.backend.data.DirectoryNode;

import java.util.List;

public interface FileSystem {
    void mkdir(String path) throws FSException;
    void rmdir(String path) throws FSException;
    void touch(String path)  throws FSException;
    void rm(String path)  throws FSException;
    void mv(String src, String dest) throws FSException;
    void ln(String target, String link, boolean sym) throws FSException;
    List<String> ls(String path, boolean showI) throws FSException;
    void cd(String path) throws FSException;
    String pwd();
    List<String> expWildcard(String path, DirectoryNode curDir) throws FSException;
    DirectoryNode getRoot();
    DirectoryNode getCwd();
}
