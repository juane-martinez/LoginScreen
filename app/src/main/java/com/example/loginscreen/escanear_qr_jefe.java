package com.example.loginscreen;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class escanear_qr_jefe extends AppCompatActivity {

    Button scan_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.escanear_qr_jefe);
        scan_btn = findViewById(R.id.esc_jefe_btn);
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(escanear_qr_jefe.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Escanea el Código");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
            }
        });
    }
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
                                Intent intent = new Intent(escanear_qr_jefe.this, Patient_Information.class);
                                Log.d("DEBUG", "ID del paciente: " + idPaciente);
                                intent.putExtra("id_paciente", idPaciente); // Pasar el ID del paciente
                                intent.putExtra("datosPaciente", response);
                                startActivity(intent);
                            } else {
                                Toast.makeText(escanear_qr_jefe.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(escanear_qr_jefe.this, "Error de parsing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(escanear_qr_jefe.this, "Error de red: " + error.toString(), Toast.LENGTH_SHORT).show();
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
