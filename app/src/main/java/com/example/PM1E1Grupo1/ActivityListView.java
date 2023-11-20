package com.example.PM1E1Grupo1;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.PM1E1Grupo1.transacciones.Contactos;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ActivityListView extends AppCompatActivity {

    private ListView contactosListView;
    private EditText buscar;
    CustomBaseAdapter myCustom;
    CustomBaseAdapter adapter;
    Button agregar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        // Obtener la lista de contactos desde la API
        ArrayList<Contactos> contactos = obtenerContactosFromAPI();
        myCustom = new CustomBaseAdapter(getApplicationContext(),contactos);
        // Crear un adaptador personalizado para la lista de contactos
        adapter = new CustomBaseAdapter(this, contactos);

        // Asignar el adaptador a la vista de lista
        contactosListView = findViewById(R.id.customListView);
        contactosListView.setAdapter(myCustom);

        // Configurar el botón de regreso a la actividad principal
        agregar = findViewById(R.id.btnAtras);
        buscar = (EditText) findViewById(R.id.txtBuscar);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crear un intent para regresar a la actividad principal
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //Llamar la función que realiza la búsqueda aquí
                buscarEnBaseDeDatos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void buscarEnBaseDeDatos(String texto) {
        ArrayList<Contactos> listaContactos = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = APIConexion.extraerEndpoint() + "SearchContacto.php";

        // Crea un objeto JSONObject para enviar en la solicitud
        final JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("searchTerm", texto);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Crear una solicitud JsonArrayRequest para enviar los datos al servidor y recibir un JSONArray como respuesta
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null, // null para el constructor, pero vamos a sobrescribir getBody
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Respuesta Server",""+response);
                        try {
                            // Procesar el JSONArray
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject contacto = response.getJSONObject(i);

                                Integer id = contacto.getInt("id");
                                String nombre = contacto.getString("nombre");
                                String telefono = contacto.getString("numero");
                                String latitud = contacto.getString("latitud");
                                String longitud = contacto.getString("longitud");
                                String imagen = contacto.getString("imagen"); // URL o referencia de la imagen

                                Contactos Clasecontacto = new Contactos(id, nombre, telefono, latitud, longitud, imagen);
                                listaContactos.add(Clasecontacto);
                            }
                            myCustom = new CustomBaseAdapter(ActivityListView.this, listaContactos);
                            contactosListView.setAdapter(myCustom);
                           // buscar = (EditText) findViewById(R.id.txtBuscar); // Limpiar el campo de búsqueda
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                }) {

            @Override
            public byte[] getBody() {
                // Sobrescribir para enviar el jsonBody como parte de la solicitud POST
                return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public String getBodyContentType() {
                // Sobrescribir para definir el tipo de contenido de la solicitud
                return "application/json; charset=utf-8";
            }
        };

        // Agregar la solicitud al RequestQueue para ejecutarla
        queue.add(jsonArrayRequest);
    }



    //Metodo buscar Contacto desde la API
    private ArrayList<Contactos> obtenerOneContactosFromAPI() {
       String  nombre = buscar.getText().toString();
        String url = APIConexion.extraerEndpoint() + "ReadOneContact.php?nombre=" + nombre;
        RequestQueue queue = Volley.newRequestQueue(this);

        final ArrayList<Contactos> contactos = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el arreglo de datos del objeto JSON de respuesta
                            JSONArray jsonArray = response.getJSONArray("datos");

                            // Iterar sobre cada objeto JSON en el arreglo
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // Extraer los atributos del objeto JSON
                                int id = jsonObject.getInt("id");
                                String nombre = jsonObject.getString("nombre");
                                String telefono = jsonObject.getString("telefono");
                                String latitud = jsonObject.getString("latitud");
                                String longitud = jsonObject.getString("longitud");
                                String imagen = jsonObject.getString("imagen");

                                // Crear un objeto Contactos con los atributos extraídos
                                Contactos contacto = new Contactos(id, nombre, telefono, latitud, longitud, imagen);
                                // Agregar el contacto a la lista
                                contactos.add(contacto);
                            }

                            // Crear un adaptador personalizado para la lista de contactos
                            CustomBaseAdapter adapter = new CustomBaseAdapter(ActivityListView.this, contactos);
                            // Asignar el adaptador a la vista de lista
                            contactosListView.setAdapter(adapter);
                            buscar = (EditText) findViewById(R.id.txtBuscar); // Limpiar el campo de búsqueda



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        // Agregar la solicitud al objeto de la cola de solicitudes
        queue.add(jsonObjectRequest);
        return contactos;
    }


    // Método para obtener la lista de contactos desde la API
    private ArrayList<Contactos> obtenerContactosFromAPI() {
        String url = APIConexion.extraerEndpoint() + "ReadContactos.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        final ArrayList<Contactos> contactos = new ArrayList<>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el arreglo de datos del objeto JSON de respuesta
                            JSONArray jsonArray = response.getJSONArray("datos");

                            // Iterar sobre cada objeto JSON en el arreglo
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                // Extraer los atributos del objeto JSON
                                int id = jsonObject.getInt("id");
                                String nombre = jsonObject.getString("nombre");
                                String telefono = jsonObject.getString("telefono");
                                String latitud = jsonObject.getString("latitud");
                                String longitud = jsonObject.getString("longitud");
                                String imagen = jsonObject.getString("imagen");

                                // Crear un objeto Contactos con los atributos extraídos
                                Contactos contacto = new Contactos(id, nombre, telefono, latitud, longitud, imagen);
                                contactos.add(contacto); // Agregar el contacto a la lista
                            }

                            // Crear un adaptador personalizado para la lista de contactos
                            CustomBaseAdapter adapter = new CustomBaseAdapter(ActivityListView.this, contactos);
                            contactosListView.setAdapter(adapter); // Asignar el adaptador a la vista de lista

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        // Agregar la solicitud al objeto de la cola de solicitudes
        queue.add(jsonObjectRequest);
        return contactos;
    }
}