/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2022 the original author or authors.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bernardomg.api.springframework.test.error.integration;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.bernardomg.api.springframework.test.error.util.annotation.MvcIntegrationTest;
import com.bernardomg.api.springframework.test.error.util.controller.ResponseTestController;

@MvcIntegrationTest
@DisplayName("Controller advices - response structure")
public final class ITControllerResponsePaged {

    @Autowired
    private MockMvc mockMvc;

    public ITControllerResponsePaged() {
        super();
    }

    @Test
    @DisplayName("Returns the paginated response structure when returning the default page")
    public final void testResponseStructure_Page() throws Exception {
        final ResultActions result;

        result = mockMvc.perform(getPageRequest());

        // The operation was accepted
        result.andExpect(MockMvcResultMatchers.status()
            .isOk());

        // The response model has content
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(5)));

        // The response model contains pagination properties
        result.andExpect(MockMvcResultMatchers.jsonPath("$.page", Matchers.equalTo(0)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size", Matchers.equalTo(5)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.elementsInPage", Matchers.equalTo(5)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", Matchers.equalTo(20)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", Matchers.equalTo(4)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.first", Matchers.equalTo(true)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.last", Matchers.equalTo(false)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.sort", Matchers.hasSize(0)));

        // The response contains no error attribute
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors")
            .doesNotExist());
    }

    @Test
    @DisplayName("Returns the paginated response structure when returning a sorted page")
    public final void testResponseStructure_Page_Sorted() throws Exception {
        final ResultActions result;

        result = mockMvc.perform(getPageRequestSorted());

        // The operation was accepted
        result.andExpect(MockMvcResultMatchers.status()
            .isOk());

        // The response model has content
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content", Matchers.hasSize(5)));

        // The response model contains pagination properties
        result.andExpect(MockMvcResultMatchers.jsonPath("$.page", Matchers.equalTo(0)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size", Matchers.equalTo(5)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.elementsInPage", Matchers.equalTo(5)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalElements", Matchers.equalTo(20)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalPages", Matchers.equalTo(4)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.first", Matchers.equalTo(true)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.last", Matchers.equalTo(false)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.sort", Matchers.hasSize(1)));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.sort[0].property", Matchers.equalTo("name")));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.sort[0].direction", Matchers.equalTo("asc")));

        // The response contains no error attribute
        result.andExpect(MockMvcResultMatchers.jsonPath("$.errors")
            .doesNotExist());
    }

    private final RequestBuilder getPageRequest() {
        return MockMvcRequestBuilders.get(ResponseTestController.PATH_PAGE)
            .contentType(MediaType.APPLICATION_JSON);
    }

    private final RequestBuilder getPageRequestSorted() {
        return MockMvcRequestBuilders.get(ResponseTestController.PATH_PAGE_SORTED)
            .contentType(MediaType.APPLICATION_JSON);
    }

}
