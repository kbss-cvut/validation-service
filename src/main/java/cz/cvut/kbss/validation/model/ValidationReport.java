package cz.cvut.kbss.validation.model;

import java.util.List;

public record ValidationReport(boolean conforms, List<ValidationResult> results) {

    public ValidationReport(List<ValidationResult> results) {
        this(results.isEmpty(), results);
    }
}
