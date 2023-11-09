package com.xoso.api.controller;

import com.xoso.campaign.service.TemplateContentService;
import com.xoso.infrastructure.core.data.SearchParameters;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/template-content")
public class TemplateContentController extends BaseController {

    @Autowired
    private TemplateContentService templateContentService;

    @Autowired
    private Environment env;

    @GetMapping("active")
    public ResponseEntity<?> listAll(@RequestParam(value = "pageNumber", required = false, defaultValue = "0") int pageNumber,
                                     @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,
                                     @RequestParam(value = "category") String category,
                                     @RequestParam(value = "language") String language) {
        var searchParams = new SearchParameters(pageNumber, pageSize);
        searchParams.setForClient(Boolean.TRUE);
        searchParams.setCategory(category);
        searchParams.setLocale(language);
        var contents = templateContentService.retrieveAll(searchParams).getPageItems();
        contents.forEach(e -> {
            var path = env.getProperty("file.upload.url");
            if (StringUtils.isNotBlank(e.getBanner())) {
                e.setBannerUrl(String.format("%s%s", path, e.getBanner()));
            }
        } );
        return response(contents);
    }
    @GetMapping("{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        var searchParams = new SearchParameters();
        searchParams.setForClient(Boolean.TRUE);
        var content = templateContentService.retrieveOne(id);
        return response(content);
    }
}
