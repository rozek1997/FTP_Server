package com.goldenore.fileserver.service;

import com.goldenore.fileserver.pojo.FileAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Component
public class StorageServiceImpl implements StorageService {

    @Value("${file.path}")
    private String filesPath;



    @Override
    public void store(MultipartFile file, String path, String username) throws IOException {

        String uploadDest = new StringBuilder()
                .append(filesPath)
                .append(File.separator)
                .append(username)
                .append(File.separator)
                .append(path)
                .append(File.separator)
                .append(file.getOriginalFilename())
                .toString();

        Files.write(Paths.get(uploadDest), file.getBytes());
    }

    @Override
    public Stream<FileAddress> loadAll(String path, String username) throws IOException {

        Path start = Paths.get(filesPath + File.separator + username);
        Path searchPath = Paths.get(start.toString() + File.separator + path);

        Stream<FileAddress> stream = Files.list(searchPath)
                .map(temp -> {
                    FileAddress fileAddress = new FileAddress();
                    fileAddress.setFileName(temp.getFileName().toString());
                    fileAddress.setRelativeAdress(start.relativize(temp).toString());
                    fileAddress.setDirectory(temp.toFile().isDirectory());
                    fileAddress.setReadable(temp.toFile().canRead());
                    return fileAddress;
                });

        return stream;
    }

    @Override
    public Stream<FileAddress> loadDirectoryNavigation(String path, String username) {

        Path start = Paths.get(filesPath + File.separator + username);
        Path searchPath = Paths.get(start.toString() + File.separator + path);

        System.out.println(start.relativize(searchPath).toString());
        FileAddress currentAddress = new FileAddress();
        currentAddress.setRelativeAdress(start.relativize(searchPath).toString());
        currentAddress.setDirectory(searchPath.toFile().isDirectory());
        currentAddress.setReadable(searchPath.toFile().canRead());

        FileAddress rootAddress = new FileAddress();
        rootAddress.setRelativeAdress(start.relativize(searchPath.getParent()).toString());
        rootAddress.setDirectory(searchPath.toFile().isDirectory());
        rootAddress.setReadable(searchPath.toFile().canRead());


        Stream<FileAddress> stream =
                Stream.of(currentAddress, rootAddress);

        return stream;
    }

    @Override
    public Path loadFile(String filename, String username) {
        String downloadDest = new StringBuilder()
                .append(filesPath)
                .append("/")
                .append(username)
                .append("/")
                .append(filename)
                .toString();

        return Paths.get(downloadDest);

    }

    @Override
    public void createFolder(String path, String dirName, String username) throws IOException {

        StringBuilder newDirPathString = new StringBuilder()
                .append(filesPath)
                .append(File.separator)
                .append(username)
                .append(File.separator)
                .append(path)
                .append(File.separator)
                .append(dirName);

        Path newDirPath = Paths.get(newDirPathString.toString());
        Files.createDirectories(newDirPath);


    }
}


