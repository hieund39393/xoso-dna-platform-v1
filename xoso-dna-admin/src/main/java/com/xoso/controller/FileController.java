package com.xoso.controller;

import com.xoso.infrastructure.core.data.SearchParameters;
import com.xoso.media.service.FileInfoService;
import com.xoso.media.service.FilesStorageService;
import com.xoso.wsdto.UploadVideoResponseWsDTO;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

@Controller
@RequestMapping("files")
public class FileController {

    @Autowired
    private FilesStorageService filesStorageService;
    @Autowired
    private FileInfoService fileInfoService;


    @GetMapping("/upload")
    public String newImage(Model model) {
        return "file/upload";
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                         @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                         @RequestParam(value = "searchValue", required = false) String searchValue,
                         RedirectAttributes attr) {
        var searchParams = new SearchParameters(pageNumber, pageSize, searchValue);
        var fileInfoPage = this.fileInfoService.retrieveAll(searchParams);
        fileInfoPage.getPageItems().forEach(item -> {
            var url = MvcUriComponentsBuilder
                    .fromMethodName(FileController.class, "getFile",item.getFileName()).build().toString();
            item.setUrl(url);
        });
        model.addAttribute("fileInfoPage", fileInfoPage);
        var totalItems = fileInfoPage.getTotalFilteredRecords();
        int totalPages = totalItems / pageSize + (totalItems % pageSize > 0 ? 1 : 0);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("searchValue", searchValue);
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "file/list";
    }

    @PostMapping("/upload")
    public String uploadImage(Model model, @RequestParam("file") MultipartFile file) {
        String message = "";

        try {
            filesStorageService.save(file);
            message = "Uploaded the image successfully: " + file.getOriginalFilename();
            model.addAttribute("message", message);
        } catch (Exception e) {
            e.printStackTrace();
            message = "Could not upload the image: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            model.addAttribute("message", message);
        }
        return "redirect:/files/search";
    }

    @PostMapping("/uploadVideo")
    public ResponseEntity<?> uploadVideo(Model model, @RequestParam("file") MultipartFile file) {
        String message = "";

        try {
            filesStorageService.save(file);
            message = "Uploaded the image successfully: " + file.getOriginalFilename();
            model.addAttribute("message", message);
            //lay thong tin video duration
            long duration = getVideoDuration(file);
            return new ResponseEntity<>(new UploadVideoResponseWsDTO(file.getOriginalFilename(), duration), null, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            message = "Could not upload the image: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return new ResponseEntity<>(message, null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/storage/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        Resource file = filesStorageService.load(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    private long getVideoDuration(MultipartFile file){
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(file.getInputStream())) {
            grabber.start();
            int frameRate = (int) grabber.getFrameRate();
            int frameCount = grabber.getLengthInFrames();
            long duration = frameCount * 1000L / frameRate;
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
            // Xử lý ngoại lệ nếu có
            e.printStackTrace();
            return 0;
        }
    }
}
