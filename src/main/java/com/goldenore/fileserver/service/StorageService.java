package com.goldenore.fileserver.service;

import com.goldenore.fileserver.pojo.FileAddress;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void store(MultipartFile file, String path, String username) throws IOException;

    Stream<FileAddress> loadAll(String path, String username) throws IOException;


    Stream<FileAddress> loadDirectoryNavigation(String path, String username);

    Path loadFile(String filename, String username);

    void createFolder(String path, String dirName, String username) throws IOException;

}
