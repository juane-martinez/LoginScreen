package com.example.loginscreen;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Log;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Comprobation_Menu extends AppCompatActivity {

    Button capture_btn;
    Button scanner_btn;
    ImageView imagen;
    EditText TextoReconocidoEt;

    String idPaciente;
    Button comprobar_btn;
    private Uri uri= null;
    private ProgressDialog progressDialog;
    private TextRecognizer textRecognizer;
    private static final int REQUEST_LOCATION = 1;
    private FusedLocationProviderClient locationClient;
    private static final int REQUEST_BACKGROUND_LOCATION = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comprobacion_medicamentos_aux);
        capture_btn = findViewById(R.id.ReconocerTXT);
        scanner_btn = findViewById(R.id.EscanearTXT);
        comprobar_btn=findViewById(R.id.comprobar);
        imagen = findViewById(R.id.imagen);
        TextoReconocidoEt = findViewById(R.id.TextoReconocidoEt);
        progressDialog = new ProgressDialog(Comprobation_Menu.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        locationClient = LocationServices.getFusedLocationProviderClient(this);
        idPaciente = getIntent().getStringExtra("id_paciente");
        Log.d("DEBUG", "ID del paciente recibida: " + idPaciente);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Comprobation_Menu.this, "Abriendo cámara", Toast.LENGTH_SHORT).show();
                AbrirCamara();
            }
        });

        scanner_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri == null){
                    Toast.makeText(Comprobation_Menu.this, "Por favor seleccione una imagen",Toast.LENGTH_SHORT).show();
                }else {
                    reconocerTextoDeImagen();
                    checkPermissionsAndRequestLocation();
                }
            }
        });
        comprobar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Comprobar_Medicamento();
            }
        });

    }



    private void reconocerTextoDeImagen() {
        progressDialog.setMessage("Preparando  Imagen");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(Comprobation_Menu.this, uri);
            progressDialog.setMessage("Reconociendo texto");
            Task<Text> textTask = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            progressDialog.dismiss();
                            String texto= text.getText();
                            TextoReconocidoEt.setText(texto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Comprobation_Menu.this, "No se pudo reconocer el texto debido a: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(Comprobation_Menu.this, "Error al preparar la imagen: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void Comprobar_Medicamento() {
        String medicamento_escaneado = TextoReconocidoEt.getText().toString().trim();
        Log.d("DEBUG", "Texto ingresado por el usuario: " + medicamento_escaneado);
        Log.d("DEBUG", "id del paciente :v " + idPaciente);
        if (medicamento_escaneado != null && !medicamento_escaneado.isEmpty()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://lab4pharmabot.000webhostapp.com/comprobar_medicamento.php?id_paciente=" + idPaciente;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("DEBUG", "Respuesta del servidor: " + response);
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                boolean encontrado = false;

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String medicamento_servidor = jsonObject.getString("medicamento");
                                    String dosis_servidor = jsonObject.getString("dosis");

                                    boolean coincidencia = buscarPalabras(medicamento_escaneado, medicamento_servidor, dosis_servidor);

                                    if (coincidencia) {
                                        encontrado = true;
                                        break;
                                    }
                                }

                                if (encontrado) {
                                    Toast.makeText(Comprobation_Menu.this, "El medicamento y la dosis coinciden", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Comprobation_Menu.this, "No se encontró coincidencia para el medicamento y dosis", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Comprobation_Menu.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Comprobation_Menu.this, "Error al realizar la solicitud al servidor", Toast.LENGTH_SHORT).show();
                }
            }
            );


            queue.add(stringRequest);
        } else {
            Toast.makeText(Comprobation_Menu.this, "Texto ingresado por el usuario inválido", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean buscarPalabras(String texto, String medicamento, String dosis) {

        Log.d("DEBUG", "Texto recibido para búsqueda: " + texto);
        Log.d("DEBUG", "Medicamento a buscar: " + medicamento);
        Log.d("DEBUG", "Dosis a buscar: " + dosis);


        Pattern patternMedicamento = Pattern.compile("\\b" + medicamento + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcherMedicamento = patternMedicamento.matcher(texto);
        boolean encontradoMedicamento = matcherMedicamento.find();

        Pattern patternDosis = Pattern.compile("\\b" + dosis + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcherDosis = patternDosis.matcher(texto);
        boolean encontradoDosis = matcherDosis.find();

        if (encontradoMedicamento) {
            Log.d("DEBUG", "Medicamento encontrado en el texto.");
        } else {
            Log.d("DEBUG", "Medicamento no encontrado en el texto.");
        }

        if (encontradoDosis) {
            Log.d("DEBUG", "Dosis encontrada en el texto.");
        } else {
            Log.d("DEBUG", "Dosis no encontrada en el texto.");
        }

        return encontradoMedicamento && encontradoDosis;
    }



    private void AbrirCamara(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Título");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Descripción");

        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camaraARL.launch(intent);
    }

    private ActivityResultLauncher<Intent> camaraARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                    imagen.setImageURI(uri);
                    TextoReconocidoEt.setText("");
                    }else {
                        Toast.makeText(Comprobation_Menu.this, "Cancelada por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void checkPermissionsAndRequestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_BACKGROUND_LOCATION);
                } else {
                    getLocation();
                }
            } else {
                getLocation();
            }
        }
    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        final String latitud = String.valueOf(location.getLatitude());
                        final String longitud = String.valueOf(location.getLongitude());
                        final String hora = DateFormat.getTimeInstance().format(new Date());

                        enviarDatosUbicacion(latitud, longitud, hora);
                    } else {
                        Toast.makeText(Comprobation_Menu.this, "Ubicación no disponible", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarDatosUbicacion(String latitud, String longitud, String hora) {
        String url = "https://lab4pharmabot.000webhostapp.com/ubicacion.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("id_ubicacion")) {
                                int idUbicacion = jsonResponse.getInt("id_ubicacion");
                                Toast.makeText(Comprobation_Menu.this, "Datos enviados correctamente. ID: " + idUbicacion, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Comprobation_Menu.this, "Error al enviar datos: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Comprobation_Menu.this, "Error al procesar la respuesta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Comprobation_Menu.this, "Error de red: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("latitud", latitud);
                params.put("longitud", longitud);
                params.put("hora", hora);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkPermissionsAndRequestLocation();  // Revisar de nuevo los permisos en caso que esté en Android 10+
        } else if (requestCode == REQUEST_BACKGROUND_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
        }
    }
}