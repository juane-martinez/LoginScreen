package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Assistant_Menu extends AppCompatActivity {
    ImageButton comprobar_btn;
    ImageButton verificar_btn;
    ImageButton registrar_btn;

    String idPaciente;
    String datosPaciente;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.assistant_main_menu);

            comprobar_btn=findViewById(R.id.imageButton2);
            verificar_btn=findViewById(R.id.imageButton);
            registrar_btn=findViewById(R.id.imageButton3);

            idPaciente = getIntent().getStringExtra("id_paciente");
            datosPaciente = getIntent().getStringExtra("datosPaciente");
            comprobar_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startActivity(new Intent(Assistant_Menu.this,Comprobation_Menu.class ));
                    Intent intent = new Intent(Assistant_Menu.this, Comprobation_Menu.class);
                    intent.putExtra("id_paciente", idPaciente);
                    startActivity(intent);
                }
            });

            verificar_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Assistant_Menu.this, Patient_Information.class);
                    //intent.putExtra("id_paciente", idPaciente);
                    intent.putExtra("id_paciente", idPaciente); // Pasar el ID del paciente
                    intent.putExtra("datosPaciente", datosPaciente); // Pasar los datos del paciente como JSON String
                    startActivity(intent);
                }
            });

            registrar_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent =(new Intent(Assistant_Menu.this, registrar_medicamento.class));
                    intent.putExtra("id_paciente", idPaciente);
                    startActivity(intent);
                }
            });
        }

}

