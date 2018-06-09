package com.hackathon.infomax.pdftool;

public class CompareFieldInfo {
    private String propertyName;
    private String expectedValue;
    private String actualValue;

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getActualValue() {
        return actualValue;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
