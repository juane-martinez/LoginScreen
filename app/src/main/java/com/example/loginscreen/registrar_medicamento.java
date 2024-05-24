package com.example.loginscreen;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class registrar_medicamento extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button btnTakePhoto;
    private Button btnSubirFoto;
    private ImageView imageView;
    private Uri imageUri; // Uri de la imagen capturada




    String idPaciente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_medicamentos_aux);

        imageView = findViewById(R.id.imagen);
        btnTakePhoto = findViewById(R.id.ReconocerTXT);
        btnSubirFoto = findViewById(R.id.subir_foto);

        idPaciente = getIntent().getStringExtra("id_paciente");
        Log.d("DEBUG", "ID del paciente recibida: " + idPaciente);

        // Establece el listener para tomar la foto
        btnTakePhoto.setOnClickListener(view -> dispatchTakePictureIntent());
        // Establece el listener para subir la foto
        btnSubirFoto.setOnClickListener(view -> {
            if (imageUri != null) {
                uploadImage(imageUri);
                //subirInformacionAplicacion(medicamento, dosis, "Sin observaciones");
            } else {
                Toast.makeText(this, "Primero toma una foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            // Establece imageUri después de capturar la imagen
            imageUri = getImageUri(getApplicationContext(), imageBitmap);
            // Ahora que imageUri está establecido, puedes subir la imagen si es necesario
            /*
            if (imageUri != null) {
                uploadImage(imageUri);
            } else {
                Toast.makeText(this, "Error al obtener la ruta de la imagen", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        // Obtener ubicación
        obtenerUbicacion();

        // Obtener fecha y hora
        String fechaHora = obtenerFechaHora();

        // Obtener la ruta de la imagen
        //String imagePath = imageUri.getPath();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = null;
        try {
            imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            FileOutputStream out = new FileOutputStream(imageFile);
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(imageFile);
    }


    private void uploadImage(Uri imageUri) {
        File file = new File(imageUri.getPath());
        String fechaHora = obtenerFechaHora();

        Location location = obtenerUbicacion();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            // Continúa con el resto de tu código aquí, utilizando latitude y longitude
            try {
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("image/jpeg"), file))
                        .addFormDataPart("fechaHora", fechaHora)
                        .addFormDataPart("latitude", String.valueOf(latitude))
                        .addFormDataPart("longitude", String.valueOf(longitude))
                        .addFormDataPart("pacienteId", idPaciente)
                        .build();

                Request request = new Request.Builder()
                        .url("https://lab4pharmabot.000webhostapp.com/upload.php")
                        .post(requestBody)
                        .build();

                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseBody = response.body().string();
                            // Verificar si el Intent tiene el extra "medicamento"
                            if (getIntent().hasExtra("medicamento")) {
                                // Obtener el valor del extra "medicamento"
                                String medicamento = getIntent().getStringExtra("medicamento");
                                if (medicamento != null) {
                                    // El extra "medicamento" no es null, puedes usarlo

                                    // Verificar si el Intent tiene el extra "dosis"
                                    if (getIntent().hasExtra("dosis")) {
                                        // Obtener el valor del extra "dosis"
                                        String dosis = getIntent().getStringExtra("dosis");
                                        if (dosis != null) {
                                            subirInformacionAplicacion(medicamento, dosis);

                                        } else {
                                            // El extra "dosis" es null
                                            // Mostrar un mensaje de error
                                            Toast.makeText(registrar_medicamento.this, "No se pudo obtener la dosis", Toast.LENGTH_SHORT).show();
                                            // Registrar el tipo de error en Logcat
                                            Log.e("ERROR", "No se pudo obtener la dosis. El valor del extra 'dosis' es null.");
                                        }
                                    } else {
                                        // El Intent no tiene el extra "dosis"
                                        // Mostrar un mensaje de error
                                        Toast.makeText(registrar_medicamento.this, "No se pudo obtener la dosis", Toast.LENGTH_SHORT).show();
                                        // Registrar el tipo de error en Logcat
                                        Log.e("ERROR", "No se pudo obtener la dosis. El Intent no tiene el extra 'dosis'.");
                                    }
                                } else {
                                    // El extra "medicamento" es null
                                    // Mostrar un mensaje de error
                                    Toast.makeText(registrar_medicamento.this, "No se pudo obtener el medicamento", Toast.LENGTH_SHORT).show();
                                    // Registrar el tipo de error en Logcat
                                    Log.e("ERROR", "No se pudo obtener el medicamento. El valor del extra 'medicamento' es null.");
                                }
                            } else {
                                // El Intent no tiene el extra "medicamento"
                                // Mostrar un mensaje de error
                                Toast.makeText(registrar_medicamento.this, "No se pudo obtener el medicamento", Toast.LENGTH_SHORT).show();
                                // Registrar el tipo de error en Logcat
                                Log.e("ERROR", "No se pudo obtener el medicamento. El Intent no tiene el extra 'medicamento'.");
                            }

                            //String dosis = getIntent().getStringExtra("dosis");

                            registrar_medicamento.this.runOnUiThread(() -> Toast.makeText(registrar_medicamento.this, "Carga exitosa: " + responseBody, Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No se pudo obtener la ubicación. Asegúrate de que los permisos de ubicación estén concedidos y de que el GPS esté activado.", Toast.LENGTH_SHORT).show();
        }

    }

    private Location obtenerUbicacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    private String obtenerFechaHora() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private void subirInformacionAplicacion(String medicamento, String dosis) {
        // Obtener fecha y hora actual
        String fechaHora = obtenerFechaHora();

        // Obtener ubicación actual
        Location location = obtenerUbicacion();
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            // Realizar la llamada a la API para subir la información
            try {
                // Crear el cuerpo de la solicitud HTTP sin las observaciones
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("fechaHora", fechaHora)
                        .addFormDataPart("latitude", String.valueOf(latitude))
                        .addFormDataPart("longitude", String.valueOf(longitude))
                        .addFormDataPart("pacienteId", idPaciente)
                        .addFormDataPart("medicamento", medicamento)
                        .addFormDataPart("dosis", dosis)
                        .build();

                // Construir la solicitud HTTP
                Request request = new Request.Builder()
                        .url("https://lab4pharmabot.000webhostapp.com/subir_aplicacion.php")
                        .post(requestBody)
                        .build();

                // Crear el cliente HTTP y realizar la llamada
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String responseBody = response.body().string();
                            registrar_medicamento.this.runOnUiThread(() -> Toast.makeText(registrar_medicamento.this, "Información de aplicación subida exitosamente: " + responseBody, Toast.LENGTH_SHORT).show());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No se pudo obtener la ubicación. Asegúrate de que los permisos de ubicación estén concedidos y de que el GPS esté activado.", Toast.LENGTH_SHORT).show();
        }
    }




}





