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

        // Obtener referencias a los elementos de la interfaz de usuario
        picture = (ImageView) findViewById(R.id.imageView);

        // Obtener datos del intent que inició esta actividad
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String nombreContacto = intent.getStringExtra("nombre");
        String numero = intent.getStringExtra("numero");
        String latitud = intent.getStringExtra("latitud");
        String longitud = intent.getStringExtra("longitud");
        String imagen = intent.getStringExtra("imagen");

        // Establecer el título de la barra de acción con el nombre del contacto
        getSupportActionBar().setTitle("Contacto: " + nombreContacto);

        // Decodificar la imagen de la ruta del archivo y mostrarla en el ImageView
        Bitmap b = BitmapFactory.decodeFile(imagen);
        picture.setImageBitmap(b);

        // Obtener referencias a los elementos de la interfaz de usuario
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

        // Establecer los textos en los EditText con los datos del contacto
        nombre.setText(nombreContacto);
        telefono.setText(numero);
        lat.setText(latitud);
        lon.setText(longitud);

        // Configurar un OnClickListener para el botón de "Ver Contactos"
        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent);
            }
        });

        // Configurar un OnClickListener para el botón de "Editar"
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear un intent para iniciar la actividad de edición
                Intent intent = new Intent(getApplicationContext(), ActivityEditar.class);

                // Pasar los datos del contacto actual a la actividad de edición
                intent.putExtra("id", id);
                intent.putExtra("nombre", nombreContacto);
                intent.putExtra("numero", numero);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                intent.putExtra("imagen", imagen);

                // Iniciar la actividad de edición
                startActivity(intent);
            }
        });

        // Configurar un OnClickListener para el botón de "Ver Foto"
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

        // Configurar un OnClickListener para el botón "Mapa"
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear un intent para iniciar la actividad de ubicación en el mapa
                Intent intent = new Intent(getApplicationContext(), Ubicacion.class);
                // Pasar los datos necesarios para la ubicación al intent
                intent.putExtra("nombre", nombreContacto);
                intent.putExtra("latitud", latitud);
                intent.putExtra("longitud", longitud);
                startActivity(intent);  // Iniciar la actividad de ubicación en el mapa
            }
        });

        // Configurar un OnClickListener para el botón "Eliminar"
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarContacto(id); // Llamar al método para eliminar el contacto
            }
        });
    }

    //Método para eliminar un contacto mediante una solicitud HTTP DELETE a la API.
    public void eliminarContacto(int idContacto) {
        // Construir la URL para la solicitud DELETE a la API
        String url = APIConexion.extraerEndpoint() + "DeleteContacto.php?id=" + idContacto;
        // Crear una solicitud StringRequest con método DELETE
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Al recibir una respuesta exitosa, iniciar la actividad de la lista de contactos
                        Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();  // Manejar errores en la respuesta
                    }
                });

        // Obtener la cola de solicitudes Volley y agregar la solicitud
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}