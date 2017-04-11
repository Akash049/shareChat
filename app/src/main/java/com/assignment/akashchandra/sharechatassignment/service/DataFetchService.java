package com.assignment.akashchandra.sharechatassignment.service;


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.assignment.akashchandra.sharechatassignment.model.requestPostData;
import com.assignment.akashchandra.sharechatassignment.model.responsePostData;
import com.assignment.akashchandra.sharechatassignment.restAPI.APIClient;
import com.assignment.akashchandra.sharechatassignment.restAPI.apiInterface;
import com.assignment.akashchandra.sharechatassignment.test;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Akash Chandra on 10-04-2017.
 */

public class DataFetchService extends IntentService {

    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";
    public String responseString;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataFetchService(String name) {
        super("DataFetchService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String requestString = intent.getStringExtra(REQUEST_STRING);

        try{
            requestPostData data = new requestPostData();
            data.setRequest_id(APIClient.REQUEST_ID);
            apiInterface apiService = APIClient.getClient().create(apiInterface.class);
            Call<responsePostData> call = apiService.requestData(data);
            call.enqueue(new Callback<responsePostData>() {
                @Override
                public void onResponse(Call<responsePostData> call, Response<responsePostData> response) {
                    responseString = response.body().getSuccess().toString();
                    if(response.body().getSuccess().equals("true"))
                    {
                        Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT);

                    }else{
                        Toast.makeText(getApplicationContext(),"Unable to download new Posts",Toast.LENGTH_SHORT);
                    }

                }
                @Override
                public void onFailure(Call<responsePostData> call, Throwable t) {
                    responseString = t.getMessage().toString();
                }
            });

        }catch (Exception e){
            responseString = e.getMessage().toString();
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(test.MyWebRequestReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RESPONSE_STRING, responseString);
        broadcastIntent.putExtra(RESPONSE_MESSAGE, responseString);
        sendBroadcast(broadcastIntent);

    }
}
