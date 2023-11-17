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
        setContentView(R.layout.activity_foto);

        picture = (ImageView) findViewById(R.id.image_view);
        regresar = (Button) findViewById(R.id.regresar);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String nombreContacto = intent.getStringExtra("nombre");
        String numero = intent.getStringExtra("numero");
        String latitud = intent.getStringExtra("latitud");
        String longitud = intent.getStringExtra("longitud");
        String foto = intent.getStringExtra("imagen");
        getSupportActionBar().setTitle("Imagen De: " + nombreContacto);

        Bitmap b = BitmapFactory.decodeFile(foto);
        picture.setImageBitmap(b);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityView.class);
                intent.putExtra("id", id);
                intent.putExtra("nombre", nombreContacto);
                intent.putExtra("numero", numero);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("imagen", foto);
                startActivity(intent);
            }
        });
    }
}