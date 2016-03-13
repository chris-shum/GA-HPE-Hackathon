package com.example.android.quicktap.BreweryDbApi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by charlie on 3/12/16.
 */
public class TestApiCalls {
    public static final String BASE_URL = "http://api.brewerydb.com/v2/";
    public static final String API_KEY = "2b59764ae60e7c21e9ee2f83b428d43c";

    public static void main(String[] args) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        BreweryDbService service = retrofit.create(BreweryDbService.class);

        String query = "Goose IPA";

        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            query = query.replace(' ', '+'); // backup if url encoding fails for some reason
        }

        Call<Response> call = service.getSearchBeersCall(API_KEY, query);

        call.enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.body().getBeers().size() > 0) {
                    Beer beer = response.body().getBeers().get(0);

                    // long names
                    System.out.println(beer.getName());
                    System.out.println(beer.getStyle().getFullName());
                    System.out.println(beer.getBrewery().getFullName());

                    System.out.println();

                    // short names
                    System.out.println(beer.getDisplayName());
                    System.out.println(beer.getStyle().getShortName());
                    System.out.println(beer.getBrewery().getShortName());
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
