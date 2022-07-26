package com.example.opencamera1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class MainActivity3 extends AppCompatActivity {
    ImageView imageView;
    Button btOpen,add;
    Bitmap bitmap;
    TextView base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        imageView = findViewById(R.id.imageview);
        btOpen = findViewById(R.id.bt_open);
        add = findViewById(R.id.add);
        base64 = findViewById(R.id.base64);

        if (ContextCompat.checkSelfPermission(MainActivity3.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity3.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
        btOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 100);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap != null){
                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();

                            String encodeImage = Base64.encodeToString(b, Base64.DEFAULT);
                            return encodeImage;
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            //Log.d("base64", s);
                            base64.setText(s);
                        }
                    }.execute();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            bitmap=(Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }
}
