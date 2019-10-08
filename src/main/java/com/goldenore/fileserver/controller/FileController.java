package com.goldenore.fileserver.controller;


import com.goldenore.fileserver.pojo.FileAddress;
import com.goldenore.fileserver.service.StorageFileNotFoundException;
import com.goldenore.fileserver.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/drive")
public class FileController {


    private final StorageService storageService;


    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/my_drive")
    public String listMainDirectory(Model model, Principal principal) throws IOException {

        model.addAttribute("files", buildURI(principal, model, storageService.loadAll(".", principal.getName())));
        List<FileAddress> navigationList = buildURI(principal, model, storageService.loadDirectoryNavigation(".", principal.getName()));
        model.addAttribute("current_address", navigationList.get(0));

        return "file";
    }

    @GetMapping("/folders")
    public String listFolder(@RequestParam("directory_path") String directoryPath, Model model, Principal principal) throws IOException {


        if (directoryPath == "")
            return "redirect:/drive/my_drive";

        model.addAttribute("files", buildURI(principal, model, storageService.loadAll(directoryPath, principal.getName())));
        List<FileAddress> navigationList = buildURI(principal, model, storageService.loadDirectoryNavigation(directoryPath, principal.getName()));
        model.addAttribute("current_address", navigationList.get(0));
        model.addAttribute("root_address", navigationList.get(1));


        return "file";

    }


    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<Object> serveUserFiles(@RequestParam(value = "filename") String filename, Principal principal) throws IOException {

        Path filePath = storageService.loadFile(filename, principal.getName());
        File fileToDownload = new File(filePath.toString());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileToDownload));

        return ResponseEntity
                .ok()
                .contentLength(fileToDownload.length())
                .header(HttpHeaders.CONTENT_DISPOSITION,

                        "attachment; filename=\"" + filePath.getFileName() + "\"")
                .body(resource);

    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("current_folder") String currentFolder, @RequestParam("file") MultipartFile file, Principal principal,
                                   RedirectAttributes redirectAttributes) throws IOException {
        String userName = principal.getName();
        storageService.store(file, currentFolder, userName);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/drive/folders?directory_path=" + currentFolder;
    }

    @PostMapping("/create_folder")
    public String createNewFolder(@RequestParam("path") String path, @RequestParam("folder_name") String folderName, Principal principal) throws IOException {

        storageService.createFolder(path, folderName, principal.getName());


        return "redirect:/drive/folders?directory_path=" + path;

    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }


    private List<FileAddress> buildURI(Principal principal, @Nullable Model model, Stream<FileAddress> address) throws IOException {

        return address
                .map(temp -> {

                    if (!temp.isDirectory()) {

                        temp.setURIAdress(MvcUriComponentsBuilder
//                                    .relativeTo()
                                .fromMethodName(FileController.class, "serveUserFiles", temp.getRelativeAdress(), principal)
                                .build()
                                .toString());
                    } else {


                        temp.setURIAdress(MvcUriComponentsBuilder
//                                    .relativeTo()
                                .fromMethodName(FileController.class, "listFolder", temp.getRelativeAdress(), model, principal)
                                .build()
                                .toString());
                    }

                    return temp;
                })
                .collect(Collectors.toList());

    }


}
