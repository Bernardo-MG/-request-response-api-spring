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

package com.bernardomg.api.springframework.error.handler;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.bernardomg.api.response.DefaultResponse;
import com.bernardomg.api.response.ErrorResponse;
import com.bernardomg.api.validation.error.DefaultErrorInformation;
import com.bernardomg.api.validation.failure.ImmutablePropertyFailure;
import com.bernardomg.api.validation.failure.PropertyFailure;
import com.bernardomg.api.validation.failure.exception.PropertyFailureException;

import lombok.extern.slf4j.Slf4j;

/**
 * Captures and handles exceptions for all the controllers.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Default constructor.
     */
    public GlobalExceptionHandler() {
        super();
    }

    @ExceptionHandler({ RuntimeException.class })
    public final ResponseEntity<Object> handleExceptionDefault(final Exception ex, final WebRequest request)
            throws Exception {
        final ErrorResponse<?>        response;
        final DefaultErrorInformation error;

        log.warn(ex.getMessage(), ex);

        error = new DefaultErrorInformation();
        error.setTitle("Internal error");

        response = DefaultResponse.builder()
            .error(error)
            .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ DataAccessException.class, PropertyReferenceException.class })
    public final ResponseEntity<Object> handlePersistenceException(final Exception ex, final WebRequest request)
            throws Exception {
        final ErrorResponse<?>        response;
        final DefaultErrorInformation error;

        log.warn(ex.getMessage(), ex);

        error = new DefaultErrorInformation();
        error.setTitle("Invalid query");

        response = DefaultResponse.builder()
            .error(error)
            .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ PropertyFailureException.class })
    public final ResponseEntity<Object> handleValidationException(final PropertyFailureException ex,
            final WebRequest request) throws Exception {
        final ErrorResponse<?>        response;
        final DefaultErrorInformation error;

        log.warn(ex.getMessage(), ex);

        // TODO: add more info
        error = new DefaultErrorInformation();

        response = DefaultResponse.builder()
            .error(error)
            .content(ex.getFailures())
            .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Transforms Spring's field error into our custom field error.
     *
     * @param error
     *            error object to transform
     * @return our custom error object
     */
    private final PropertyFailure toPropertyFailure(final org.springframework.validation.FieldError error) {
        final Collection<String> codes;
        final String             code;

        log.error("{}.{} with value {}: {}", error.getObjectName(), error.getField(), error.getRejectedValue(),
            error.getDefaultMessage());

        codes = Arrays.asList(error.getCodes());
        if (codes.contains("NotNull")) {
            code = "empty";
        } else if (codes.contains("NotEmpty")) {
            code = "empty";
        } else {
            code = "";
        }

        return new ImmutablePropertyFailure(error.getDefaultMessage(), error.getField(), code,
            error.getRejectedValue());
    }

    @Override
    protected final ResponseEntity<Object> handleExceptionInternal(final Exception ex, final Object body,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final ErrorResponse<?>        response;
        final DefaultErrorInformation error;

        log.error(ex.getMessage());

        error = new DefaultErrorInformation();
        error.setTitle("Internal error");
        error.setType("500");

        response = DefaultResponse.builder()
            .error(error)
            .build();

        return super.handleExceptionInternal(ex, response, headers, status, request);
    }

    @Override
    protected final ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        final Collection<PropertyFailure> errors;
        final ErrorResponse<?>            response;
        final DefaultErrorInformation     error;

        // TODO: add more info
        error = new DefaultErrorInformation();

        errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::toPropertyFailure)
            .collect(Collectors.toList());

        response = DefaultResponse.builder()
            .error(error)
            .content(errors)
            .build();

        return super.handleExceptionInternal(ex, response, headers, status, request);
    }

}
