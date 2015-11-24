package ru.ulfr.poc;

/**
 * Site configuration
 * <p/>
 * Created by ulfr on 22.10.15.
 */
public class Config {
    public static final String DS_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DS_URL = "jdbc:mysql://127.0.0.1:3306/pocibs?useUnicode=true&characterEncoding=UTF-8";
    public static final String DS_USER = "pocibs";
    public static final String DS_PASS = "pocibs";

    public static final String PASSWORD_ENCODER_SECRET = "secret";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    private static Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }

}
