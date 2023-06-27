package org.indritbreti.restprocessor.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.function.Function;

public class TypeMirrorUtils {
    public static Element getElementFromTypeMirror(TypeMirror type, RoundEnvironment roundEnv) {
        return roundEnv.getRootElements().stream().filter(element_ -> element_.toString().equals(type.toString())).findFirst().orElse(null);
    }


    /**
     * For Class attribute, if we invoke directly, it may throw {@link MirroredTypeException} because the class has not be
     * compiled. Use this method to get the Class value safely.
     *
     * @param anno annotation object
     * @param func the invocation of get Class value
     * @return the value's {@link TypeMirror}
     */
    public static <T extends Annotation> TypeMirror getAnnotationClassValue(T anno, Function<T, Class<?>> func) {
        try {
            func.apply(anno);
            return null; // TODO check if we ever reach this branch
        } catch (MirroredTypeException e) {
            return e.getTypeMirror();
        }
    }


    public static TypeElement getTypeElement(Element e, ProcessingEnvironment processingEnvironment) {
        if (e == null)
            return null;
        return getTypeElement(e.asType(), processingEnvironment);
    }

    public static TypeElement getTypeElement(TypeMirror type, ProcessingEnvironment processingEnvironment) {
        if (type == null)
            return null;
        if (type instanceof PrimitiveType primitiveType)
            return processingEnvironment.getTypeUtils().boxedClass(primitiveType);
        return (TypeElement) processingEnvironment.getTypeUtils().asElement(type);
    }

    public static <T extends Annotation> TypeElement getTypeElement(T anno, Function<T, Class<?>> func, ProcessingEnvironment processingEnvironment) {
        return getTypeElement(getAnnotationClassValue(anno, func), processingEnvironment);
    }

    public static boolean matchesAnyClassName(TypeElement typeElement, Class<?> ... class_) {
        for (Class<?> c:class_)
            if (matchesClassName(typeElement, c))
                return true;
        return false;
    }
    public static boolean matchesClassName(TypeElement typeElement, Class<?> class_) {
        return typeElement.getQualifiedName().toString().equals(class_.getCanonicalName());
    }

    public static boolean instanceOf(TypeElement typeElement, Class<?> class_, ProcessingEnvironment processingEnvironment) {
        if (typeElement==null)
            return false;
        if (typeElement.getQualifiedName().toString().equals(Object.class.getCanonicalName()))
            return class_.getCanonicalName().equals(Object.class.getCanonicalName());
        return typeElement.getQualifiedName().toString().equals(class_.getCanonicalName()) || instanceOf(getTypeElement(typeElement.getSuperclass(), processingEnvironment), class_, processingEnvironment);
    }
}
