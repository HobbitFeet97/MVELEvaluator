package com.MVELService.evaluator.models;

public class Question {

    private final String id;
    private String label;
    private String bdp;
    private String[] value;
    private Boolean visible;
    private String visibleExpression;
    private Boolean readOnly;
    private String readOnlyExpression;
    private Argument[] args;

    public Question(String id, String label, String bdp, String[] value, Boolean visible, String visibleExpression, Boolean readOnly, String readOnlyExpression, Argument[] args) {
        this.id = id;
        this.label = label;
        this.bdp = bdp;
        this.value = value;
        this.visible = visible;
        this.visibleExpression = visibleExpression;
        this.readOnly = readOnly;
        this.readOnlyExpression = readOnlyExpression;
        this.args = args;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getBdp() {
        return bdp;
    }

    public void setBdp(String bdp) {
        this.bdp = bdp;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public String getVisibleExpression() {
        return visibleExpression;
    }

    public void setVisibleExpression(String visibleExpression) {
        this.visibleExpression = visibleExpression;
    }

    public Argument[] getArgs() {
        return args;
    }

    public void setArgs(Argument[] args) {
        this.args = args;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getReadOnlyExpression() {
        return readOnlyExpression;
    }

    public void setReadOnlyExpression(String readOnlyExpression) {
        this.readOnlyExpression = readOnlyExpression;
    }
}
