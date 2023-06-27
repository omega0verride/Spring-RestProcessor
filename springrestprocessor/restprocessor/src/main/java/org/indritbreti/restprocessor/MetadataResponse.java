package org.indritbreti.restprocessor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;

import java.util.Hashtable;

public class MetadataResponse {
    JsonSchema schema;
    Hashtable<String, FieldDetails> fieldDetails;

    public MetadataResponse(Hashtable<String, FieldDetails> fieldDetails, Class<?> responseEntityClass) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        try {
            schema = schemaGen.generateSchema(responseEntityClass);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
        this.fieldDetails=fieldDetails;
    }


}
