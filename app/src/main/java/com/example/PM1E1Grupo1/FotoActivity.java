package com.example.PM1E1Grupo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FotoActivity extends AppCompatActivity {

    ImageView picture;
    Button regresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establece el diseño de la interfaz de usuario utilizando el archivo XML correspondiente
        setContentView(R.layout.activity_foto);

        // Inicializa los elementos de la interfaz de usuario
        picture = (ImageView) findViewById(R.id.image_view);
        regresar = (Button) findViewById(R.id.regresar);

        // Obtiene los datos pasados a través del Intent
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String nombreContacto = intent.getStringExtra("nombre");
        String numero = intent.getStringExtra("numero");
        String latitud = intent.getStringExtra("latitud");
        String longitud = intent.getStringExtra("longitud");
        String foto = intent.getStringExtra("imagen");

        // Establece el título de la barra de acción
        getSupportActionBar().setTitle("Imagen De: " + nombreContacto);

        // Decodifica la imagen y la muestra en el ImageView
        Bitmap b = BitmapFactory.decodeFile(foto);
        picture.setImageBitmap(b);

        // Configura el evento de clic para el botón "regresar"
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un nuevo Intent para regresar a la ActivityView
                Intent intent = new Intent(getApplicationContext(), ActivityView.class);

                // Pasa los datos necesarios a través del Intent
                intent.putExtra("id", id);
                intent.putExtra("nombre", nombreContacto);
                intent.putExtra("numero", numero);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("imagen", foto);
                startActivity(intent); // Inicia la nueva actividad
            }
        });
    }
}