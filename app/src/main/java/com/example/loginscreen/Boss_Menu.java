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
public class Boss_Menu extends AppCompatActivity{

    ImageButton infop_btn;
    ImageButton verificarp_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boss_main_menu);

        infop_btn=findViewById(R.id.imageButton2);
        verificarp_btn=findViewById(R.id.imageButton);

        infop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Boss_Menu.this, Boss_Patient_Info.class));
            }
        });

        verificarp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(Boss_Menu.this);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Escanea el Código");
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                intentIntegrator.initiateScan();
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
