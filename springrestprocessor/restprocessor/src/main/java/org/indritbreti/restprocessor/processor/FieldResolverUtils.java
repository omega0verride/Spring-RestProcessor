package org.indritbreti.restprocessor.processor;

import jakarta.persistence.Embedded;
import org.indritbreti.restprocessor.FieldDetails;
import org.indritbreti.restprocessor.IgnoreRESTField;
import org.indritbreti.restprocessor.JoinRESTField;
import org.indritbreti.restprocessor.RESTField;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.*;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;

public class FieldResolverUtils {
    public static Hashtable<String, FieldDetails> getFields(Element element, ProcessingEnvironment processingEnvironment, RoundEnvironment roundEnvironment) {
        return getFields(element, new Hashtable<>(), null, processingEnvironment, roundEnvironment);
    }

    private static Hashtable<String, FieldDetails> getFields(Element element, Hashtable<String, FieldDetails> fields, FieldDetails restFieldDetailsPersist, ProcessingEnvironment processingEnvironment, RoundEnvironment roundEnvironment) {
        Queue<Element> queuedNestedRestFields = new ArrayDeque<>();
        if (restFieldDetailsPersist == null)
            restFieldDetailsPersist = new FieldDetails();
        while (element != null) {
            // fields are considered all elements that:
            // are of kind field
            // are not static or final
            // are not marked @IgnoreRESTField
            // datatype byte is ignored, it does extend from Number but would be out of the standard
            // transient fields are allowed since we can have non-persistent/"in memory" fields that we might need to filter by
            List<VariableElement> entityFields = element.getEnclosedElements().stream().filter(e_ -> (e_.getKind().isField() && e_.getAnnotation(IgnoreRESTField.class) == null && !e_.getModifiers().contains(Modifier.STATIC) && !e_.getModifiers().contains(Modifier.FINAL))).map(e_ -> (VariableElement) e_).toList();
            for (Element e : entityFields) {
                // TODO: check for superclass, unless the object is a registered serializable type i.e:
                // Integer
                // BigDecimal (this one does not directly extend java.lang.Object so the current impl will not work)
                // TODO how should we serialize specific object types
                // 1 if this is a string field, build the FieldDetails
                // 2 if the fields extends from Number.class, build the FieldDetails, the filter factory will be able to build the corresponding NumericFilter
                // 3 if the field is an ENUM, build the FieldDetails
                // 4 if the field is any other type
                //  4.1 check if the field is marked as @Embedded -> add the field to queuedNestedRestFields and let it resolve the embedded fields later
                //  4.2 check if the field is marked as @ManyToMany -> --==--
                //  4.3 check if the field is marked as @ManyToOne/OneToMany (?is there a OneToOne mapping in the persistence API?) TODO
                // 5 in any other
                // TODO date types, character types
                TypeElement typeElement = TypeMirrorUtils.getTypeElement(e, processingEnvironment);
                RESTField restFieldAnnotation = e.getAnnotation(RESTField.class);
                if (restFieldAnnotation != null || TypeMirrorUtils.matchesAnyClassName(typeElement, String.class, Boolean.class, Character.class) || typeElement.getKind().equals(ElementKind.ENUM) || TypeMirrorUtils.matchesAnyClassName(typeElement, Date.class, Time.class, Timestamp.class) || (!TypeMirrorUtils.matchesClassName(typeElement, Byte.class) && TypeMirrorUtils.instanceOf(typeElement, Number.class, processingEnvironment))) {
                    FieldDetails fieldDetails = restFieldDetailsPersist.clone(); // clone the field so we can still persist the
                    fields.put(fieldDetails.buildFieldDetails(e.getSimpleName().toString(), TypeMirrorUtils.getTypeElement(e, processingEnvironment), restFieldAnnotation, fields, processingEnvironment), fieldDetails); // build the field based on the annotation
                }
                else {
                    queuedNestedRestFields.add(e);
                }

                // TODO what about fields annotated with @Column, will the naming schema work?

            }

            // search for fields in superclass i.e: Product extends ProductBase
            TypeElement superClass = TypeMirrorUtils.getTypeElement(((TypeElement) element).getSuperclass(), processingEnvironment);
            if (TypeMirrorUtils.matchesClassName(superClass, Object.class))
                element = null;
            else
                element = superClass;
        }
        // after resolving all first level fields, go through nested fields that are waiting in queuedNestedRestFields
        while (!queuedNestedRestFields.isEmpty()) {
            Element e = queuedNestedRestFields.remove();
            // TODO this should be replaced with JPA annotations i.e: ManyToMany, OneToMany etc.
            JoinRESTField joinFieldsAnnotation = e.getAnnotation(JoinRESTField.class);
            if (joinFieldsAnnotation != null) {
                Element nestedClass = TypeMirrorUtils.getTypeElement(joinFieldsAnnotation, JoinRESTField::joinClass, processingEnvironment);
                restFieldDetailsPersist.addPersistencePath(e.getSimpleName().toString(), e.getSimpleName().toString()); // keep track of root path
                getFields(nestedClass, fields, restFieldDetailsPersist, processingEnvironment, roundEnvironment);
                // recursively resolve sortable fields for this root
                restFieldDetailsPersist.popPersistencePath(); // remove last path since we resolved all sortable field for the given embedded object
            } else {
                Embedded embeddableFieldAnnotation = e.getAnnotation(Embedded.class);
                if (embeddableFieldAnnotation != null) {
                    Element nestedClass = TypeMirrorUtils.getTypeElement(e.asType(), processingEnvironment);
                    restFieldDetailsPersist.addPersistencePath(e.getSimpleName().toString(), false);
//                 recursively resolve sortable fields for this root
                    getFields(nestedClass, fields, restFieldDetailsPersist, processingEnvironment, roundEnvironment);
                    restFieldDetailsPersist.popPersistencePath(); // remove last path since we resolved all sortable field for the given embedded object
                }
            }
        }
        return fields;
    }
}
