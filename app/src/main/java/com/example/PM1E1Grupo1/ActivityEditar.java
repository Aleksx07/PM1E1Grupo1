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
import android.text.InputFilter;
import android.text.Spanned;
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
import java.util.regex.Pattern;

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

        // Indica si la actualización está activa, inicialmente establecida como falsa.
        actualizacionActiva = false;

        // Referencia al componente ImageView en la interfaz de usuario.
        picture = (ImageView) findViewById(R.id.imageView);

        // Referencias a los botones y campos de texto en la interfaz de usuario.
        tomarfoto = (Button) findViewById(R.id.btn_foto);
        contactos = (Button) findViewById(R.id.btn_contacto);
        txtLatitud = ( EditText ) findViewById(R.id.txtLatitud);
        txtLongitud = ( EditText ) findViewById(R.id.txtLongitud);
        nombre = ( EditText ) findViewById(R.id.txtNombre);
        telefono = ( EditText ) findViewById(R.id.txtTelefono);
        guardar = (Button) findViewById(R.id.btn_guardar);

        // Recupera los datos del intent que inició esta actividad (si existen).
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String nombreContacto = intent.getStringExtra("nombre");
        String numero = intent.getStringExtra("numero");
        String latitud = intent.getStringExtra("latitud");
        String longitud = intent.getStringExtra("longitud");
        String foto = intent.getStringExtra("imagen");

        // Establece el título de la barra de acción para indicar que se está actualizando un contacto.
        getSupportActionBar().setTitle("Actualizar Contacto: " + nombreContacto);

        // Llena los campos de texto con los datos existentes del contacto a actualizar.
        nombre.setText(nombreContacto);
        telefono.setText(numero);
        txtLatitud.setText(latitud);
        txtLongitud.setText(longitud);
        currentPhotoPath = foto;

        // Carga la imagen del contacto existente en el ImageView.
        Bitmap b = BitmapFactory.decodeFile(foto);
        picture.setImageBitmap(b);

        // Establece un OnClickListener para el botón de tomar foto.
        tomarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Solicita permisos antes de iniciar la captura de foto.
                permisos();
            }
        });

        // Establece un OnClickListener para el botón de ver contactos.
        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inicia la actividad para ver la lista de contactos.
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent);
            }
        });

        // Establece un OnClickListener para el botón de guardar.
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtiene los datos ingresados por el usuario.
                String nombreContacto = nombre.getText().toString();
                String numero = telefono.getText().toString();
                String lat = txtLatitud.getText().toString();
                String longi = txtLongitud.getText().toString();

                // Verifica que todos los campos estén completos.
                if(nombreContacto.isEmpty() || numero.isEmpty() || lat.isEmpty() || longi.isEmpty()){
                    // Muestra un mensaje de error si algún campo está vacío.
                    Toast.makeText(ActivityEditar.this, "INGRESE TODOS LOS CAMPOS",Toast.LENGTH_SHORT).show();
                }
                else{
                    // Llama al método para actualizar el contacto con los nuevos datos.
                    actualizarContacto(id, nombreContacto, numero, lat, longi);
                }
            }
        });
        // Inicia la obtención de la ubicación del dispositivo.
        getLocation();

        //Validar el ingreso de solo letras en el nombre
        expresiones_regulares();
    }

    // Método para solicitar la ubicación del dispositivo.
    @SuppressLint("MissingPermission")
    public void retrieveLocation() {
        // Obtiene el servicio de ubicación del sistema
        LocationManager manager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        // Verifica si el GPS está habilitado
        boolean isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGpsEnabled) {
            Toast.makeText(getApplicationContext(), "GPS NO ESTA ACTIVADO", Toast.LENGTH_LONG).show();
        } else {
            // GPS está activado, procede con la solicitud de actualizaciones de ubicación
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10, this);
        }
    }

    // Método para obtener la ubicación del dispositivo.
    public void getLocation(){
        // Verifica si se tiene permiso para acceder a la ubicación.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // Si tiene permiso, solicita la ubicación.
            retrieveLocation();
        }else{
            // Si no tiene permiso, solicita permisos al usuario.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    // Método invocado cuando se otorgan o deniegan permisos.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Verifica si los permisos de la cámara fueron otorgados.
        if (requestCode == PETICION_ACCESS_CAM) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si los permisos son otorgados, inicia la captura de foto.
                dispatchTakePictureIntent();

            } else {
                // Muestra un mensaje indicando que se necesita el permiso de la cámara.
                Toast.makeText(getApplicationContext(), "se necesita el permiso de la camara", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Método invocado después de que se captura una foto.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Verifica si la solicitud de captura de imagen fue exitosa.
        if (requestCode == REQUEST_IMAGE) {

            try {
                // Lee la foto capturada y la muestra en el ImageView.
                File foto = new File(currentPhotoPath);
                image = BitmapFactory.decodeFile(foto.getAbsolutePath());
                picture.setImageURI(Uri.fromFile(foto));
            } catch (Exception ex) {
                ex.toString();
            }
        }
    }

    // Método para solicitar permisos antes de iniciar la captura de foto.
    private void permisos() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PETICION_ACCESS_CAM);
        } else {
            // Si ya tiene permisos, inicia la captura de foto.
            dispatchTakePictureIntent();

        }

    }

    // Método para iniciar la captura de foto.
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Crea un archivo temporal para almacenar la foto.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continúa solo si el archivo se creó exitosamente.
            if (photoFile != null) {
                // Obtiene la URI del archivo temporal y envía la solicitud de captura de foto.
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.PM1E1Grupo1.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    // Método para crear un archivo de imagen temporal.
    private File createImageFile() throws IOException {
        // Crea un nombre único para el archivo de imagen.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // Define el directorio de almacenamiento de imágenes.
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Crea el archivo temporal.
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Guarda la ruta del archivo para su uso posterior.
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Método invocado cuando cambia la ubicación del dispositivo.
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Convierte la latitud y longitud en texto y los muestra en los campos correspondientes.
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

    // Métodos de la interfaz LocationListener que gestionan cambios en la ubicación y estado del proveedor de ubicación.
    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        // Gestiona cambios en la ubicación cuando se recibe una lista de ubicaciones.
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        // Gestiona la finalización del flush de ubicación.
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Gestiona cambios en el estado del proveedor de ubicación.
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        // Gestiona la activación del proveedor de ubicación.
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        // Gestiona la desactivación del proveedor de ubicación.
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        // Gestiona cambios en la captura del puntero.
        super.onPointerCaptureChanged(hasCapture);
    }

    // Método para actualizar un contacto con nuevos datos.
    public void actualizarContacto(int id, String nombre, String telefono, String latitud, String longitud) {
        // Configura la cola de solicitudes Volley.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        // Construye la URL del servicio web para actualizar el contacto.
        String url = APIConexion.extraerEndpoint() + "UpdateContacto.php";
        // Crea un objeto JSON con los datos del contacto a actualizar.
        JSONObject data = new JSONObject();
        // Intenta poner los datos del contacto a actualizar en el objeto JSON.
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
        // Crea una solicitud JSON para enviar los datos al servidor.
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Maneja la respuesta después de la actualización.
                        try {
                            String message = response.getString("message");
                            // Muestra un mensaje al usuario con la respuesta.
                            Toast.makeText(ActivityEditar.this, message, Toast.LENGTH_LONG).show();
                            // Redirige a la actividad de lista de contactos.
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
                        // Maneja errores de respuesta.
                        System.out.println(error.getMessage());
                        // Muestra un mensaje al usuario indicando el error.
                        Toast.makeText(ActivityEditar.this, "Error al actualizar el contacto" + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        // Agrega la solicitud a la cola de solicitudes Volley.
        queue.add(request);
    }

    // Método para limpiar los campos de entrada y restablecer la imagen predeterminada.
    public void limpiar() {
        nombre.setText(Transacciones.Empty);
        telefono.setText(Transacciones.Empty);
        picture.setImageResource(R.drawable.hols);
    }

    /*Metodo para poder validar expresiones regulares*/
    public void expresiones_regulares(){
        InputFilter soloLetras = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                StringBuilder builder = new StringBuilder();
                for (int i = start; i < end; i++) {
                    if (Pattern.matches("[a-zA-Z\\s]", String.valueOf(source.charAt(i)))) {
                        builder.append(source.charAt(i));
                    }
                    // Los caracteres que no cumplen con la condición simplemente no se añaden al constructor
                }
                // Si todos los caracteres son válidos, devolver null no cambia la entrada
                return source.length() == builder.length() ? null : builder.toString();
            }
        };
        nombre.setFilters(new InputFilter[]{soloLetras});
    }
}
