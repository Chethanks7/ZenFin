package com.ZenFin.email;

import lombok.Getter;

@Getter
public enum EmailTemplateName {

    ACTIVATE_ACCOUNT("activate_account");

    public final String name ;
    private EmailTemplateName(final String name) {
        this.name = name;
    }

}