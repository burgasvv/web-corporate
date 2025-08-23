package org.burgas.corporateservice.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

    ADMIN,
    USER,
    WORKER,
    DIRECTOR;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
