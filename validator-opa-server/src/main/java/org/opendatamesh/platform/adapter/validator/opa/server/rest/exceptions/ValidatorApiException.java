package org.opendatamesh.platform.adapter.validator.opa.server.rest.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ValidatorApiException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3876573329263306459L;	
	
	public ValidatorApiException() {
		super();
	}

	public ValidatorApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ValidatorApiException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidatorApiException(String message) {
		super(message);
	}

	public ValidatorApiException(Throwable cause) {
		super(cause);
	}

	/**
	 * @return the errorName
	 */
	public String getErrorName() {
		return getClass().getSimpleName();	
	}

	/**
	 * @return the status
	 */
	public abstract HttpStatus getStatus();	
	

}