package com.example.android.quicktap.BreweryDbApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by charlie on 3/12/16.
 */
public interface BreweryDbService {
    @GET("search?type=beer&withBreweries=Y")
    Call<Response> getSearchBeersCall(
            @Query("key") String apiKey,
            @Query("q") String query
    );
}
