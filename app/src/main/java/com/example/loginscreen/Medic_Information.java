package com.example.loginscreen;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Medic_Information extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medic_information);
        String idPaciente = getIntent().getStringExtra("id_paciente");
        obtenerHistoriaClinica(idPaciente);
    }

    private void obtenerHistoriaClinica(String idPaciente) {
        String url = "https://pharmabot-lab4.000webhostapp.com/php/historia_clinica.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.has("error")) {
                                // Actualizar los TextViews con la información recibida
                                EditText txtFechaCreacion = findViewById(R.id.editTextFechaI);
                                EditText txtUltimaActualizacion = findViewById(R.id.editTextCama);
                                EditText txtDiagnostico = findViewById(R.id.editTextMedicamentos);
                                EditText txtTratamiento = findViewById(R.id.editTextDosis);

                                txtFechaCreacion.setText(obj.getString("fecha_creacion"));
                                txtUltimaActualizacion.setText(obj.getString("ultima_actualizacion"));
                                txtDiagnostico.setText(obj.getString("diagnostico"));
                                txtTratamiento.setText(obj.getString("tratamiento"));
                            } else {
                                Toast.makeText(Medic_Information.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Medic_Information.this, "Error de parsing: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Medic_Information.this, "Error de red: " + error.toString(), Toast.LENGTH_SHORT).show();
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
