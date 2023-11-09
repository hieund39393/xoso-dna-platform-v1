package com.xoso.api.controller;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.media.service.FileInfoService;
import com.xoso.media.service.FilesStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("files")
public class FileController extends BaseController{

    @Autowired
    private FilesStorageService filesStorageService;
    @Autowired
    private FileInfoService fileInfoService;
    @GetMapping("/storage/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = filesStorageService.load(filename);

        return ResponseEntity.ok()
		.contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/video/{filename:.+}")
    public ResponseEntity<Resource> getVideo(@PathVariable String filename) {
        Resource file = filesStorageService.load(filename);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

}
