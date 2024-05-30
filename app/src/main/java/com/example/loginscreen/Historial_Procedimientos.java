package com.example.loginscreen;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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

public class Historial_Procedimientos extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_procedimientos);

        String idPaciente = getIntent().getStringExtra("id_paciente");

        obtenerHistorialProcedimientos(idPaciente);
    }

    private void obtenerHistorialProcedimientos(String idPaciente) {
        String url = "https://lab4pharmabot.000webhostapp.com/histrorial_aplicaciones.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            StringBuilder fechas = new StringBuilder();
                            StringBuilder medicamentos = new StringBuilder();
                            StringBuilder dosis = new StringBuilder();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (i > 0) {
                                    fechas.append(", ");
                                    medicamentos.append(", ");
                                    dosis.append(", ");
                                }
                                fechas.append(obj.getString("fecha_aplicacion"));
                                medicamentos.append(obj.getString("medicamento"));
                                dosis.append(obj.getString("dosis"));
                            }

                            // Asignar los valores a los EditTexts de la interfaz
                            EditText txtIdentificacion = findViewById(R.id.editTextIdentificacion);
                            EditText txtFechaAplicacion = findViewById(R.id.editTextFechaAplicacion);
                            EditText txtMedicamento = findViewById(R.id.editTextMedicamentos);
                            EditText txtDosis = findViewById(R.id.editTextDosis);

                            txtIdentificacion.setText(idPaciente);
                            txtFechaAplicacion.setText(fechas.toString());
                            txtMedicamento.setText(medicamentos.toString());
                            txtDosis.setText(dosis.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Historial_Procedimientos.this, "Error de parsing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Historial_Procedimientos.this, "Error de red: " + error.toString(), Toast.LENGTH_SHORT).show();
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
