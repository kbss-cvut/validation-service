package cz.cvut.kbss.validation.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import cz.cvut.kbss.validation.exception.ValidatorException;
import org.apache.jena.rdf.model.RDFNode;
import org.topbraid.shacl.validation.ValidationReport;

import java.io.IOException;

public class ValidationReportSerializer extends JsonSerializer<ValidationReport> {

    @Override
    public void serialize(ValidationReport value, JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeBooleanField("conforms", value.conforms());
        gen.writeFieldName("results");
        gen.writeStartArray();
        value.results().forEach(r -> {
            try {
                gen.writeStartObject();
                gen.writeStringField("severity", r.getSeverity().getURI());
                gen.writeFieldName("message");
                gen.writeStartObject();
                for (RDFNode m : r.getMessages()) {
                    gen.writeStringField(m.asLiteral().getLanguage(), m.asLiteral().getLexicalForm());
                }
                gen.writeEndObject();
                gen.writeStringField("focusNode", r.getFocusNode().toString());
                gen.writeStringField("sourceShape", r.getSourceShape().getURI());
                if (r.getPath() != null) {
                    gen.writeStringField("path", r.getPath().getURI());
                }
                gen.writeEndObject();
            } catch (IOException e) {
                throw new ValidatorException("Unable to serialize validation report.", e);
            }
        });
        gen.writeEndArray();
        gen.writeEndObject();
    }
}
