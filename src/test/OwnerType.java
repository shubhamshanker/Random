package test;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum OwnerType {
    INDIVIDUAL("Individual"),
    ASSOCIATION_OF_PERSON("Association Of Person");

    private String text;

    OwnerType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
