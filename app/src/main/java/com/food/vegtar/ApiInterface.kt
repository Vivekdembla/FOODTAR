package com.food.vegtar

import retrofit2.Call
import retrofit2.http.GET


interface ApiInterface {
    @GET("v2.1/get-time-zone?key=5IGBCZW9I7X4&format=json&by=zone&zone=Asia/Kolkata")
    fun getData(): Call<MyData>
}