package org.burgas.corporateservice.message;

import lombok.Getter;

@Getter
public enum IdentityMessages {

    IDENTITY_IMAGE_UPLOADED("Identity image uploaded"),
    IDENTITY_IMAGE_CHANGED("identity image changed"),
    IDENTITY_IMAGE_DELETED("Identity image deleted"),
    PART_FILE_EMPTY("Part file is empty"),
    IDENTITY_ENABLED("Identity enabled"),
    IDENTITY_DISABLED("Identity disabled"),
    ENABLE_DISABLE_MATCHES("Enable disable matches"),
    PASSWORD_CHANGED("Identity password changed"),
    PASSWORD_MATCHES("New password and old password are matches"),
    PASSWORD_NOT_FOUND("New password not found"),
    IDENTITY_NOT_AUTHORIZED("Identity not authorized"),
    IDENTITY_NOT_AUTHENTICATED("Identity not authenticated"),
    IDENTITY_DELETED("Identity deleted"),
    IDENTITY_NOT_FOUND("Identity not found"),
    IDENTITY_USERNAME_FIELD_EMPTY("Identity username field empty"),
    IDENTITY_PASSWORD_FIELD_EMPTY("Identity password field empty"),
    IDENTITY_EMAIL_FIELD_EMPTY("Identity email field empty"),
    IDENTITY_PHONE_FILED_EMPTY("Identity phone field empty");

    private final String message;

    IdentityMessages(String message) {
        this.message = message;
    }
}
