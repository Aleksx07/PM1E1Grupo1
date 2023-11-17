package com.example.PM1E1Grupo1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class ActivityView extends AppCompatActivity {

    ImageView picture;
    Button verFoto, editar, eliminar, contactos, mapa;
    EditText nombre, telefono, lat, lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        picture = (ImageView) findViewById(R.id.imageView);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String nombreContacto = intent.getStringExtra("nombre");
        String numero = intent.getStringExtra("numero");
        String latitud = intent.getStringExtra("latitud");
        String longitud = intent.getStringExtra("longitud");
        String imagen = intent.getStringExtra("imagen");
        getSupportActionBar().setTitle("Contacto: " + nombreContacto);

        Bitmap b = BitmapFactory.decodeFile(imagen);
        picture.setImageBitmap(b);

        verFoto = (Button) findViewById(R.id.btn_foto);
        contactos = (Button) findViewById(R.id.btn_contacto);
        editar = (Button) findViewById(R.id.btn_editar);
        eliminar = (Button) findViewById(R.id.btn_eliminar);
        mapa = (Button) findViewById(R.id.btn_mapa);
        picture = (ImageView) findViewById(R.id.imageView);
        nombre = (EditText) findViewById(R.id.txtNombre);
        telefono = (EditText) findViewById(R.id.txtTelefono);
        lat = (EditText) findViewById(R.id.txtLatitud);
        lon = (EditText) findViewById(R.id.txtLongitud);

        nombre.setText(nombreContacto);
        telefono.setText(numero);
        lat.setText(latitud);
        lon.setText(longitud);

        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent);
            }
        });


        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityEditar.class);
                intent.putExtra("id", id);
                intent.putExtra("nombre", nombreContacto);
                intent.putExtra("numero", numero);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("imagen", imagen);
                startActivity(intent);
            }
        });

        verFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FotoActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("nombre", nombreContacto);
                intent.putExtra("numero", numero);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("imagen", imagen);
                startActivity(intent);
            }
        });


        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Ubicacion.class);
                intent.putExtra("nombre", nombreContacto);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                startActivity(intent);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarContacto(id);
            }
        });
    }

    public void eliminarContacto(int idContacto) {
        String url = APIConexion.extraerEndpoint() + "DeleteContacto.php?id=" + idContacto;

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}