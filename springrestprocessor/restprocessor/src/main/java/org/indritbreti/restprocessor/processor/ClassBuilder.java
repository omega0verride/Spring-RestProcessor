package org.indritbreti.restprocessor.processor;

import org.indritbreti.restprocessor.Utilities;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ClassBuilder {
    private final BufferedWriter bufferedWriter;

    private String packageName = null;
    private Set<String> imports = new HashSet<>();

    private final StringBuilder classBody = new StringBuilder();
    public ClassBuilder(ProcessingEnvironment processingEnvironment, String generatedSourcesPackageName, String fileName) throws IOException {
        JavaFileObject f = processingEnvironment.getFiler().createSourceFile(generatedSourcesPackageName+"."+fileName);
        bufferedWriter = new BufferedWriter(f.openWriter());
    }

    public void build() throws IOException {
        if (!Utilities.isNullOrEmpty(packageName)) {
            bufferedWriter.append("// source code generated from Spring-RestProcessor build.0002");
            bufferedWriter.append("\n");
            bufferedWriter.append("package ");
            bufferedWriter.append(packageName);
            bufferedWriter.append(";");
            bufferedWriter.newLine();
        }
        buildImports();
        bufferedWriter.append(classBody);
        bufferedWriter.close();
    }

    private void buildImports() throws IOException {
        for (String c : imports)
            bufferedWriter.append("import ").append(c).append(";\n");
        bufferedWriter.newLine();
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Set<String> getImports() {
        return imports;
    }

    public void setImports(Set<String> imports) {
        this.imports = imports;
    }

    public void addImports(Class<?>... class_){
        imports.addAll(Stream.of(class_).map(Class::getCanonicalName).toList());
    }

    public void addImport(Class<?> class_){
        addImports(class_);
    }

    public void addImport(String class_){
        imports.add(class_);
    }
    public ClassBuilder appendToBody(String str){
        classBody.append(str);
        return this;
    }

    public StringBuilder getBody(){
        return classBody;
    }
}
