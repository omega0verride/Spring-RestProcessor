package org.indritbreti.restprocessor.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseException extends RuntimeException {
    private HttpStatus httpStatus;
    private int httpStatusCode;
    private String exceptionId = "";
    private List<String> exceptionStack = new ArrayList<>(); // make sure to call buildExceptionStack if modified

    private String message; // descriptive msg
    private Exception rootException; // root ex if we are encapsulating one
    private HashMap<String, Object> details = new HashMap<>(); // extra details
    private ArrayList<String> detailsList = new ArrayList<>();

    private boolean suppressRootException = true;
    private boolean suppressRootExceptionMessage = true;
    private boolean exposeExtraFieldsAsDetails = true;

    private boolean isInternalServerException = false;
    private String internalServerExceptionLogFile = null;
    private String internalServerExceptionURL = null;

    public BaseException(HttpStatus httpStatus, String message, String customExceptionId, boolean suppressRootException, boolean suppressRootExceptionMessage) {
        setHttpStatus(httpStatus);
        buildExceptionStack(customExceptionId);
        setMessage(message);
        setSuppressRootException(suppressRootException);
        setSuppressRootExceptionMessage(suppressRootExceptionMessage);
    }

    public BaseException(HttpStatus httpStatus, String message, String customExceptionId, boolean suppressRootExceptionMessage) {
        this(httpStatus, message, customExceptionId, true, false);
    }

    public BaseException(HttpStatus httpStatus, String message, String customExceptionId) {
        this(httpStatus, message, customExceptionId, true, true);
    }

    public BaseException(HttpStatus httpStatus, String message) {
        this(httpStatus, message, null, true, true);
    }

    public BaseException(HttpStatus httpStatus) {
        this(httpStatus, null);
    }

    public BaseException() {
        buildExceptionStack(null);
        buildAsInternalServerException();
    }

    private void buildExceptionStack(String customExceptionId) {
        Class<?> class_ = getClass();
        StringBuilder id = new StringBuilder();
        if (customExceptionId != null && customExceptionId.trim().length() != 0) {
            exceptionStack.add(0, class_.getSimpleName());
            id.insert(0, class_.getSimpleName());
        }
        while (class_ != null && class_ != BaseException.class) {
            if (id.length() > 0) // do no insert a dot "." if customExceptionId was null or empty
                id.insert(0, ".");
            exceptionStack.add(0, class_.getSimpleName());
            id.insert(0, class_.getSimpleName());
            class_ = class_.getSuperclass();
        }
        exceptionId = id.toString();
    }

    public void setCustomExceptionId(String customExceptionId) {
        buildExceptionStack(customExceptionId);
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        this.httpStatusCode = httpStatus.value();
    }

    public void addDetail(String detail) {
        detailsList.add(detail);
    }

    public void putDetail(String key, Object value) {
        details.put(key, value);
    }

    public void buildAsInternalServerException() {
        isInternalServerException = true;
        setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR); // TODO
    }

    public boolean isInternalServerException() {
        return isInternalServerException;
    }

    @Override
    public String toString() {
        return "BaseException{" +
                "httpStatus=" + httpStatus +
                ", httpStatusCode=" + httpStatusCode +
                ", exceptionId='" + exceptionId + '\'' +
                ", exceptionStack=" + exceptionStack +
                ", message='" + message + '\'' +
                ", rootException=" + rootException +
                ", details=" + details +
                ", detailsList=" + detailsList +
                ", suppressRootException=" + suppressRootException +
                ", suppressRootExceptionMessage=" + suppressRootExceptionMessage +
                ", exposeExtraFieldsAsDetails=" + exposeExtraFieldsAsDetails +
                ", isInternalServerException=" + isInternalServerException +
                ", internalServerExceptionLogFile='" + internalServerExceptionLogFile + '\'' +
                ", internalServerExceptionURL='" + internalServerExceptionURL + '\'' +
                '}';
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse(this);
    }

    public ResponseEntity<Object> toResponseEntity() {
        return toErrorResponse().toResponseEntity();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getExceptionId() {
        return exceptionId;
    }

    public List<String> getExceptionStack() {
        return exceptionStack;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Exception getRootException() {
        return rootException;
    }

    public HashMap<String, Object> getDetails() {
        return details;
    }

    public ArrayList<String> getDetailsList() {
        return detailsList;
    }

    public boolean isSuppressRootException() {
        return suppressRootException;
    }

    public boolean isSuppressRootExceptionMessage() {
        return suppressRootExceptionMessage;
    }

    public boolean isExposeExtraFieldsAsDetails() {
        return exposeExtraFieldsAsDetails;
    }

    public String getInternalServerExceptionLogFile() {
        return internalServerExceptionLogFile;
    }

    public String getInternalServerExceptionURL() {
        return internalServerExceptionURL;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRootException(Exception rootException) {
        this.rootException = rootException;
    }

    public void setDetails(HashMap<String, Object> details) {
        this.details = details;
    }

    public void setDetailsList(ArrayList<String> detailsList) {
        this.detailsList = detailsList;
    }

    public void setSuppressRootException(boolean suppressRootException) {
        this.suppressRootException = suppressRootException;
    }

    public void setSuppressRootExceptionMessage(boolean suppressRootExceptionMessage) {
        this.suppressRootExceptionMessage = suppressRootExceptionMessage;
    }

    public void setExposeExtraFieldsAsDetails(boolean exposeExtraFieldsAsDetails) {
        this.exposeExtraFieldsAsDetails = exposeExtraFieldsAsDetails;
    }

    public void printRootStackTrace() {
        if (rootException!=null)
            rootException.printStackTrace();
    }
}
