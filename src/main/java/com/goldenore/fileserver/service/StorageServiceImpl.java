package com.goldenore.fileserver.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
public class StorageServiceImpl implements StorageService {

    @Value("${file.path}")
    private String filesPath;


    @Override
    public void store(MultipartFile file, String username) throws IOException {

        String uploadDest = new StringBuilder()
                .append(filesPath)
                .append(FileSystems.getDefault().getSeparator())
                .append(username)
                .append(FileSystems.getDefault().getSeparator())
                .append(file.getOriginalFilename())
                .toString();

        Files.write(Paths.get(uploadDest), file.getBytes());
    }

    @Override
    public Stream<Path> loadAll(String path) throws IOException {

        Path start = Paths.get(filesPath + "/" + path);
        Stream<Path> stream = Files.walk(start, Integer.MAX_VALUE);

        return stream;
    }

    @Override
    public Path load(String filename, String username) {
        String downloadDest = new StringBuilder()
                .append(filesPath)
                .append("/")
                .append(username)
                .append("/")
                .append(filename)
                .toString();

        return Paths.get(downloadDest);

    }


}


