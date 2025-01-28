package org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ValidatorApiException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2263265399328435820L;

	public NotFoundException(String string) {
		super(string);
	}

	
	public NotFoundException() {
		super();
	}


	public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}


	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}


	public NotFoundException(Throwable cause) {
		super(cause);
	}


	@Override
	public HttpStatus getStatus() {
		return HttpStatus.NOT_FOUND;
	}

}