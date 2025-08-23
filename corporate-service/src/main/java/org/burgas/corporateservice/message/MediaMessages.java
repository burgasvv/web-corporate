package org.burgas.corporateservice.message;

import lombok.Getter;

@Getter
public enum MediaMessages {

    MEDIA_NOT_FOUND("Media not found");

    private final String message;

    MediaMessages(String message) {
        this.message = message;
    }
}
