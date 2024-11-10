package unipi.exercise.smartalert.service;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import unipi.exercise.smartalert.model.EventReport;

public interface ApiService {
    @POST("/report")
    Call<Void> sendEventReport(@Body EventReport eventReport);
}
