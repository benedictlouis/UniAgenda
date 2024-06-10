package com.ProyekOOP.UniAgenda_android.request;

import com.ProyekOOP.UniAgenda_android.model.Login;
import com.ProyekOOP.UniAgenda_android.model.LoginResponse;
import com.ProyekOOP.UniAgenda_android.model.Signup;
import com.ProyekOOP.UniAgenda_android.model.Task;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BaseApiService {
    //@POST("/user/login")
    //Call<String> login(@Body Login request);

    @POST("/user/login")
    Call<LoginResponse> login(@Body Login login);

    @POST("/user/signup")
    Call<String> signup(@Body Signup request);

    @POST("/task/addTask")
    Call<String> addTask(@Body Task request);

    @GET("/task/getAllTask")
    Call<List<Task>> getAllTask();

    @GET("/task/getTaskById/{task_id}")
    Call<Task> getTaskById(@Path("task_id") int taskId);

    @PUT("task/updateTask/{task_id}")
    Call<Task> updateTask(@Path("task_id") int taskId, @Body Task task);

    @DELETE("task/deleteTask/{task_id}")
    Call<Void> deleteTask(@Path("task_id") int taskId);


}
