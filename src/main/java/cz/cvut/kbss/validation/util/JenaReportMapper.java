package cz.cvut.kbss.validation.util;

import com.github.sgov.server.ValidationResultSeverityComparator;
import cz.cvut.kbss.validation.model.ValidationReport;
import cz.cvut.kbss.validation.model.ValidationResult;
import org.apache.jena.shacl.validation.ReportEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class JenaReportMapper {

    public static ValidationReport map(org.apache.jena.shacl.ValidationReport report) {
        Objects.requireNonNull(report);
        final List<ValidationResult> results = report.getEntries().stream()
                                                     .sorted(new ValidationResultSeverityComparator())
                                                     .distinct()
                                                     .map(JenaReportMapper::mapResult).toList();
        return new ValidationReport(results);
    }

    private static ValidationResult mapResult(ReportEntry entry) {
        final ValidationResult result = new ValidationResult();
        result.setSeverity(entry.severity().level().getURI());
        result.setFocusNode(entry.focusNode().getURI());
        result.setMessage(new HashMap<>());
        entry.messages().forEach(n -> result.getMessage().put(n.getLiteralLanguage(), n.getLiteralLexicalForm()));
        if (entry.source() != null && !entry.source().isBlank()) {
            result.setSourceShape(entry.source().getURI());
        }
        if (entry.resultPath() != null) {
            result.setPath(entry.resultPath().toString());
        }
        return result;
    }
}
