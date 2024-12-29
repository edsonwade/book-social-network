package code.with.vanilson.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {
    ACTIVATE_ACCOUNT("activate_account");

    private final String templateName;

    EmailTemplateName(String name) {
        this.templateName = name;
    }
}
