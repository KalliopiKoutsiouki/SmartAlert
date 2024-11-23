package unipi.exercise.smartalert.service;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import unipi.exercise.smartalert.model.Event;
import unipi.exercise.smartalert.model.EventNotification;
import unipi.exercise.smartalert.model.EventReport;

public interface ApiService {

    @GET("/important")
    Call<List<Event>> getImportantEvents();

    @POST("/report")
    Call<Void> sendEventReport(@Body EventReport eventReport);

    @POST("/eventNotification")
    Call<Void> sendNotification(@Body EventNotification eventNotification);

    @POST("/rejectEvent")
    Call<Void> rejectEvent(@Body Long eventId);

}
