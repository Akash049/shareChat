package com.assignment.akashchandra.sharechatassignment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.akashchandra.sharechatassignment.model.requestPostData;
import com.assignment.akashchandra.sharechatassignment.model.responsePostData;
import com.assignment.akashchandra.sharechatassignment.restAPI.APIClient;
import com.assignment.akashchandra.sharechatassignment.restAPI.apiInterface;
import com.assignment.akashchandra.sharechatassignment.service.DataFetchService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

public class test extends AppCompatActivity {

    private static final String BROADCAST_ACTION = "com.assignment.akashchandra.sharechatassignment.BROADCAST";
    Intent mServiceIntent;
    TextView mdata;
    private MyWebRequestReceiver receiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Button get = (Button)findViewById(R.id.get);
        final ProgressBar pg = (ProgressBar)findViewById(R.id.pg);
        mdata= (TextView)findViewById(R.id.data);
        IntentFilter filter = new IntentFilter(MyWebRequestReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyWebRequestReceiver();
        registerReceiver(receiver, filter);
        mdata.setText("Rest mode");
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mdata.setText("Fetching data using the service");
                Intent msgIntent = new Intent(test.this, DataFetchService.class);
                msgIntent.putExtra(DataFetchService.REQUEST_STRING, "http://www.amazon.com");
                startService(msgIntent);
                Toast.makeText(getApplicationContext(),"Working",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public class MyWebRequestReceiver extends BroadcastReceiver{

        public static final String PROCESS_RESPONSE = "com.assignment.akashchandra.sharechatassignment.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            String responseString = intent.getStringExtra(DataFetchService.RESPONSE_STRING);
            String reponseMessage = intent.getStringExtra(DataFetchService.RESPONSE_MESSAGE);
            mdata.setText(responseString);
            mdata.setTextColor(getResources().getColor(R.color.white));
        }


    }



}
