package unipi.exercise.smartalert.httpclient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import unipi.exercise.smartalert.service.ApiService;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
