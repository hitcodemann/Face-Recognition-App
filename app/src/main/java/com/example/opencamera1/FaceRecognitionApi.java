package com.example.opencamera1;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface FaceRecognitionApi {

    @GET("/")
    Call<Post> getPosts();

    @POST("/")
    Call<PostRequest> createPost(@Body PostRequest postRequest);
}
