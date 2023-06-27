package org.indritbreti.restprocessor;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;

public class MethodParameter {
    String name;
    TypeMirror type;

    List<? extends AnnotationMirror> annotationMirrors;

    public MethodParameter(String name, TypeMirror type, List<? extends AnnotationMirror> annotationMirrors) {
        this.name = name;
        this.type = type;
        this.annotationMirrors = annotationMirrors;
    }

    public String getName() {
        return name;
    }

    public TypeMirror getType() {
        return type;
    }

    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return annotationMirrors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (annotationMirrors!=null)
            sb.append(annotationMirrors.stream().map(Object::toString)
                    .collect(Collectors.joining(" "))).append(" ");
        if (type!=null)
            sb.append(type).append(" ");
        sb.append(name);
        return sb.toString().trim();
    }
}
