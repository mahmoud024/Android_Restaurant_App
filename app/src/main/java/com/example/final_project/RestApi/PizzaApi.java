package com.example.final_project.RestApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface PizzaApi {
    @GET("/v1/pizza-types")
    Call<List<String>> getPizzaTypes();
}

