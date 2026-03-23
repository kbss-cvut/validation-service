package cz.cvut.kbss.validation.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationResult {

    private String focusNode;

    private String sourceShape;

    private String severity;

    private String path;

    private Map<String, String> message;

    public String getFocusNode() {
        return focusNode;
    }

    public void setFocusNode(String focusNode) {
        this.focusNode = focusNode;
    }

    public String getSourceShape() {
        return sourceShape;
    }

    public void setSourceShape(String sourceShape) {
        this.sourceShape = sourceShape;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getMessage() {
        return message;
    }

    public void setMessage(Map<String, String> message) {
        this.message = message;
    }
}
