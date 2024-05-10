package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button scan_btn;
    Button login_btn;
    TextView textView;
    EditText edtUsuario, edtPassword;
    String usuario, password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scan_btn = findViewById(R.id.button_code);
        textView= findViewById(R.id.textView2);
        edtUsuario = findViewById(R.id.editTextText);
        edtPassword =findViewById(R.id.editTextTextPassword);

        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(MainActivity.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Escanea el Código");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });

        login_btn=findViewById(R.id.button_login);
       login_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               usuario = edtUsuario.getText().toString();
               password = edtPassword.getText().toString();
               if (!usuario.isEmpty() && !password.isEmpty()){
                   validarUsuario("https://lab4pharmabot.000webhostapp.com/validar_usuario.php");
               }else{
                   Toast.makeText(MainActivity.this, "No se permiten campos vacios", Toast.LENGTH_SHORT).show();
               }
               //startActivity(new Intent(MainActivity.this,Boss_Menu.class ));
           }
       });
    }

    private void validarUsuario(String URL){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("error")) {
                                Toast.makeText(MainActivity.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                            } else {
                                int tipoUsuario = obj.getInt("tipo_usuario");
                                Intent intent;
                                if (tipoUsuario == 0) {
                                    intent = new Intent(getApplicationContext(), Assistant_Menu.class);
                                } else if (tipoUsuario == 1) {
                                    intent = new Intent(getApplicationContext(), Boss_Menu.class);
                                } else {
                                    throw new IllegalArgumentException("Tipo de usuario no soportado");
                                }
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error de parsing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", error.toString());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("usuario", usuario);
                parametros.put("password", password);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        if (intentResult !=null){
            String contents = intentResult.getContents();
            if (contents != null){
                textView.setText(intentResult.getContents());

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null && intentResult.getContents() != null) {
            String idPaciente = intentResult.getContents();
            obtenerInformacionPaciente(idPaciente);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void obtenerInformacionPaciente(String idPaciente) {
        String url = "https://lab4pharmabot.000webhostapp.com/consultaQR.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.has("error")) {
                                Intent intent = new Intent(MainActivity.this, Patient_Information.class);
                                intent.putExtra("id_paciente", idPaciente); // Pasar el ID del paciente
                                intent.putExtra("datosPaciente", response);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error de parsing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error de red: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_paciente", idPaciente);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}