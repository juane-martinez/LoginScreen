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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Kardex extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kardex);
        String idPaciente = getIntent().getStringExtra("id_paciente");
        obtenerDatosKardex(idPaciente);
    }

    private void obtenerDatosKardex(String idPaciente) {
        String url = "https://pharmabot-lab4.000webhostapp.com/php/kardex.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.has("error")) {
                                EditText txtFechaIngreso = findViewById(R.id.editTextFechaI);
                                EditText txtCama = findViewById(R.id.editTextCama);
                                EditText txtMedicamento = findViewById(R.id.editTextMedicamentos);
                                EditText txtDosis = findViewById(R.id.editTextDosis);
                                EditText txtVia = findViewById(R.id.editTextVia);
                                EditText txtHorario = findViewById(R.id.editTextHorario);

                                txtFechaIngreso.setText(obj.getString("fecha_ingreso"));
                                txtCama.setText(obj.getString("cama"));
                                txtMedicamento.setText(obj.getString("medicamento"));
                                txtDosis.setText(obj.getString("dosis"));
                                txtVia.setText(obj.getString("via"));
                                txtHorario.setText(obj.getString("horario"));
                            } else {
                                Toast.makeText(Kardex.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                            }
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
