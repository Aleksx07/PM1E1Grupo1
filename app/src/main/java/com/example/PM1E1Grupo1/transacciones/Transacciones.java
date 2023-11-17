package com.example.PM1E1Grupo1.transacciones;

public class Transacciones
{
    // Nombre de la base de datos
    public static final String NameDatabase = "app_registrocontacto";
    // Tablas de la base de datos
    public static final String tablacontactos = "contactos";

    /* Transacciones de la base de datos app_registrocontacto */
    public static final String CreateTBContactos =
            "CREATE TABLE contactos (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, pais TEXT, numero TEXT, nota TEXT, image TEXT)";

    public static final String DropTableContactos = "DROP TABLE IF EXISTS contactos";

    // Helpers
    public static final String Empty = "";
}