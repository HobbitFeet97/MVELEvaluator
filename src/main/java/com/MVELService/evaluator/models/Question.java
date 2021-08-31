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
    private String clearValueExpression;
    private Argument[] args;
    private Answer[] answers;
    private String answerExpression;

    public Question(String id, String label, String bdp, String[] value, Boolean visible, String visibleExpression, Boolean readOnly, String readOnlyExpression, String clearValueExpression, Argument[] args, Answer[] answers, String answerExpression) {
        this.id = id;
        this.label = label;
        this.bdp = bdp;
        this.value = value;
        this.visible = visible;
        this.visibleExpression = visibleExpression;
        this.readOnly = readOnly;
        this.readOnlyExpression = readOnlyExpression;
        this.clearValueExpression = clearValueExpression;
        this.args = args;
        this.answers = answers;
        this.answerExpression = answerExpression;
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

    public String getClearValueExpression() {
        return clearValueExpression;
    }

    public void setClearValueExpression(String clearValueExpression) {
        this.clearValueExpression = clearValueExpression;
    }

    public Answer[] getAnswers() {
        return answers;
    }

    public void setAnswers(Answer[] answers) {
        this.answers = answers;
    }

    public String getAnswerExpression() {
        return answerExpression;
    }

    public void setAnswerExpression(String answerExpression) {
        this.answerExpression = answerExpression;
    }
}
