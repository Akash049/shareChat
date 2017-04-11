package com.assignment.akashchandra.sharechatassignment.restAPI;

import com.assignment.akashchandra.sharechatassignment.model.requestPostData;
import com.assignment.akashchandra.sharechatassignment.model.requestUpdateData;
import com.assignment.akashchandra.sharechatassignment.model.responsePostData;
import com.assignment.akashchandra.sharechatassignment.model.responseUpdateData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

public interface apiInterface {

    @POST("http://35.154.143.154:8000/data")
    Call<responsePostData> requestData(@Body requestPostData data);

    @POST("http://35.154.143.154:8000/update")
    Call<responseUpdateData> updateProfile(@Body requestUpdateData data);

}
