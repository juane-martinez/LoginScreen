package com.example.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Information_Aux_Menu extends AppCompatActivity {

    ImageButton infop_btn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_menu);

        infop_btn= findViewById(R.id.imageButton4);

        infop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Information_Aux_Menu.this, Boss_Patient_List.class));
            }
        });
    }
}
