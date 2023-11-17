package com.example.PM1E1Grupo1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Ubicacion extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    GoogleMap mMap;
    String latitud, longitud;

    //Configura y muestra la ubicación en un mapa para un contacto específico.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        // Obtiene el SupportMapFragment del diseño y sincroniza el mapa de manera asíncrona
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        // Obtiene la información del contacto de la intención
        Intent intent = getIntent();
        String nombreContacto = intent.getStringExtra("nombre");
        latitud = intent.getStringExtra("latitud");
        longitud = intent.getStringExtra("longitud");

        // Establece el título de la barra de acción con el nombre del contacto
        getSupportActionBar().setTitle("Ubicacion de Contacto: " + nombreContacto);
    }

    //Método llamado cuando el mapa está listo para ser utilizado.
    //Se configura y muestra la ubicación del contacto en el mapa.
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Convierte las coordenadas de latitud y longitud a un objeto LatLng
        LatLng sydney = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
        // Añade un marcador en la ubicación del contacto y mueve la cámara a esa posición
        mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicación Actual"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    // Implementación opcional para manejar cambios en la captura del puntero
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}