package com.roomrent.app.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ARRENDADOR = "ROLE_ARRENDADOR";

    public static final String ARRENDATARIO = "ROLE_ARRENDATARIO";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
