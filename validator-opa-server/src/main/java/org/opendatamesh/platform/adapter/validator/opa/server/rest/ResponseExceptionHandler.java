package org.opendatamesh.platform.adapter.validator.opa.server.rest;

import org.opendatamesh.platform.adapter.validator.opa.server.resources.errors.ErrorRes;
import org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions.ValidatorApiException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({RuntimeException.class})
    private ResponseEntity<Object> handleRuntimeException(RuntimeException e, WebRequest request) {
        this.logger.error("Unknown server error: ", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String url = this.getUrl(request);
        ErrorRes errorRes = new ErrorRes(status.value(), "Server Error", "Unknown Internal Server Error", url);
        return this.handleExceptionInternal(e, errorRes, headers, status, request);
    }

    @ExceptionHandler({ValidatorApiException.class})
    private ResponseEntity<Object> handleBlindataException(ValidatorApiException e, WebRequest request) {
        if (e.getStatus() == HttpStatus.INTERNAL_SERVER_ERROR) {
            logger.error(e.getErrorName() + ":" + e.getMessage(), e);
        } else {
            logger.info(e.getErrorName() + ":" + e.getMessage());
        }
        String url = getUrl(request);
        ErrorRes error = new ErrorRes(e.getStatus().value(), e.getErrorName(), e.getMessage(), url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return handleExceptionInternal(e, error, headers, e.getStatus(), request);
    }


    private String getUrl(WebRequest request) {
        String url = request.toString();
        if (request instanceof ServletWebRequest) {
            ServletWebRequest r = (ServletWebRequest) request;
            url = r.getRequest().getRequestURI();
        }

        return url;
    }
}