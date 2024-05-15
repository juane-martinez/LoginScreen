package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
public class Boss_Menu extends AppCompatActivity {

    ImageButton infop_btn;
    ImageButton verificarp_btn;
    String idPaciente;
    String datosPaciente;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boss_main_menu);

        infop_btn=findViewById(R.id.imageButton2);
        verificarp_btn=findViewById(R.id.imageButton);


        infop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Boss_Menu.this, Boss_Patient_List.class));
            }
        });

        verificarp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Boss_Menu.this, escanear_qr_jefe.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);
        if (intentResult !=null){
            String contents = intentResult.getContents();
            if (contents != null){
                //Action
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
