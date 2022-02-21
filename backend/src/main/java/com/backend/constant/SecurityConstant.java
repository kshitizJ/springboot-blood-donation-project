package com.backend.constant;

/**
 * SecurityConstant
 */
public class SecurityConstant {
    public static final Long EXPIRATION_TIME = 600_000L; // 5 Days expressed in milliseconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "access_token";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified!";
    public static final String BLOOD_DONATION_LLC = "Blood Donation, LLC";
    public static final String BLOOD_DONATION_ADMINISTRATION = "Blood Donation Management Portal";
    public static final String AUTHORITIES = "roles";
    public static final String FORBIDDEN_MESSAGE = "You need to login to access this page.";
    public static final String ACCESS_DENIED = "You do not have permission to access this page.";
    public static final String OPTIONS_HTTP_METHOD = "OPTION";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
    public static final String[] PUBLIC_URLS = { "/login", "/admin/login", "/admin/resetPassword" };
}