package com.laskstud.blingslot.api;

import com.laskstud.blingslot.models.oldgoodtales.FirstQueryModel;
import com.laskstud.blingslot.models.oldgoodtales.SecondQueryModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OldGoodTalesApi {

    @GET("/testing/index6.php")
    Call<FirstQueryModel> getData(@Query("country") String country, @Query("tz") int tz);

    @GET("/testing/index6.php")
    Call<FirstQueryModel> getData(@Query("country") String country, @Query("id") int id, @Query("tz") int tz);

    @GET("/testing/index6.php")
    Call<List<SecondQueryModel>> getData(@Query("id") int id);
}
