package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class Assistant_Menu extends AppCompatActivity {
    ImageButton comprobar_btn;
    ImageButton verificar_btn;
    ImageButton registrar_btn;
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.assistant_main_menu);

            comprobar_btn=findViewById(R.id.imageButton2);
            verificar_btn=findViewById(R.id.imageButton);
            registrar_btn=findViewById(R.id.imageButton3);
            comprobar_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Assistant_Menu.this,Comprobation_Menu.class ));
                }
            });

            verificar_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Assistant_Menu.this, Information_Aux_Menu.class));
                }
            });

            registrar_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Assistant_Menu.this, Medication_Record_Aux.class));
                }
            });
        }
}
