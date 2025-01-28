package org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions;

import org.springframework.http.HttpStatus;

public class InternalException extends ValidatorApiException {
	public InternalException() {
	}

	public InternalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InternalException(String message, Throwable cause) {
		super(message, cause);
	}

	public InternalException(String message) {
		super(message);
	}

	public InternalException(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
