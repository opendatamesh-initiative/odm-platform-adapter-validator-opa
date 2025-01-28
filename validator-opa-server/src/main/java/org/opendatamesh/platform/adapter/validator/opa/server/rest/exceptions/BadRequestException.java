package org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ValidatorApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2775226019414735885L;

	public BadRequestException() {
		super();
	}

	public BadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.BAD_REQUEST;
	}

}