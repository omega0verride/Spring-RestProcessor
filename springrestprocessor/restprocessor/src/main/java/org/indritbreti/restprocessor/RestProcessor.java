package org.indritbreti.restprocessor;

import com.google.auto.service.AutoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.Filter;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.Filters.FilterFactory;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicFilterBuilder.RHSColonExpression;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.FunctionArg;
import org.indritbreti.restprocessor.DynamicQueryBuilder.DynamicSortBuilder.MultiColumnSort;
import org.indritbreti.restprocessor.DynamicQueryBuilder.JPA.DynamicQueryRepository;
import org.indritbreti.restprocessor.DynamicQueryBuilder.JPA.DynamicQueryRepositoryUtils;
import org.indritbreti.restprocessor.DynamicRESTController.CriteriaParameters;
import org.indritbreti.restprocessor.processor.TypeMirrorUtils;
import org.indritbreti.restprocessor.processor.ClassBuilder;
import org.indritbreti.restprocessor.processor.FieldResolverUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AutoService({Processor.class, AbstractProcessor.class})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
//@SupportedAnnotationTypes({"org.indritbreti.restprocessor.EnableRestProcessor", "org.indritbreti.restprocessor.DynamicRestMapping", "org.indritbreti.restprocessor.RESTField", "org.indritbreti.restprocessor.JoinRESTField"})
public class RestProcessor extends AbstractProcessor {

    private static final String DEFAULT_PACKAGE_NAME = "org.indritbreti.restprocessor";
    private final transient ProcessorFieldDetailsRegistry processorFieldDetailsRegistry = ProcessorFieldDetailsRegistry.instance(DEFAULT_PACKAGE_NAME, false);

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.size() == 0) return true;

        // generate all dynamic repositories for all @Entity annotated classes beforehand
        for (Element entityClass : roundEnv.getElementsAnnotatedWith(Entity.class)) {
            TypeElement entityTypeElement = TypeMirrorUtils.getTypeElement(entityClass, processingEnv);
            if (entityTypeElement == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not get TypeMirror from @Entity, " + entityClass);
                return false;
            }
            processorFieldDetailsRegistry.bind(entityTypeElement.getQualifiedName().toString(), FieldResolverUtils.getFields(entityClass, processingEnv, roundEnv));
            buildDynamicQueryRepository(entityTypeElement);
        }


        // get all dynamic rest mappings
        // store them in the specific controller they are part of
        Map<Element, Set<ExecutableElement>> dynamicRestControllers = new Hashtable<>();
        for (Element dynamicRestMapping : roundEnv.getElementsAnnotatedWith(DynamicRestMapping.class)) {
            Element controller = dynamicRestMapping.getEnclosingElement();
            if (!dynamicRestControllers.containsKey(controller))
                dynamicRestControllers.put(controller, new HashSet<>());
            if (dynamicRestMapping instanceof ExecutableElement)
                dynamicRestControllers.get(controller).add((ExecutableElement) dynamicRestMapping);
        }

        // after all controllers that contain dynamic rest mapping annotation have been resolved,
        // build each controller
        for (Element dynamicRestController : dynamicRestControllers.keySet()) {
            TypeElement dynamicRestControllerTypeElement = TypeMirrorUtils.getTypeElement(dynamicRestController, processingEnv);
            if (dynamicRestControllerTypeElement == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not get TypeMirror for controller: [" + dynamicRestController.getSimpleName().toString() + "]");
                return false;
            }
            PackageElement packageElement = (PackageElement) dynamicRestController.getEnclosingElement();
            String controllerName = dynamicRestController.getSimpleName().toString();
            try {
                String packageName = packageElement.getQualifiedName().toString() + ".restprocessor.generated";
                ClassBuilder classBuilder = new ClassBuilder(processingEnv, packageName, controllerName);

                classBuilder.setPackageName(packageName);

                classBuilder.addImports(RHSColonExpression.class, MultiColumnSort.class, Filter.class, FilterFactory.class, CriteriaParameters.class, GetMapping.class, RequestParam.class, ArrayList.class, List.class);
                classBuilder.addImports(Component.class, Autowired.class, Schema.class, Parameter.class, ExampleObject.class);

                classBuilder.appendToBody("\n");

                // get all annotations
                // filter @Component out and give a new name to the Bean to avoid conflicts with the existing controller bean
                // add them to the generated class
                classBuilder.appendToBody(dynamicRestControllerTypeElement.getAnnotationMirrors().stream().filter(a -> !TypeMirrorUtils.matchesClassName(TypeMirrorUtils.getTypeElement(a.getAnnotationType(), processingEnv), Component.class)).map(Object::toString).collect(Collectors.joining("\n")));
                classBuilder.appendToBody("\n@Component(\"" + controllerName + "_RestProcessor_" + UUID.randomUUID() + "\")");
                //

                classBuilder.appendToBody("\n")
                        .appendToBody("public class ")
                        .appendToBody(controllerName)
                        .appendToBody(" {")
                        .appendToBody("\n")
                        .appendToBody("\n\t@Autowired")
                        .appendToBody("\n\t").appendToBody(dynamicRestControllerTypeElement.getQualifiedName().toString())
                        .appendToBody(" controller_;")
                        .appendToBody("\n");

                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, String.valueOf(dynamicRestController.getEnclosedElements().size()));
                // build each mapping
                for (ExecutableElement e : dynamicRestControllers.get(dynamicRestController)) {
                    DynamicRestMapping annotation = e.getAnnotation(DynamicRestMapping.class);
                    if (annotation != null) { // always true since we got dynamicRestControllers from getElementsAnnotatedWith(DynamicRestMapping.class)

                        // get all annotations
                        // filter @Component out and give a new name to the Bean to avoid conflicts with the existing controller bean
                        // add them to the generated class
                        classBuilder.appendToBody("\n\t").appendToBody(e.getAnnotationMirrors().stream().filter(a -> !TypeMirrorUtils.matchesAnyClassName(TypeMirrorUtils.getTypeElement(a.getAnnotationType(), processingEnv), DynamicRestMapping.class, annotation.requestMethod().getRequestMethodAnnotation())).map(Object::toString).collect(Collectors.joining("\n\t")));
                        classBuilder.addImport(annotation.requestMethod().getClass());
                        // get custom params
                        // check if there are conflicts
                        // generate documentation
                        // call child method with requested params
                        classBuilder.appendToBody("\n\t@").appendToBody(String.valueOf(annotation.requestMethod())).appendToBody("({").appendToBody(Arrays.stream(annotation.path()).map(s -> "\"" + s + "\"").collect(Collectors.joining(", "))).appendToBody("})");
                        classBuilder.appendToBody("\n");
                        // ((TypeElement)roundEnv.getRootElements().toArray()[0]).getEnclosedElements()

                        // get entityClass from entity defined in @DynamicRestMapping
                        TypeElement entityTypeElement = TypeMirrorUtils.getTypeElement(annotation, DynamicRestMapping::entity, processingEnv);
                        if (entityTypeElement == null) {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not get TypeMirror from DynamicGetMapping::entity");
                            return false;
                        }
                        classBuilder.addImport(entityTypeElement.getQualifiedName().toString());
                        Hashtable<String, FieldDetails> fields = processorFieldDetailsRegistry.lookup(entityTypeElement.getQualifiedName().toString());
                        if (fields == null) {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Could not get FieldDetails for " + entityTypeElement.getQualifiedName() + ". The entity is not registered! Try rebuilding the project. Make sure the class specified in @DynamicRestController is in fact marked as @Entity.");
                            return false;
                        }
//                        Set<FilterRequestParameter> filterParams = new HashSet<>();
//                        // fields are considered all elements that:
//                        // are of kind field
//                        // are not static or final
//                        // transient fields are allowed since we can have non-persistent/"in memory" fields that we might need to filter by
//                        List<VariableElement> entityFields = entityClass.get().getEnclosedElements().stream().filter(e_ -> (e_.getKind().isField() && !e_.getModifiers().contains(Modifier.STATIC) && !e_.getModifiers().contains(Modifier.FINAL))).map(e_ -> (VariableElement) e_).toList();
//                        for (VariableElement entityField : entityFields) {
//                            filterParams.add(new FilterRequestParameter(entityField.getSimpleName().toString(), entityField.asType(), false));
//                        }
//                        for (FieldDetails fieldDetails : fields.values()) {
//                            filterParams.add(new FilterRequestParameter(fieldDetails.getApiName(), fieldDetails.getType(), fieldDetails.isRequired()));
//                        }

                        Hashtable<String, String> methodParameters = new Hashtable<>();

                        StringBuilder filterBuilder = new StringBuilder();
                        filterBuilder.append("\t\tList<Filter<?>> filters = new ArrayList<>();");

                        for (FieldDetails filterParamFieldDetails : fields.values()) {
                            if (!filterParamFieldDetails.isFilterable())
                                continue;
                            StringBuilder methodParamStringBuilder = new StringBuilder();
                            String paramName = filterParamFieldDetails.getUniqueAPINameUnderscore() + "Filters";

                            methodParamStringBuilder.append("@Parameter(name =\"").append(filterParamFieldDetails.getUniqueAPIName()).append("\", schema = @Schema(description = \"\", type = \"string\"), required = ").append(filterParamFieldDetails.isRequired()).append(" ) @RequestParam(name = \"").append(filterParamFieldDetails.getUniqueAPIName()).append("\", required = ").append(filterParamFieldDetails.isRequired()).append(") List<RHSColonExpression> ").append(paramName);
                            methodParameters.put(paramName, methodParamStringBuilder.toString());
//                            ((TypeElement)processingEnv.getTypeUtils().asElement(filterParamFieldDetails.getFilterClassType())).getQualifiedName();
                            if (TypeMirrorUtils.matchesClassName(filterParamFieldDetails.getFilterClassType(), String.class))
                                filterBuilder.append("\n\t\tfilters.addAll(FilterFactory.getStringFiltersFromRHSColonExpression(\"").append(filterParamFieldDetails.getPersistencePathAsString()).append("\", ").append(paramName).append("));");
                            else if (filterParamFieldDetails.getElementKind().equals(ElementKind.ENUM))
                                filterBuilder.append("\n\t\tfilters.addAll(FilterFactory.getEnumFiltersFromRHSColonExpression(").append(filterParamFieldDetails.getFilterClassType().getQualifiedName()).append(".class, \"").append(filterParamFieldDetails.getPersistencePathAsString()).append("\", ").append(paramName).append("));");
                            else if (TypeMirrorUtils.matchesClassName(filterParamFieldDetails.getFilterClassType(), LocalDateTime.class))
                                filterBuilder.append("\n\t\tfilters.addAll(FilterFactory.getLocalDateTimeFiltersFromRHSColonExpression(\"").append(filterParamFieldDetails.getPersistencePathAsString()).append("\", ").append(paramName).append("));");
                            else if (TypeMirrorUtils.instanceOf(filterParamFieldDetails.getFilterClassType(), Number.class, processingEnv))
                                filterBuilder.append("\n\t\tfilters.addAll(FilterFactory.getNumericFiltersFromRHSColonExpression(").append(filterParamFieldDetails.getFilterClassType().getQualifiedName()).append(".class, \"").append(filterParamFieldDetails.getPersistencePathAsString()).append("\", ").append(paramName).append("));");
                        }

                        List<MethodParameter> returnParameters = getDeclaredParameters(e);
                        for (MethodParameter methodParameter : returnParameters) {
                            methodParameters.put(methodParameter.name, methodParameter.toString());
                        }
                        // note this must be after the loop that adds returnParameters to methodParameters
                        returnParameters.add(0, new MethodParameter("cp", null, null));

//                        TODO[swagger/documentation] document parameters
//                        @Parameter(name ="sortBy", examples = {@ExampleObject("+price"), @ExampleObject("price"), @ExampleObject("-price"), @ExampleObject("price;id"), @ExampleObject("-id;+price")}, schema = @Schema(description = "var 1", type = "string"))
                        classBuilder.appendToBody("\tpublic ").appendToBody(e.getReturnType().toString()).appendToBody(" ").appendToBody(String.valueOf(e.getSimpleName())).appendToBody("(@RequestParam(name = \"pageSize\", defaultValue = \"30\") int pageSize, @RequestParam(name = \"pageNumber\", defaultValue = \"0\") int pageNumber, @Parameter(name =\"sortBy\", examples = {@ExampleObject(\"+price\"), @ExampleObject(\"price\"), @ExampleObject(\"-price\"), @ExampleObject(\"price;id\"), @ExampleObject(\"-id;+price\")}, schema = @Schema(description = \"\", type = \"string\")) @RequestParam(name = \"sortBy\", required = false) MultiColumnSort sortBy, ").appendToBody(methodParameters.values().stream().map(Object::toString)
                                        .collect(Collectors.joining(", "))).appendToBody("){").appendToBody("\n")
                                .appendToBody(filterBuilder.toString())
                                .appendToBody("\n\t\tCriteriaParameters cp = new CriteriaParameters(pageNumber, pageSize, sortBy, filters);");
                        // old impl with extend
//                        classBuilder.appendToBody("\n\t\treturn ((").appendToBody(controllerName).appendToBody(") this).").appendToBody(String.valueOf(e.getSimpleName())).appendToBody("(").appendToBody(returnParameters.stream().map(o -> o.name).collect(Collectors.joining(", "))).appendToBody(");");
                        classBuilder.appendToBody("\n\t\treturn this.controller_.").appendToBody(String.valueOf(e.getSimpleName())).appendToBody("(").appendToBody(returnParameters.stream().map(o -> o.name).collect(Collectors.joining(", "))).appendToBody(");");
                        classBuilder.appendToBody("\n\t}");

//                        TODO[metadata]
//                        classBuilder.appendToBody("""
//                                \n\t@org.springframework.web.bind.annotation.GetMapping("public/products/$metadata")
//                                \tpublic MetadataResponse getAllProductsMetadata(){
//                                \t\treturn new MetadataResponse(FieldDetailsRegistry.instance().lookup(Product.class), GetModerateProductDTO.class);
//                                \t}""");
                    }
                }

                classBuilder.appendToBody("\n}");
                classBuilder.build();
            } catch (IOException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
                return false;
            }

        }

        try {
            generateFieldsRegistryClass();
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
            return false;
        }
        try {
            processorFieldDetailsRegistry.serialize(processingEnv);
        } catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.toString());
            return false;
        }
        return true;
    }

    private void buildDynamicQueryRepository(TypeElement entityTypeElement) {
        PackageElement packageElement = (PackageElement) entityTypeElement.getEnclosingElement();
        String entityName = entityTypeElement.getSimpleName().toString();
        String generatedRepositoryName = entityName + "DynamicQueryRepository";

        ClassBuilder classBuilder = null;
        try {
            classBuilder = new ClassBuilder(processingEnv, packageElement.getQualifiedName().toString(), generatedRepositoryName);
            classBuilder.setPackageName(packageElement.getQualifiedName().toString());
            classBuilder.addImports(DynamicQueryRepository.class);
            classBuilder.appendToBody("public interface ")
                    .appendToBody(generatedRepositoryName)
                    .appendToBody(" extends DynamicQueryRepository<")
                    .appendToBody(entityTypeElement.getQualifiedName().toString())
                    .appendToBody("> {}\n");
            classBuilder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            classBuilder = new ClassBuilder(processingEnv, packageElement.getQualifiedName().toString(), generatedRepositoryName + "Impl");
            classBuilder.setPackageName(packageElement.getQualifiedName().toString());
            classBuilder.addImports(Stream.class, CriteriaParameters.class, FunctionArg.class, Hashtable.class, Repository.class, Page.class, Autowired.class, EntityManager.class, MultiColumnSort.class, List.class, Filter.class, DynamicQueryRepositoryUtils.class);
            classBuilder.appendToBody("\n@Repository")
                    .appendToBody("\npublic class " + generatedRepositoryName + "Impl implements " + packageElement.getQualifiedName().toString() + "." + generatedRepositoryName + " {")
                    .appendToBody("\n\t@Autowired")
                    .appendToBody("\n\tEntityManager em;")

                    .appendToBody("\n\n\t@Override")
                    .appendToBody("\n\tpublic Page<")
                    .appendToBody(entityTypeElement.getQualifiedName().toString())
                    .appendToBody("> findAllByCriteria(CriteriaParameters cp) {")
                    .appendToBody("\n\t\treturn DynamicQueryRepositoryUtils.findAllByCriteria(")
                    .appendToBody(entityTypeElement.getQualifiedName().toString() + ".class, em, ")
                    .appendToBody(DEFAULT_PACKAGE_NAME + ".FieldDetailsRegistry.instance().lookup(")
                    .appendToBody(entityTypeElement.getQualifiedName().toString() + ".class), cp);")
                    .appendToBody("\n\t}")

                    .appendToBody("\n\n\t@Override")
                    .appendToBody("\n\tpublic Page<")
                    .appendToBody(entityTypeElement.getQualifiedName().toString())
                    .appendToBody("> findAllByCriteria(int page, int size, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {")
                    .appendToBody("\n\t\treturn DynamicQueryRepositoryUtils.findAllByCriteria(")
                    .appendToBody(entityTypeElement.getQualifiedName().toString() + ".class, em, page, size, sortBy, filters, ")
                    .appendToBody(DEFAULT_PACKAGE_NAME + ".FieldDetailsRegistry.instance().lookup(")
                    .appendToBody(entityTypeElement.getQualifiedName().toString() + ".class), sortByFunctionArgs);")
                    .appendToBody("\n\t}")

                    .appendToBody("\n\n\t@Override")
                    .appendToBody("\n\tpublic Stream<")
                    .appendToBody(entityTypeElement.getQualifiedName().toString())
                    .appendToBody("> findAllByCriteriaAsStream(CriteriaParameters cp) {")
                    .appendToBody("\n\t\treturn DynamicQueryRepositoryUtils.findAllByCriteriaAsStream(")
                    .appendToBody(entityTypeElement.getQualifiedName().toString() + ".class, em, ")
                    .appendToBody(DEFAULT_PACKAGE_NAME + ".FieldDetailsRegistry.instance().lookup(")
                    .appendToBody(entityTypeElement.getQualifiedName().toString() + ".class), cp);")
                    .appendToBody("\n\t}")

                    .appendToBody("\n\n\t@Override")
                    .appendToBody("\n\tpublic Stream<")
                    .appendToBody(entityTypeElement.getQualifiedName().toString())
                    .appendToBody("> findAllByCriteriaAsStream(int page, int size, MultiColumnSort sortBy, List<Filter<?>> filters, Hashtable<String, FunctionArg[]> sortByFunctionArgs) {")
                    .appendToBody("\n\t\treturn DynamicQueryRepositoryUtils.findAllByCriteriaAsStream(")
                    .appendToBody(entityTypeElement.getQualifiedName().toString() + ".class, em, sortBy, filters, ")
                    .appendToBody(DEFAULT_PACKAGE_NAME + ".FieldDetailsRegistry.instance().lookup(")
                    .appendToBody(entityTypeElement.getQualifiedName().toString() + ".class), sortByFunctionArgs);")
                    .appendToBody("\n\t}")

                    .appendToBody("\n}");
            classBuilder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private String buildImports(Class<?>... classes) {
        Set<String> imports_ = new HashSet<>();
        for (Class<?> c : classes)
            imports_.add("import " + c.getCanonicalName() + ";\n");
        return String.join("", imports_);
    }

    private void generateFieldsRegistryClass() throws IOException {
        ClassBuilder classBuilder = new ClassBuilder(processingEnv, DEFAULT_PACKAGE_NAME, "FieldDetailsRegistry");
        classBuilder.setPackageName(DEFAULT_PACKAGE_NAME);
        classBuilder.addImports(Hashtable.class, FieldDetails.class, Objects.class, FileObject.class);
        classBuilder.addImport("java.io.*");
        classBuilder.appendToBody("""
                                
                public class FieldDetailsRegistry {
                 
                     private static FieldDetailsRegistry instance = new FieldDetailsRegistry();
                     private Hashtable<String, Hashtable<String, IRestFieldDetails>> registry = new Hashtable<>();
                 
                     private FieldDetailsRegistry() {
                         deserialize();
                     }
                 
                     public static FieldDetailsRegistry instance() {
                         return instance;
                     }
                 
                     public Hashtable<String, IRestFieldDetails> lookup(Class<?> class_) {
                         return registry.getOrDefault(class_.getCanonicalName(), null);
                     }
                 
                     public void bind(Class<?> class_, Hashtable<String, IRestFieldDetails> fieldDetailsSet) {
                         registry.put(class_.getCanonicalName(), fieldDetailsSet);
                     }
                     
                     public void bindField(Class<?> class_, IRestFieldDetails fieldDetails) {
                         if (lookup(class_) == null)
                             registry.put(class_.getCanonicalName(), new Hashtable<>());
                         if (lookup(class_).containsKey(fieldDetails.getUniqueAPIName()))
                             throw new RuntimeException("Duplicate key [" + fieldDetails.getUniqueAPIName() + "] for class [" + class_.getCanonicalName() + "]");
                         lookup(class_).put(fieldDetails.getUniqueAPIName(), fieldDetails);
                     }
                     public void unBindField(Class<?> class_, String key) {
                         if (lookup(class_) != null)
                                 lookup(class_).remove(key);
                     }
                     
                     protected void deserialize() {
                          try {
                              InputStream fileInput;
                              try { // when packaged into jar
                                  fileInput = FieldDetailsRegistry.class.getResourceAsStream("./persist/field_details_registry.data");
                                  if (fileInput == null)
                                      throw new NullPointerException();
                              } catch (NullPointerException ex) { // try to retrieve it locally if not packaged into jar
                                  fileInput = new FileInputStream(FieldDetailsRegistry.class.getProtectionDomain().getCodeSource().getLocation().getFile() + \"""" + DEFAULT_PACKAGE_NAME.replaceAll("\\.", "/") +
                """
                        /persist/field_details_registry.data");
                                      }
                                      Objects.requireNonNull(fileInput);
                         
                                      ObjectInputStream objectInput
                                              = new ObjectInputStream(fileInput);
                         
                                      registry = (Hashtable<String, Hashtable<String, IRestFieldDetails>>) objectInput.readObject();
                         
                                      objectInput.close();
                                      fileInput.close();
                                  } catch (IOException obj1) {
                                      obj1.printStackTrace();
                                      return;
                                  } catch (ClassNotFoundException obj2) {
                                      System.out.println("Class not found");
                                      obj2.printStackTrace();
                                      return;
                                  }
                             }
                         }
                        """);
        classBuilder.build();
    }


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(List.of("org.indritbreti.restprocessor.EnableRestProcessor", "org.indritbreti.restprocessor.DynamicRestMapping", "org.indritbreti.restprocessor.RESTField", "org.indritbreti.restprocessor.JoinRESTField"));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_17;
    }


    private List<MethodParameter> getDeclaredParameters(ExecutableElement executableElement) {
        List<? extends VariableElement> parameters = executableElement.getParameters();
        List<MethodParameter> methodParameters = new ArrayList<>();
        for (VariableElement param : parameters) {
            TypeElement parameterType = TypeMirrorUtils.getTypeElement(param.asType(), processingEnv);
            if (parameterType != null && parameterType.getQualifiedName().toString().equals(CriteriaParameters.class.getCanonicalName()))
                continue;
            methodParameters.add(new MethodParameter(param.getSimpleName().toString(), param.asType(), param.getAnnotationMirrors()));
        }
        return methodParameters;
    }

    public static String getDefaultPackageName() {
        return DEFAULT_PACKAGE_NAME;
    }
}
