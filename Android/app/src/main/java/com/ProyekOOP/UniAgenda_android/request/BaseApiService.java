package com.ProyekOOP.UniAgenda_android.request;

import com.ProyekOOP.UniAgenda_android.model.Login;
import com.ProyekOOP.UniAgenda_android.model.Signup;
import com.ProyekOOP.UniAgenda_android.model.Task;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BaseApiService {
    @POST("/user/login")
    Call<String> login(@Body Login request);

    @POST("/user/signup")
    Call<String> signup(@Body Signup request);

    @POST("/task/addTask")
    Call<String> addTask(@Body Task request);

    @GET("/task/getAllTask")
    Call<List<Task>> getAllTask();

    @GET("/task/getTaskById")
    Call<Task> getTaskById(@Path("task_id") String taskId);

}
