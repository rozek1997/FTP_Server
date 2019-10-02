package com.goldenore.fileserver.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

    void store(MultipartFile file, String username) throws IOException;

    Stream<Path> loadAll(String path) throws IOException;

    Path load(String filename, String username);

}
