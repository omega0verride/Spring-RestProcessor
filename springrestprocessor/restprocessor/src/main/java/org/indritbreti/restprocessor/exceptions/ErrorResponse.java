package org.indritbreti.restprocessor.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//  @JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponse {
    private HttpStatus httpStatus;

    private int httpStatusCode;

    private String exceptionId;

    private List<String> exceptionStack;
    private String message;

    private Exception rootException;

    private String rootExceptionMessage;
    private HashMap<String, Object> details = new HashMap<>();
    private ArrayList<String> detailsList = new ArrayList<>();

    public ErrorResponse(BaseException exception) {
        setHttpStatus(exception.getHttpStatus());
        setHttpStatusCode(exception.getHttpStatusCode());
        setExceptionId(exception.getExceptionId());
        setExceptionStack(exception.getExceptionStack());
        setMessage(exception.getMessage());
        setDetails(exception.getDetails());
        setDetailsList(exception.getDetailsList());
        if (!exception.isSuppressRootException())
            setRootException(exception.getRootException());
        if (!exception.isSuppressRootException()) {
            Exception ex = exception.getRootException();
            setRootExceptionMessage(ex == null ? null : ex.getMessage());
        }
        if (exception.isExposeExtraFieldsAsDetails()) { // NOTE for this to work it is recommended to declare the fields public
            buildDetails(exception);
        }
    }

    public ErrorResponse(String message) {
        this(message, new ArrayList<>(), new HashMap<>());
    }

    public ErrorResponse(String message, HashMap<String, Object> details) {
        this(message, new ArrayList<>(), details);
    }

    public ErrorResponse(String message, ArrayList<String> details_list) {
        this(message, details_list, new HashMap<>());
    }

    public ErrorResponse(String message, ArrayList<String> detailsList, HashMap<String, Object> details) {
        this.message = message;
        this.details = details;
        this.detailsList = detailsList;
    }

    private void buildDetails(Exception exception) {
        Class<?> class_ = exception.getClass();
        while (class_ != null && class_ != BaseException.class) {
            for (Field f : class_.getDeclaredFields()) {
                try {
                    details.put(f.getName(), f.get(exception));
                } catch (IllegalAccessException ex) {
                    System.out.println("Could not access field '" + f.getName() + "' to build exception details. Check if this field is declared as public in the exception class '" + class_ + "'."); // TODO change to log
                }
            }
            class_ = class_.getSuperclass();
        }

    }

    public void addDetail(String detail) {
        detailsList.add(detail);
    }

    public void putDetail(String key, Object value) {
        details.put(key, value);
    }

    public ResponseEntity<Object> toResponseEntity() {
        return new ResponseEntity<>(this, getHttpStatus());
    }


    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public List<String> getExceptionStack() {
        return exceptionStack;
    }

    public void setExceptionStack(List<String> exceptionStack) {
        this.exceptionStack = exceptionStack;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getRootException() {
        return rootException;
    }

    public void setRootException(Exception rootException) {
        this.rootException = rootException;
    }

    public String getRootExceptionMessage() {
        return rootExceptionMessage;
    }

    public void setRootExceptionMessage(String rootExceptionMessage) {
        this.rootExceptionMessage = rootExceptionMessage;
    }

    public HashMap<String, Object> getDetails() {
        return details;
    }

    public void setDetails(HashMap<String, Object> details) {
        this.details = details;
    }

    public ArrayList<String> getDetailsList() {
        return detailsList;
    }

    public void setDetailsList(ArrayList<String> detailsList) {
        this.detailsList = detailsList;
    }
}