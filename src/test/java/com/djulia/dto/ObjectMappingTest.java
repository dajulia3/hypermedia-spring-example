package com.djulia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ObjectMappingTest {
    private ObjectMapper mapper;

    @Before
    public void setup(){
        mapper = Jackson2ObjectMapperBuilder.json().build();
        mapper.addMixIn(ErrorResponse.class, ErrorResponseMixin.class);
    }

    @Test
    public void testMapping() throws IOException {
        String expected = "{error:{message:\"Bad news\", type:\"UNEXPECTED_ERROR\"}}";

        ErrorResponse originalError = new ErrorResponse("Bad news", "UNEXPECTED_ERROR");
        String marshalled = mapper.writeValueAsString(originalError);
        JSONAssert.assertEquals(expected, marshalled, true);

        ErrorResponse errorResponse = mapper.readValue(marshalled, ErrorResponse.class);
        assertThat(errorResponse, equalTo(originalError));

    }
    @Test
    public void testMapping_subclassWithExtraFields() throws IOException {
        String expected = "{error:{message:\"there were validation problems!\", type:\"VALIDATION_ERROR\", validationErrorStuff:\"some extra stuff\"}}";

        ErrorResponse originalError = new ValidationErrorResponse("there were validation problems!", "VALIDATION_ERROR", "some extra stuff");
        String marshalled = mapper.writeValueAsString(originalError);
        System.out.println("marshalled = " + marshalled);
        JSONAssert.assertEquals(expected, marshalled, true);

        ValidationErrorResponse errorResponse = mapper.readValue(marshalled, ValidationErrorResponse.class);
        assertThat(errorResponse, equalTo(originalError));

    }


    @JsonSerialize(using= CustomSerializer.class)
    @JsonDeserialize(using=CustomDeserializer.class)
    public static abstract class ErrorResponseMixin{

    }

    public static class CustomSerializer extends JsonSerializer<ErrorResponse> {

        @Override
        public Class handledType() {
            return ErrorResponse.class;
        }

        @Override
        public void serializeWithType(ErrorResponse value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
            serialize(value, gen, serializers);
        }

        @Override
        public void serialize(ErrorResponse value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            ObjectWriter writer = Jackson2ObjectMapperBuilder.json().build().writer();
            gen.writeStartObject();
                gen.writeFieldName("error");
                    gen.writeRawValue(writer.writeValueAsString(value));
            gen.writeEndObject();
        }
    }

    public static class CustomDeserializer extends JsonDeserializer<ErrorResponse> {

        @Override
        public Class handledType() {
            return ErrorResponse.class;
        }

        @Override
        public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
//            JsonNode node = p.getCodec().readTree(p);
//            p.next
//            p.ne
            p.nextFieldName(); //skip the error field (the wrapper).
            Object o = typeDeserializer.deserializeTypedFromObject(p, ctxt);
            p.getCurrentLocation();
//            JsonNode error = node.get("error");
            TypeIdResolver typeIdResolver = typeDeserializer.getTypeIdResolver();
//            Jackson2ObjectMapperBuilder.json().build().readValue(p, typeDeserializer.getTypeIdResolver().typeFromId())
//            typeDeserializer.deserializeTypedFromObject
            System.out.println("typeIdResolver = " + typeIdResolver);
            return deserialize(p, ctxt);
        }

        @Override
        public ErrorResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            JsonNode node = p.getCodec().readTree(p);
            JsonNode error = node.get("error");
            ObjectReader reader = Jackson2ObjectMapperBuilder.json().build().reader().forType(ErrorResponse.class);

            return reader.readValue(error);
        }

    }

    public static class ValidationErrorResponse extends ErrorResponse{
        String validationErrorStuff;

        public ValidationErrorResponse(String message, String type, String validationErrorStuff) {
            super(message, type);
            this.validationErrorStuff = validationErrorStuff;
        }

        public ValidationErrorResponse() {
        }

        public String getValidationErrorStuff() {
            return validationErrorStuff;
        }

        public void setValidationErrorStuff(String validationErrorStuff) {
            this.validationErrorStuff = validationErrorStuff;
        }

        @Override
        public String toString() {
            return "ValidationErrorResponse{" +
                    "validationErrorStuff='" + validationErrorStuff + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            ValidationErrorResponse that = (ValidationErrorResponse) o;

            return validationErrorStuff != null ? validationErrorStuff.equals(that.validationErrorStuff) : that.validationErrorStuff == null;

        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (validationErrorStuff != null ? validationErrorStuff.hashCode() : 0);
            return result;
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = ErrorResponse.class, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
    @JsonSubTypes(value = @JsonSubTypes.Type(value=ValidationErrorResponse.class, name="VALIDATION_ERROR"))
    public static class ErrorResponse{

        private @JsonProperty String message;
        private @JsonProperty String type;

        public ErrorResponse() {
        }

        public ErrorResponse(String message, String type) {
            this.message = message;
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "ErrorResponse{" +
                    "message='" + message + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ErrorResponse that = (ErrorResponse) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;
            return type != null ? type.equals(that.type) : that.type == null;

        }

        @Override
        public int hashCode() {
            int result = message != null ? message.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }
    }
}
