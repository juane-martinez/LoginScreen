package com.example.loginscreen;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
public class Medication_Record_Aux extends AppCompatActivity  {

    Button capture_btn;
    Button scanner_btn;
    ImageView imagen;
    EditText TextoReconocidoEt;
    private Uri uri= null;
    private ProgressDialog progressDialog;
    private TextRecognizer textRecognizer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_medicamentos_aux);
        capture_btn = findViewById(R.id.ReconocerTXT);
        scanner_btn = findViewById(R.id.EscanearTXT);
        imagen = findViewById(R.id.imagen);
        TextoReconocidoEt = findViewById(R.id.TextoReconocidoEt);
        progressDialog = new ProgressDialog(Medication_Record_Aux.this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        capture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Medication_Record_Aux.this, "Abriendo cámara", Toast.LENGTH_SHORT).show();
                AbrirCamara();
            }
        });

        scanner_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri == null){
                    Toast.makeText(Medication_Record_Aux.this, "Por favor seleccione una imagen",Toast.LENGTH_SHORT).show();
                }else {
                    reconocerTextoDeImagen();
                }
            }
        });

    }

    private void reconocerTextoDeImagen() {
        progressDialog.setMessage("Preparando  Imagen");
        progressDialog.show();

        try {
            InputImage inputImage = InputImage.fromFilePath(Medication_Record_Aux.this, uri);
            progressDialog.setMessage("Reconociendo texto");
            Task<Text> textTask = textRecognizer.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            progressDialog.dismiss();
                            String texto= text.getText();
                            TextoReconocidoEt.setText(texto);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Medication_Record_Aux.this, "No se pudo reconocer el texto debido a: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (IOException e) {
            progressDialog.dismiss();
            Toast.makeText(Medication_Record_Aux.this, "Error al preparar la imagen: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void AbrirCamara(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Título");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Descripción");

        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        camaraARL.launch(intent);
    }

    private ActivityResultLauncher<Intent> camaraARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        imagen.setImageURI(uri);
                        TextoReconocidoEt.setText("");
                    }else {
                        Toast.makeText(Medication_Record_Aux.this, "Cancelada por el usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

}
