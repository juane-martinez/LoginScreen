package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Kardex extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kardex);

        // Obtener el ID del paciente pasado por el Intent que inició esta actividad
        String idPaciente = getIntent().getStringExtra("id_paciente");

        // Llamar al método que inicia la petición de datos del kardex
        obtenerDatosKardex(idPaciente);
    }

    private void obtenerDatosKardex(String idPaciente) {
        String url = "https://lab4pharmabot.000webhostapp.com/kardex.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            StringBuilder medicamentos = new StringBuilder();
                            StringBuilder dosis = new StringBuilder();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (i > 0) {
                                    medicamentos.append(", ");
                                    dosis.append(", ");
                                }
                                medicamentos.append(obj.getString("medicamento"));
                                dosis.append(obj.getString("dosis"));
                            }

                            // Asignar los valores a los EditTexts de la interfaz
                            EditText txtFechaIngreso = findViewById(R.id.editTextFechaI);
                            EditText txtCama = findViewById(R.id.editTextCama);
                            EditText txtMedicamento = findViewById(R.id.editTextMedicamentos);
                            EditText txtDosis = findViewById(R.id.editTextDosis);
                            EditText txtVia = findViewById(R.id.editTextVia);
                            EditText txtHorario = findViewById(R.id.editTextHorario);

                            // Si hay datos, usar el primer objeto JSON para llenar los campos que no cambian
                            if (jsonArray.length() > 0) {
                                JSONObject firstEntry = jsonArray.getJSONObject(0);
                                txtFechaIngreso.setText(firstEntry.getString("fecha_ingreso"));
                                txtCama.setText(firstEntry.getString("cama"));
                                txtVia.setText(firstEntry.getString("via"));
                                txtHorario.setText(firstEntry.getString("horario"));
                            }

                            txtMedicamento.setText(medicamentos.toString());
                            txtDosis.setText(dosis.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Kardex.this, "Error de parsing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Kardex.this, "Error de red: " + error.toString(), Toast.LENGTH_SHORT).show();
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

