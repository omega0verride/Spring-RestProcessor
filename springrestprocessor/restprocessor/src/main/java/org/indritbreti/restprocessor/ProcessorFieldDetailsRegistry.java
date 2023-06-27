package org.indritbreti.restprocessor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.*;
import java.net.URL;
import java.util.Hashtable;
import java.util.Objects;


public class ProcessorFieldDetailsRegistry {

    private static ProcessorFieldDetailsRegistry instance;
    private String generatedSourcesPackageName;
    private Hashtable<String, Hashtable<String, FieldDetails>> registry = new Hashtable<>();

    private ProcessorFieldDetailsRegistry() {

    }

    private ProcessorFieldDetailsRegistry(String generatedSourcesPackageName, boolean loadSerializedData) {
        this.generatedSourcesPackageName = generatedSourcesPackageName;
        if (loadSerializedData)
            deserialize();
    }

    public static ProcessorFieldDetailsRegistry instance(String generatedSourcesPackageName) {
        return instance(generatedSourcesPackageName, false);
    }

    public static ProcessorFieldDetailsRegistry instance(String generatedSourcesPackageName, boolean loadSerializedData) {
        if (loadSerializedData)
            instance = new ProcessorFieldDetailsRegistry(generatedSourcesPackageName, true);
        if (instance == null)
            instance = new ProcessorFieldDetailsRegistry(generatedSourcesPackageName, false);
        return instance;
    }

    public Hashtable<String, FieldDetails> lookup(Class<?> class_) {
        return registry.getOrDefault(class_.getCanonicalName(), null);
    }

    public Hashtable<String, FieldDetails> lookup(String class_) {
        return registry.getOrDefault(class_, null);
    }

    public void bind(Class<?> class_, Hashtable<String, FieldDetails> fieldDetailsSet) {
        registry.put(class_.getCanonicalName(), fieldDetailsSet);
    }

    protected void bind(String class_, Hashtable<String, FieldDetails> fieldDetailsSet) {
        registry.put(class_, fieldDetailsSet);
    }

    protected void serialize(ProcessingEnvironment processingEnvironment) throws IOException {
        FileObject file = processingEnvironment.getFiler().createResource(StandardLocation.CLASS_OUTPUT, generatedSourcesPackageName+".persist", "field_details_registry.data");
        OutputStream myFileOutStream = file.openOutputStream();
        ObjectOutputStream myObjectOutStream
                = new ObjectOutputStream(myFileOutStream);
        myObjectOutStream.writeObject(registry);
        myObjectOutStream.close();
        myFileOutStream.close();
    }

    protected void deserialize() {
        try {
            // when inside jar
            URL filePath = this.getClass().getResource("./persist/field_details_registry.data");
            if (filePath == null) // try to retrieve it locally if not packaged into jar
                filePath = this.getClass().getResource("./" + generatedSourcesPackageName + ".persist/field_details_registry.data");
            FileInputStream fileInput = new FileInputStream(Objects.requireNonNull(filePath).getFile());

            ObjectInputStream objectInput
                    = new ObjectInputStream(fileInput);

            registry = (Hashtable<String, Hashtable<String, FieldDetails>>) objectInput.readObject();

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
