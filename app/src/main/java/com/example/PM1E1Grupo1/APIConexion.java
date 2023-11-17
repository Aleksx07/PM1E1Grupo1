package com.example.PM1E1Grupo1;

public class APIConexion {
    private static final String BASE_ENDPOINT = "http://192.168.0.64/contactos/";

    public static String extraerEndpoint() {
        return BASE_ENDPOINT;
    }
}