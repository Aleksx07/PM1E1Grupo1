package com.example.PM1E1Grupo1;

public class APIConexion {

    //Clase que proporciona la conexión a la API para realizar operaciones CRUD en la base de datos de contactos
    // URL base de la API
    private static final String BASE_ENDPOINT = "http://192.168.1.36/contactos/";

    public static String extraerEndpoint() {

        return BASE_ENDPOINT;
    }
}