package com.example.loginscreen;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class Boss_Patient_List extends AppCompatActivity {
    private LinearLayout patientsLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boss_patient_list);
        patientsLayout = findViewById(R.id.patients_layout);

        fetchPatients();
    }

    private void fetchPatients() {
        String url = "https://lab4pharmabot.000webhostapp.com/Jefe_pacientes.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("FetchPatients", "Server Response: " + response);
                        try {
                            JSONArray patients = new JSONArray(response);
                            if (patients.length() == 0) {
                                Log.d("FetchPatients", "No patients found");
                                Toast.makeText(Boss_Patient_List.this, "No patients found", Toast.LENGTH_LONG).show();
                            }
                            for (int i = 0; i < patients.length(); i++) {
                                JSONObject patient = patients.getJSONObject(i);
                                final String idPaciente = patient.getString("id_paciente");
                                String nombre = patient.getString("nombre");
                                String apellido = patient.getString("apellido");
                                String fecha_nacimiento= patient.getString("fecha_nacimiento");
                                String sexo= patient.getString("sexo");
                                String direccion= patient.getString("direccion");
                                String telefono= patient.getString("telefono");


                                Button btn = new Button(Boss_Patient_List.this);
                                btn.setText(idPaciente + " - " + nombre + " " + apellido);
                                btn.setContentDescription("Botón para " + nombre + " " + apellido);

                                // Estilo del botón
                                btn.setBackgroundTintList(ContextCompat.getColorStateList(Boss_Patient_List.this, R.color.buttons));
                                btn.setTextColor(ContextCompat.getColor(Boss_Patient_List.this, R.color.white));
                                btn.setTextSize(20);
                                btn.setTypeface(btn.getTypeface(), Typeface.BOLD);
                                btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Log.d("Passing ID", "Passing ID: " + idPaciente);
                                        Intent intent = new Intent(Boss_Patient_List.this, Patient_Information.class);
                                        intent.putExtra("id_paciente", idPaciente);
                                        intent.putExtra("datosPaciente", patient.toString());
                                        startActivity(intent);
                                    }
                                });
                                patientsLayout.addView(btn);
                                Log.d("FetchPatients", "Button added for: " + idPaciente + " - " + nombre + " " + apellido);
                            }
                        } catch (JSONException e) {
                            Log.e("FetchPatients", "JSON parsing error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("FetchPatients", "Error fetching data: " + error.toString());
                Toast.makeText(Boss_Patient_List.this, "Error fetching data: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(request);
    }
}