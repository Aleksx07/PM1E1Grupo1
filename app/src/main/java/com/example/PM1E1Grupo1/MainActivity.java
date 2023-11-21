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

public class MainActivity extends AppCompatActivity implements LocationListener {
    ImageView picture;
    Bitmap image;
    EditText txtLatitud, txtLongitud, nombre, telefono;
    Button tomarfoto, guardar, contactos;
    String currentPhotoPath = "";
    Boolean actualizacionActiva;
    ImageView foto;
    static final int REQUEST_IMAGE = 101;
    static final int PETICION_ACCESS_CAM = 201;
    int ingresaFoto = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establece el diseño de la interfaz de usuario utilizando el archivo XML correspondiente
        setContentView(R.layout.activity_main);

        // Inicializa las variables y elementos de la interfaz de usuario
        actualizacionActiva = false;
        picture = (ImageView) findViewById(R.id.imageView);
        tomarfoto = (Button) findViewById(R.id.btn_foto);
        contactos = (Button) findViewById(R.id.btn_contacto);
        txtLatitud = ( EditText ) findViewById(R.id.txtLatitud);
        txtLongitud = ( EditText ) findViewById(R.id.txtLongitud);
        nombre = ( EditText ) findViewById(R.id.txtNombre);
        telefono = ( EditText ) findViewById(R.id.txtTelefono);
        guardar = (Button) findViewById(R.id.btn_guardar);

        // Configura el evento de clic para el botón "tomarfoto"
        tomarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisos(); // Llama al método para solicitar permisos
            }
        });

        // Configura el evento de clic para el botón "contactos"
        contactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un intent para abrir la actividad ActivityListView
                Intent intent = new Intent(getApplicationContext(), ActivityListView.class);
                startActivity(intent); // Inicia la nueva actividad
            }
        });

        // Configura el evento de clic para el botón "guardar"
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreContacto = nombre.getText().toString();  // Obtiene los valores de los campos de texto
                String numero = telefono.getText().toString();
                String lat = txtLatitud.getText().toString();
                String longi = txtLongitud.getText().toString();

                if(validarGPS()==true){
                    Toast.makeText(MainActivity.this, "GPS no esta activado",Toast.LENGTH_SHORT).show();
                }else{
                    // Verifica si algún campo está vacío
                    if(nombreContacto.isEmpty() || numero.isEmpty() || lat.isEmpty() || longi.isEmpty()){
                        // Muestra un mensaje de advertencia si algún campo está vacío
                        Toast.makeText(MainActivity.this, "INGRESE TODOS LOS CAMPOS",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // Llama al método para crear un nuevo contacto con los datos proporcionados
                        crearContacto(nombreContacto, numero, lat, longi, currentPhotoPath);
                    }
                }

            }
        });
        // Llama al método para obtener la ubicación del dispositivo
        getLocation();

        //Validar el ingreso de solo letras en el nombre
        expresiones_regulares();
}
    // Método para obtener la ubicación del dispositivo
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

    // Método para verificar y solicitar permisos de ubicación
    public void getLocation(){
        // Verifica si se tienen los permisos necesarios para acceder a la ubicación fina
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            retrieveLocation(); // Si se tienen los permisos, llama al método para obtener la ubicación
        }else{
            // Si no se tienen los permisos, solicita al usuario que los conceda
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    // Método llamado cuando se han procesado las solicitudes de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Verifica si la solicitud de permisos corresponde al acceso a la cámara
        if (requestCode == PETICION_ACCESS_CAM) {
            // Verifica si se otorgaron los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent(); // Si se otorgan los permisos, se inicia la captura de la imagen

            } else {
                // Si no se otorgan los permisos, muestra un mensaje al usuario
                Toast.makeText(getApplicationContext(), "se necesita el permiso de la camara", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Método llamado cuando se ha capturado una imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verifica si la solicitud corresponde a la captura de una imagen
        if (requestCode == REQUEST_IMAGE) {

            try {
                // Obtiene la ruta de la imagen capturada y la muestra en el ImageView
                File foto = new File(currentPhotoPath);
                image = BitmapFactory.decodeFile(foto.getAbsolutePath());
                picture.setImageURI(Uri.fromFile(foto));
            } catch (Exception ex) {
                ex.toString(); // Manejo de errores en caso de que haya problemas al mostrar la imagen
            }
        }
    }

    // Método para solicitar permisos de cámara
    private void permisos() {
        // Verifica si se tienen los permisos necesarios para acceder a la cámara
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen los permisos, los solicita al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PETICION_ACCESS_CAM);
        } else {
            dispatchTakePictureIntent(); // Si se tienen los permisos, inicia la captura de la imagen
        }

    }

    // Método para iniciar la captura de la imagen
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Verifica si hay una actividad que pueda manejar la captura de imágenes
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile(); // Crea un archivo de imagen temporal
            } catch (IOException ex) {
                ex.toString(); // Manejo de errores en caso de problemas al crear el archivo de imagen
            }
            // Continúa solo si el archivo de imagen se creó correctamente
            if (photoFile != null) {
                // Obtiene la URI del archivo de imagen utilizando un proveedor de archivos
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.PM1E1Grupo1.fileprovider",
                        photoFile);

                // Configura la ubicación de salida de la imagen capturada y lanza la actividad de captura
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
            }
        }
    }

    // Método para crear un archivo de imagen con nombre único
    private File createImageFile() throws IOException {
        // Crea un nombre de archivo único basado en la fecha y hora actual
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Obtiene el directorio de almacenamiento de imágenes externo de la aplicación
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Crea un archivo temporal de imagen con el nombre único y extensión .jpg
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Guarda la ruta del archivo de imagen para su uso posterior
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // Inicializa el geocoder con la configuración regional predeterminada del dispositivo
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            // Imprime la latitud y longitud en la consola de desarrollo
            System.out.println("Latitude:" + location.getLatitude());
            System.out.println("Longitude:" + location.getLongitude());

            // Actualiza los campos de texto con la latitud y longitud
            txtLatitud.setText(Double.toString(location.getLatitude()));
            txtLongitud.setText(Double.toString(location.getLongitude()));

            // Obtiene la dirección a partir de la latitud y longitud utilizando geocoder
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            // Captura y lanza una RuntimeException si hay un error en la obtención de la dirección
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

    public void crearContacto(String nombre, String telefono, String latitud, String longitud, String path) {
        if(currentPhotoPath.equals("")){
            Toast.makeText(this, "INGRESE SU FOTOGRAFÍA",Toast.LENGTH_SHORT).show();
        }else{
            // Crea una cola de solicitudes Volley
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            // Construye la URL completa para la creación del contacto
            String url = APIConexion.extraerEndpoint() + "CreateContacto.php";
            // Crea un objeto JSON con los datos del nuevo contacto
            JSONObject data = new JSONObject();
            try {
                data.put("nombre", nombre);
                data.put("telefono", telefono);
                data.put("latitud", latitud);
                data.put("longitud", longitud);
                data.put("imagen", currentPhotoPath);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Crea una solicitud JsonObjectRequest para enviar los datos al servidor
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Obtiene el mensaje de respuesta del servidor
                                String message = response.getString("message");
                                // Muestra un mensaje Toast con la respuesta.
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                                limpiar(); // Limpia los campos después de crear el contacto
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.getMessage()); // Imprime el mensaje de error en la consola de desarrollo
                            // Muestra un mensaje Toast informando sobre el error al crear el contacto
                            Toast.makeText(MainActivity.this, "Error al crear el contacto" + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
            queue.add(request); // Agrega la solicitud a la cola para su procesamiento
        }
    }

    //Limpia los campos de nombre y teléfono, y restablece la imagen predeterminada.
    public void limpiar() {
        // Establece el texto de nombre y teléfono a vacío
        nombre.setText(Transacciones.Empty);
        telefono.setText(Transacciones.Empty);

        // Restablece la imagen a la predeterminada (hols en este caso)
        picture.setImageResource(R.drawable.hols);
    }

    public boolean validarGPS(){
        boolean validar=false;
        if(txtLatitud.getText().toString().isEmpty() && txtLongitud.getText().toString().isEmpty()){
            validar = true;
        }
        return validar;
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
