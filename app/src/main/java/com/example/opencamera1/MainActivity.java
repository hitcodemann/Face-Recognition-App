 package com.example.opencamera1;

import androidx.annotation.NonNull;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


 public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
     public static final int CAMERA_REQUEST_CODE = 102;
     private ImageView imageView;
     private Button openCamera,button;
     Bitmap image;
     String base64;
     TextView textView;
     FaceRecognitionApi faceRecognitionApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.capturedImage);
        openCamera = findViewById(R.id.openCamera);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        if(getSupportActionBar() !=null){
            getSupportActionBar().hide();
        }

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image != null){
                    new AsyncTask<Void, Void, String>() {
                        @Override
                        protected String doInBackground(Void... voids) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();

                            String encodeImage = Base64.encodeToString(b, Base64.DEFAULT);
                            return encodeImage;
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            //Log.d("base64", s);
                            base64 = s;

                            createPost(s);
                            getPosts();

                        }
                    }.execute();
                }

            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://desolate-plateau-39021.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        faceRecognitionApi = retrofit.create(FaceRecognitionApi.class);



    }

    private void createPost(String s) {

        PostRequest postRequest = new PostRequest(s);

        Call<PostRequest> call = faceRecognitionApi.createPost(postRequest);

        call.enqueue(new Callback<PostRequest>() {
            @Override
            public void onResponse(Call<PostRequest> call, Response<PostRequest> response) {
                Toast.makeText(MainActivity.this, "Post Successful", Toast.LENGTH_SHORT).show();
                Log.e("Post Response Code:", String.valueOf(response.code()));
            }

            @Override
            public void onFailure(Call<PostRequest> call, Throwable t) {
                Log.e("failed",t.getMessage());
            }
        });

    }

    private void getPosts() {

        Call<Post> call = faceRecognitionApi.getPosts();

        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {
                if (!response.isSuccessful()) {
                    textView.setText("Code: " + response.code());
                    return;
                }

                Post post = response.body();
                Toast.makeText(MainActivity.this, "This is " + post.getName(), Toast.LENGTH_SHORT).show();
                textView.setText(post.getName());

            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {

            }
        });

    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA},CAMERA_PERM_CODE);
        }else{
            openCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                Toast.makeText(this, "Camera Permission required to use the camera ", Toast.LENGTH_SHORT).show();
            }
        }
    }

     private void openCamera() {
            Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera,CAMERA_REQUEST_CODE);
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode,Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if(requestCode==CAMERA_REQUEST_CODE){
             image = (Bitmap) data.getExtras().get("data");
             imageView.setImageBitmap(image);


         }
     }
 }

