package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class Patient_Information extends AppCompatActivity {
    EditText nombre, apellido, fechaN, sexo, direccion, telefono;
    ImageButton historiac;

    String idPaciente;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_information);

        EditText nombre = findViewById(R.id.editTextFechaC);
        EditText apellido = findViewById(R.id.editTextFechaU);
        EditText fechaN = findViewById(R.id.editTextDiagnostico);
        EditText sexo = findViewById(R.id.editTextTratamiento);
        EditText direccion= findViewById(R.id.editTextDireccion);
        EditText telefono = findViewById(R.id.editTextTelefono);
        ImageButton historiac = findViewById(R.id.imageButtonHistoriaC);

        String idPaciente = getIntent().getStringExtra("id_paciente");
        String datosPaciente = getIntent().getStringExtra("datosPaciente");
        try {
            JSONObject obj = new JSONObject(datosPaciente);
            nombre.setText(obj.getString("nombre"));
            apellido.setText(obj.getString("apellido"));
            fechaN.setText(obj.getString("fecha_nacimiento"));
            sexo.setText(obj.getString("sexo"));
            direccion.setText(obj.getString("direccion"));
            telefono.setText(obj.getString("telefono"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        historiac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_Information.this, Medic_Information.class);
                intent.putExtra("id_paciente", idPaciente);
                startActivity(intent);
            }
        });
    }
}
