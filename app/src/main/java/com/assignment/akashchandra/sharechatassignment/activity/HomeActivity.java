package com.assignment.akashchandra.sharechatassignment.activity;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.akashchandra.sharechatassignment.R;
import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;
import com.assignment.akashchandra.sharechatassignment.model.DataModel;
import com.assignment.akashchandra.sharechatassignment.model.requestPostData;
import com.assignment.akashchandra.sharechatassignment.model.responsePostData;
import com.assignment.akashchandra.sharechatassignment.restAPI.APIClient;
import com.assignment.akashchandra.sharechatassignment.restAPI.apiInterface;
import com.assignment.akashchandra.sharechatassignment.service.DownloadImage;
import com.bumptech.glide.Glide;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity{
    protected boolean shouldAskPermissions(){ return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);}
    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<DataModel> data = new ArrayList<>();
    List<DataModel> download = new ArrayList<>();
    private PostScreen adapter;
    DataBaseHandler db;
    TextView header;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        db = new DataBaseHandler(this);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeContainer);
        header = (TextView)findViewById(R.id.header);
        if (shouldAskPermissions()) {
            askPermissions();
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refreshItems(-1);
            }
        });

        if(!db.isEmpty()){
            data = db.getData();
            recyclerView = (RecyclerView)findViewById(R.id.recycleView);
            adapter = new PostScreen(data,HomeActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }

    }



    public void refreshItems(final int id)
    {
        requestPostData reqData = new requestPostData();
        reqData.setRequest_id(APIClient.REQUEST_ID);
        if(id!=-1)
        {
            reqData.setId_offset(id);
        }
        apiInterface apiService = APIClient.getClient().create(apiInterface.class);
        Call<responsePostData> call = apiService.requestData(reqData);
        call.enqueue(new Callback<responsePostData>() {
            @Override
            public void onResponse(Call<responsePostData> call, Response<responsePostData> response) {
                if(response.body().getSuccess().equals("true"))
                {
                    download = response.body().getData();
                    try{
                        if(id==-1){
                            try{
                                db.Erase();
                            }catch (Exception e){
                                Toast.makeText(getApplicationContext(),"Unable to update data",Toast.LENGTH_SHORT).show();
                            }
                            db.addPostDataList(download);
                            adapter = new PostScreen(download,HomeActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);
                            startService(new Intent(HomeActivity.this, DownloadImage.class));
                        }else{
                            db.addPostDataList(download);
                            for(int i = 0 ;i<download.size();i++)
                            {
                                data.add(download.get(i));
                            }
                            adapter = new PostScreen(data,HomeActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(adapter);
                            startService(new Intent(HomeActivity.this, DownloadImage.class));

                        }
                        Toast.makeText(getApplicationContext(),"Data updated "+db.count(),Toast.LENGTH_SHORT).show();

                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }else{
                    Toast.makeText(getApplicationContext(),"Unable to download new Posts",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<responsePostData> call, Throwable t) {

            }
        });
    }


    public class PostScreen extends RecyclerView.Adapter<PostScreen.MyViewHolder>{

        private List<DataModel> list;
        private Context context;

        public PostScreen(List<DataModel> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @Override
        public PostScreen.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.listitem_home_activity, parent, false);
            return new PostScreen.MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            final DataModel data = list.get(position);
            String uri = "";
            holder.pg.setVisibility(View.GONE);
            if(data.getType().equals("profile"))
            {
                try{
                    Glide.with(HomeActivity.this)
                            .load(data.getProfile_url())
                            .into(holder.profileImage);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }

                holder.profileView.setVisibility(View.VISIBLE);
                holder.userName.setText(data.getAuthor_name());
                holder.userDob.setText(data.getAuthor_dob());
                holder.userGender.setText(data.getAuthor_gender());
                holder.userStatus.setText(data.getAuthor_status());
            }
            else {
                holder.postData.setVisibility(View.VISIBLE);

                try{
                    Glide.with(HomeActivity.this)
                            .load(data.getUrl())
                            .into(holder.postImage);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }

                holder.name.setText(data.getAuthor_name());
                holder.postDate.setText(toDate(data.getPostedOn()));
            }

            if(position == list.size() -1){
                holder.loadMore.setVisibility(View.VISIBLE);
            }
            holder.loadMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.pg.setVisibility(View.VISIBLE);
                    refreshItems(Integer.valueOf(data.getId()));
                }
            });
            holder.profileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        startActivity(new Intent(HomeActivity.this,ProfileDetailsActivity.class).putExtra("ID",data.getId().toString()));

                }
            });
            holder.saveID.setText(data.getId());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout profileView,postData;
            public TextView userName, userAge, userDob,userStatus,userGender,name,postDate,saveID;
            public ImageView profileImage,postImage;
            public Button loadMore;
            public ProgressBar pg;

            public MyViewHolder(View view) {
                super(view);
                profileView = (LinearLayout)view.findViewById(R.id.profileView);
                profileImage = (ImageView)view.findViewById(R.id.profileImage);
                userName = (TextView)view.findViewById(R.id.userName);
                userDob = (TextView)view.findViewById(R.id.userDob);
                userStatus = (TextView)view.findViewById(R.id.userStatus);
                userGender = (TextView)view.findViewById(R.id.userGender);
                name = (TextView)view.findViewById(R.id.name);
                postDate = (TextView)view.findViewById(R.id.postDate);
                postData = (LinearLayout)view.findViewById(R.id.postData);
                postImage = (ImageView)view.findViewById(R.id.postImage);
                loadMore = (Button)view.findViewById(R.id.loadMore);
                saveID = (TextView)view.findViewById(R.id.saveID);
                pg = (ProgressBar)view.findViewById(R.id.pg);

            }
        }

        private String toDate(long timestamp) {
            Date date = new Date (timestamp * 1000);
            return DateFormat.getInstance().format(date).toString();
        }

    }
}
