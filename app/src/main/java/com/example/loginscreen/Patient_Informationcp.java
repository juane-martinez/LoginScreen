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

public class Patient_Informationcp extends AppCompatActivity {
    EditText nombre, apellido, fechaN, sexo, direccion, telefono;
    ImageButton historiac;
    ImageButton btnKardex;
    String idPaciente;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_information);

        nombre = findViewById(R.id.editTextFechaI);
        apellido = findViewById(R.id.editTextCama);
        fechaN = findViewById(R.id.editTextMedicamentos);
        sexo = findViewById(R.id.editTextDosis);
        direccion= findViewById(R.id.editTextDireccion);
        telefono = findViewById(R.id.editTextTelefono);
        historiac = findViewById(R.id.imageButtonHistoriaC);
        idPaciente = getIntent().getStringExtra("id_paciente");
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
                Intent intent = new Intent(Patient_Informationcp.this, Medic_Information.class);
                intent.putExtra("id_paciente", idPaciente);
                startActivity(intent);
            }
        });

        ImageButton btnKardex = findViewById(R.id.imageButtonKardex);
        btnKardex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_Informationcp.this, Kardex.class);
                intent.putExtra("id_paciente", idPaciente);
                startActivity(intent);
            }
        });
    }
}
