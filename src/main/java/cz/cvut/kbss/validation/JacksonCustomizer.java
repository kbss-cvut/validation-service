package cz.cvut.kbss.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import cz.cvut.kbss.validation.util.ValidationReportSerializer;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.inject.Singleton;
import org.topbraid.shacl.validation.ValidationReport;

@Singleton
public class JacksonCustomizer implements ObjectMapperCustomizer {
    @Override
    public void customize(ObjectMapper objectMapper) {
        objectMapper.registerModule(
                new SimpleModule().addSerializer(ValidationReport.class, new ValidationReportSerializer()));
    }
}
