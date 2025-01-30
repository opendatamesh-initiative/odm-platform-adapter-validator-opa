package org.opendatamesh.platform.adapter.validator.opa.server.resources.errors;

import java.util.Date;

public class ErrorRes {

    int status;
    long timestamp = new Date().getTime();
    String error;
    String message;
    String path;

    public ErrorRes(int status, String errorName, String message, String path) {
        super();
        this.status = status;
        this.error = errorName;
        this.message = message;
        this.path = path;
    }

    /**
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return the errorName
     */
    public String getError() {
        return error;
    }

    public String getErrorName(){
        return this.error;
    }

    /**
     * @param error the errorName to set
     */
    public void setError(String error) {
        this.error = error;
    }


    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the url
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the url to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}