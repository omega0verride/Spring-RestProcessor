package org.indritbreti.restprocessor;

import org.springframework.web.bind.annotation.*;

public enum RequestMethod {
    GET(GetMapping.class),
    DELETE(DeleteMapping.class),
    PATCH(PatchMapping.class),
    POST(PostMapping.class),
    PUT(PutMapping.class);
    private final Class<?> annotation;

    RequestMethod(Class<?> annotation) {
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        return annotation.getTypeName();
    }

    public Class<?> getRequestMethodAnnotation(){
        return annotation;
    }
}
