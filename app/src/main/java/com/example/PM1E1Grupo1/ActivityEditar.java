package com.example.PM1E1Grupo1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.PM1E1Grupo1.transacciones.Transacciones;


import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityEditar extends AppCompatActivity implements LocationListener {
    ImageView picture;
    Bitmap image;
    EditText txtLatitud, txtLongitud, nombre, telefono;
    Button tomarfoto, guardar, contactos;
    String currentPhotoPath;
    Boolean actualizacionActiva;
    ImageView foto;
    static final int REQUEST_IMAGE = 101;
    static final int PETICION_ACCESS_CAM = 201;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actualizacionActiva = false;
        picture = (ImageView) findViewById(R.id.imageView);
        tomarfoto = (Button) findViewById(R.id.btn_foto);
        contactos = (Button) findViewById(R.id.btn_contacto);
        txtLatitud = ( EditText ) findViewById(R.id.txtLatitud);
        txtLongitud = ( EditText ) findViewById(R.id.txtLongitud);
        nombre = ( EditText ) findViewById(R.id.txtNombre);
        telefono = ( EditText ) findViewById(R.id.txtTelefono);
        guardar = (Button) findViewById(R.id.btn_guardar);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String nombreContacto = intent.getStringExtra("nombre");
        String numero = intent.getStringExtra("numero");
        String latitud = intent.getStringExtra("latitud");
        String longitud = intent.getStringExtra("longitud");
        String foto = intent.getStringExtra("imagen");
        getSupportActionBar().setTitle("Actualizar Contacto: " + nombreContacto);

        nombre.setText(nombreContacto);
        telefono.setText(numero);
        txtLatitud.setText(latitud);
        txtLongitud.setText(longitud);
        currentPhotoPath = foto;
        Bitmap b = BitmapFactory.decodeFile(foto);
        picture.setImageBitmap(b);

        tomarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos();
            }
        });

        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent);
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreContacto = nombre.getText().toString();
                String numero = telefono.getText().toString();
                String lat = txtLatitud.getText().toString();
                String longi = txtLongitud.getText().toString();

                if(nombreContacto.isEmpty() || numero.isEmpty() || lat.isEmpty() || longi.isEmpty()){
                    Toast.makeText(ActivityEditar.this, "INGRESE TODOS LOS CAMPOS",Toast.LENGTH_SHORT).show();
                }
                else{
                    actualizarContacto(id, nombreContacto, numero, lat, longi);
                }
            }
        });

        getLocation();
    }

    @SuppressLint("MissingPermission")
    public void retrieveLocation() {
        LocationManager manager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
    }

    public void getLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            retrieveLocation();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PETICION_ACCESS_CAM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();

            } else {
                Toast.makeText(getApplicationContext(), "se necesita el permiso de la camara", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {

            try {
                File foto = new File(currentPhotoPath);
                image = BitmapFactory.decodeFile(foto.getAbsolutePath());
                picture.setImageURI(Uri.fromFile(foto));
            } catch (Exception ex) {
                ex.toString();
            }
        }
    }
    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PETICION_ACCESS_CAM);
        } else {
            dispatchTakePictureIntent();

        }

    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.PM1E1Grupo1.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            System.out.println("Latitude:" + location.getLatitude());
            System.out.println("Longitude:" + location.getLongitude());
            txtLatitud.setText(Double.toString(location.getLatitude()));
            txtLongitud.setText(Double.toString(location.getLongitude()));
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void actualizarContacto(int id, String nombre, String telefono, String latitud, String longitud) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = APIConexion.extraerEndpoint() + "UpdateContacto.php";
        JSONObject data = new JSONObject();
        try {
            data.put("id", id);
            data.put("nombre", nombre);
            data.put("telefono", telefono);
            data.put("latitud", latitud);
            data.put("longitud", longitud);
            data.put("imagen", currentPhotoPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(ActivityEditar.this, message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                        Toast.makeText(ActivityEditar.this, "Error al actualizar el contacto" + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        queue.add(request);
    }

    public void limpiar() {
        nombre.setText(Transacciones.Empty);
        telefono.setText(Transacciones.Empty);
        picture.setImageResource(R.drawable.hols);
    }
}
