package org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions;

import org.springframework.http.HttpStatus;

public class NotImplemented extends ValidatorApiException {

	public NotImplemented() {
	}

	public NotImplemented(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotImplemented(String message, Throwable cause) {
		super(message, cause);
	}

	public NotImplemented(String message) {
		super(message);
	}

	public NotImplemented(Throwable cause) {
		super(cause);
	}

	@Override
	public HttpStatus getStatus() {
		return HttpStatus.NOT_IMPLEMENTED;
	}
}
