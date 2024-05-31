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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

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
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private Button btnTakePhoto;
    private Button btnSubirFoto;
    private ImageView imageView;
    private Uri imageUri; // Uri de la imagen capturada
    private String idPaciente;

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
            } else {
                Toast.makeText(this, "Primero toma una foto", Toast.LENGTH_SHORT).show();
            }
        });

        // Solicitar permisos de ubicación si no están concedidos
        checkLocationPermissions();
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
            imageUri = getImageUri(getApplicationContext(), imageBitmap);
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        String fechaHora = obtenerFechaHora();

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
                            registrar_medicamento.this.runOnUiThread(() -> Toast.makeText(registrar_medicamento.this, "Carga exitosa: " + responseBody, Toast.LENGTH_SHORT).show());

                            // Después de la carga exitosa, subir la información de la aplicación
                            if (getIntent().hasExtra("medicamento")) {
                                String medicamento = getIntent().getStringExtra("medicamento");
                                if (medicamento != null) {
                                    if (getIntent().hasExtra("dosis")) {
                                        String dosis = getIntent().getStringExtra("dosis");
                                        if (dosis != null) {
                                            subirInformacionAplicacion(medicamento, dosis);
                                        } else {
                                            registrar_medicamento.this.runOnUiThread(() -> Toast.makeText(registrar_medicamento.this, "No se pudo obtener la dosis", Toast.LENGTH_SHORT).show());
                                            Log.e("ERROR", "No se pudo obtener la dosis. El valor del extra 'dosis' es null.");
                                        }
                                    } else {
                                        registrar_medicamento.this.runOnUiThread(() -> Toast.makeText(registrar_medicamento.this, "No se pudo obtener la dosis", Toast.LENGTH_SHORT).show());
                                        Log.e("ERROR", "No se pudo obtener la dosis. El Intent no tiene el extra 'dosis'.");
                                    }
                                } else {
                                    registrar_medicamento.this.runOnUiThread(() -> Toast.makeText(registrar_medicamento.this, "No se pudo obtener el medicamento", Toast.LENGTH_SHORT).show());
                                    Log.e("ERROR", "No se pudo obtener el medicamento. El valor del extra 'medicamento' es null.");
                                }
                            } else {
                                registrar_medicamento.this.runOnUiThread(() -> Toast.makeText(registrar_medicamento.this, "No se pudo obtener el medicamento", Toast.LENGTH_SHORT).show());
                                Log.e("ERROR", "No se pudo obtener el medicamento. El Intent no tiene el extra 'medicamento'.");
                            }
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

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_LOCATION_PERMISSION);
        }
    }

    private Location obtenerUbicacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        String fechaHora = obtenerFechaHora();
        Location location = obtenerUbicacion();

        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            try {
                RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("fechaHora", fechaHora)
                        .addFormDataPart("latitude", String.valueOf(latitude))
                        .addFormDataPart("longitude", String.valueOf(longitude))
                        .addFormDataPart("pacienteId", idPaciente)
                        .addFormDataPart("medicamento", medicamento)
                        .addFormDataPart("dosis", dosis)
                        .build();

                Request request = new Request.Builder()
                        .url("https://lab4pharmabot.000webhostapp.com/subir_aplicacion.php")
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}





