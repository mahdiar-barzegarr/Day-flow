package com.example.dayflow.data.remote.api

import com.example.dayflow.data.remote.ApiConfig
import com.example.dayflow.data.remote.request.LoginRequest
import com.example.dayflow.data.remote.request.RefreshRequest
import com.example.dayflow.data.remote.request.RegisterRequest
import com.example.dayflow.data.remote.response.LoginResponse
import com.example.dayflow.data.remote.response.ProfileResponse
import com.example.dayflow.data.remote.response.RefreshResponse
import com.example.dayflow.data.remote.response.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    companion object {
        val PRODUCTS_URL = "${ApiConfig.PRODUCTS_BASE}get_products/"
        val CATEGORIES_URL = "${ApiConfig.PRODUCTS_BASE}get_categories/"
        val PRODUCT_DETAIL_BASE = "${ApiConfig.PRODUCTS_BASE}get_product_detail/"
        val ADD_ITEM_URL = "${ApiConfig.CART_BASE}add_item/"
        val CART_URL = "${ApiConfig.CART_BASE}user_cart/"
        val DELETE_ITEM_BASE = "${ApiConfig.CART_BASE}delete_item/"
        val CART_CHECKOUT_URL = "${ApiConfig.CART_BASE}cart_checkout/"
    }



    @POST("user_login/")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("new_access/")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<RefreshResponse>

    @POST("user_register/")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("user_profile/")
    suspend fun getProfile(): Response<ProfileResponse>


}