package com.mladenov.projectmanagement.controller;

import com.mladenov.projectmanagement.model.dto.search.SearchResultDto;
import com.mladenov.projectmanagement.service.impl.SearchService;
import com.mladenov.projectmanagement.util.AppResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Search Controller", description = "Operations related to searching")
@RestController
@RequestMapping("/api/v1/search")
@SecurityRequirement(name = "bearerAuth")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<?> search(@RequestParam String q) {
        List<SearchResultDto> search = searchService.search(q);

        return AppResponseUtil.success()
                .withData(search)
                .build();
    }
}
