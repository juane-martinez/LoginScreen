package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class Patient_Information extends AppCompatActivity {
    EditText nombre, apellido, fechaN, sexo, direccion, telefono;
    ImageButton historiac;
    ImageButton btnKardex;
    ImageButton btnHistorialProcedimientos;
    String idPaciente;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_information);

        // Inicializar vistas
        nombre = findViewById(R.id.editTextFechaI);
        apellido = findViewById(R.id.editTextCama);
        fechaN = findViewById(R.id.editTextMedicamentos);
        sexo = findViewById(R.id.editTextDosis);
        direccion = findViewById(R.id.editTextDireccion);
        telefono = findViewById(R.id.editTextTelefono);
        historiac = findViewById(R.id.imageButtonHistoriaC);
        btnKardex = findViewById(R.id.imageButtonKardex);
        btnHistorialProcedimientos= findViewById(R.id.imageButtonHistorialP);

        // Obtener ID del paciente
        idPaciente = getIntent().getStringExtra("id_paciente");

        // Obtener datos del paciente y mostrarlos en las vistas
        String datosPaciente = getIntent().getStringExtra("datosPaciente");
        if (datosPaciente != null) {
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
                Toast.makeText(this, "Error al obtener datos del paciente", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se recibieron datos del paciente", Toast.LENGTH_SHORT).show();
        }

        // Configurar el botón para ir a la actividad Medic_Information
        historiac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_Information.this, Medic_Information.class);
                intent.putExtra("id_paciente", idPaciente);
                startActivity(intent);
            }
        });

        // Configurar el botón para ir a la actividad Kardex
        btnKardex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_Information.this, Kardex.class);
                intent.putExtra("id_paciente", idPaciente);
                startActivity(intent);
            }
        });

        btnHistorialProcedimientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Patient_Information.this, Historial_Procedimientos.class);
                intent.putExtra("id_paciente", idPaciente);
                startActivity(intent);
            }
        });

    }
}
