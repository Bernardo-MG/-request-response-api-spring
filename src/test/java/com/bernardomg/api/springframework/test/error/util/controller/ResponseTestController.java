
package com.bernardomg.api.springframework.test.error.util.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ResponseTestController.PATH)
public class ResponseTestController {

    public static final String PATH             = "/test/response";

    public static final String PATH_COLLECTION  = PATH + "/collection";

    public static final String PATH_PAGE        = PATH + "/page";

    public static final String PATH_PAGE_SORTED = PATH + "/page/sorted";

    public ResponseTestController() {
        super();
    }

    @GetMapping(path = "/collection", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Object> readCollection() {
        return Arrays.asList("abc");
    }

    @GetMapping(path = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<String> readPage() {
        final List<String> content;
        final Pageable     pageable;
        final long         total;

        content = Arrays.asList("abc", "abc", "abc", "abc", "abc");
        pageable = Pageable.ofSize(5);
        total = 20;

        return new PageImpl<>(content, pageable, total);
    }

    @GetMapping(path = "/page/sorted", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<String> readPageSorted() {
        final List<String> content;
        final Pageable     pageable;
        final long         total;

        content = Arrays.asList("abc", "abc", "abc", "abc", "abc");
        pageable = PageRequest.of(0, 5, Direction.ASC, "name");
        total = 20;

        return new PageImpl<>(content, pageable, total);
    }

}
