package com.hackathon.infomax.pdftool;

public class ValueMataData {
    private String id;
    private String fontSize;
    private String value;
    private boolean isNumber;
    private String fontFamily;
    private String rawDom;

    public ValueMataData(){}

    public ValueMataData(String id, String value){
        this.id = id;
        this.value = value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public void setNumber(boolean number) {
        isNumber = number;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFontSize() {
        return fontSize;
    }

    public String getValue() {
        return value;
    }

    public boolean isNumber() {
        return isNumber;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setRawDom(String rawDom) {
        this.rawDom = rawDom;
    }

    public String getRawDom() {
        return rawDom;
    }
}
