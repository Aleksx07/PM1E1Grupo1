package com.example.PM1E1Grupo1;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.PM1E1Grupo1.transacciones.Contactos;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityListView extends AppCompatActivity {

    private ListView contactosListView;
    private EditText buscar;
    Button agregar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        ArrayList<Contactos> contactos = obtenerContactosFromAPI();


        CustomBaseAdapter adapter = new CustomBaseAdapter(this, contactos);


        contactosListView = findViewById(R.id.customListView);
        contactosListView.setAdapter(adapter);

        agregar = findViewById(R.id.btnAtras);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
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
                            JSONArray jsonArray = response.getJSONArray("datos");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String nombre = jsonObject.getString("nombre");
                                String telefono = jsonObject.getString("telefono");
                                String latitud = jsonObject.getString("latitud");
                                String longitud = jsonObject.getString("longitud");
                                String imagen = jsonObject.getString("imagen");
                                Contactos contacto = new Contactos(id, nombre, telefono, latitud, longitud, imagen);
                                contactos.add(contacto);
                            }

                            CustomBaseAdapter adapter = new CustomBaseAdapter(ActivityListView.this, contactos);
                            contactosListView.setAdapter(adapter);
                            buscar = (EditText) findViewById(R.id.txtBuscar);



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
                            JSONArray jsonArray = response.getJSONArray("datos");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int id = jsonObject.getInt("id");
                                String nombre = jsonObject.getString("nombre");
                                String telefono = jsonObject.getString("telefono");
                                String latitud = jsonObject.getString("latitud");
                                String longitud = jsonObject.getString("longitud");
                                String imagen = jsonObject.getString("imagen");
                                Contactos contacto = new Contactos(id, nombre, telefono, latitud, longitud, imagen);
                                contactos.add(contacto);
                            }

                            CustomBaseAdapter adapter = new CustomBaseAdapter(ActivityListView.this, contactos);
                            contactosListView.setAdapter(adapter);

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

        queue.add(jsonObjectRequest);
        return contactos;
    }
}