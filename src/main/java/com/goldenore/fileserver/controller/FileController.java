package com.goldenore.fileserver.controller;


import com.goldenore.fileserver.pojo.FileAddress;
import com.goldenore.fileserver.service.StorageFileNotFoundException;
import com.goldenore.fileserver.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/files")
public class FileController {


    private final StorageService storageService;


    @Autowired
    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/users/{username}")
    public String listUploadedUserFiles(@PathVariable String username, Model model, Principal principal) throws IOException {


        model.addAttribute("files",
                storageService
                        .loadAll(username)
                        .map(temp -> {
                            FileAddress fileAddress = new FileAddress();
                            fileAddress.setPhysicalAdress(temp.getFileName().toString());
                            fileAddress.setURIAdress(MvcUriComponentsBuilder.fromMethodName(FileController.class,
                                    "serveUserFiles", temp.getFileName().toString(), principal).build().toString());
                            return fileAddress;
                        })
                        .collect(Collectors.toList()));

        return "file";
    }

    @GetMapping("/download")
    @ResponseBody
    public ResponseEntity<FileInputStream> serveUserFiles(@RequestParam(value = "filename") String filename, Principal principal) throws IOException {

//        String requestURL = request.getRequestURL().toString();
//
//        String moduleName = requestURL.split("/download/")[1];

        System.out.println(filename);
        Path filePath = storageService.load(filename, principal.getName());
        FileInputStream fileBody = new FileInputStream(new File(filePath.toUri()));

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,

                "attachment; filename=\"" + filePath.getFileName() + "\"").body(fileBody);

    }

    @PostMapping("/upload")
    public String handleFileUpload(Principal principal, @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {
        String userName = principal.getName();
        storageService.store(file, userName);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/files/users/" + userName;
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }


}
