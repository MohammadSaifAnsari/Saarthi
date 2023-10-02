package com.saif.languagetranslatorapp.Model;

public class LanguageModel {

    String langCode,langTitle;

    public LanguageModel(String langCode, String langTitle) {
        this.langCode = langCode;
        this.langTitle = langTitle;
    }

    public String getLangCode() {
        return langCode;
    }

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public String getLangTitle() {
        return langTitle;
    }

    public void setLangTitle(String langTitle) {
        this.langTitle = langTitle;
    }
}
