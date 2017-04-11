package com.assignment.akashchandra.sharechatassignment.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.akashchandra.sharechatassignment.R;
import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;
import com.assignment.akashchandra.sharechatassignment.model.DataModel;
import com.assignment.akashchandra.sharechatassignment.model.ProfileModel;
import com.assignment.akashchandra.sharechatassignment.model.requestUpdateData;
import com.assignment.akashchandra.sharechatassignment.model.responseUpdateData;
import com.assignment.akashchandra.sharechatassignment.restAPI.APIClient;
import com.assignment.akashchandra.sharechatassignment.restAPI.apiInterface;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileDetailsActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView userProfileImage;
    Toolbar toolbar;
    String user_id;
    DataModel mdata;
    DataBaseHandler db;
    TextView userName,dob;
    EditText status,contact;
    Button save;
    RadioGroup genderSelect;
    RadioButton male,female;
    int dateChange = 0;
    static final int DATE_DIALOG_ID = 999;
    int year,month,day;
    String uName,uContact,uStatus,uDob,uGender,uIMG;
    String nContact,nStatus,nDob,nGender;
    ProgressBar pg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);
        toolbar = (Toolbar)findViewById(R.id.MyToolbar);
        userProfileImage = (ImageView)findViewById(R.id.userProfileImage);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ProfileDetailsActivity.this,HomeActivity
                            .class));
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initialize();
        db = new DataBaseHandler(this);
        try{
            Intent in = getIntent();
            user_id = in.getStringExtra("ID");
            //Toast.makeText(getApplicationContext(),user_id,Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Invalid ID",Toast.LENGTH_SHORT).show();
        }
        try{
            mdata = db.getProfile(user_id);
            uName = mdata.getAuthor_name();
            uIMG = mdata.getProfile_url();
            uContact = mdata.getAuthor_contact();
            uStatus = mdata.getAuthor_status();
            uDob = mdata.getAuthor_dob();
            uGender = mdata.getAuthor_gender();
           // Toast.makeText(getApplicationContext(),uIMG,Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
        Glide.with(ProfileDetailsActivity.this)
                .load(uIMG)
                .into(userProfileImage);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitle(uName);
        collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.bluecolor));
        status.setText(uStatus);
        dob.setText(uDob);
        contact.setText(uContact);
        //Toast.makeText(getApplicationContext(),uGender,Toast.LENGTH_SHORT).show();
        if(uGender.equals("male")){
            male.setChecked(true);
        }else{
            female.setChecked(true);
        }

        genderSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId){
                    case R.id.maleSelect : nGender = "male";
                        //Toast.makeText(getApplicationContext(),"Male Selected",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.femaleSelect : nGender = "female";
                        //Toast.makeText(getApplicationContext(),"female Selected",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    public void initialize(){
        status = (EditText)findViewById(R.id.statusField);
        contact = (EditText) findViewById(R.id.contactField);
        dob = (TextView)findViewById(R.id.dobText);
        male = (RadioButton)findViewById(R.id.maleSelect);
        female = (RadioButton)findViewById(R.id.femaleSelect);
        genderSelect = (RadioGroup)findViewById(R.id.genderSelect);
        dob.setOnClickListener(this);
        save = (Button)findViewById(R.id.save);
        pg = (ProgressBar)findViewById(R.id.pg);
        save.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.dobText:
            showDialog(DATE_DIALOG_ID);break;

            case R.id.save :
                pg.setVisibility(View.VISIBLE);
                save.setVisibility(View.GONE);
                update(); break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
            Date _date = new Date(selectedYear, selectedMonth, selectedDay-1);
            String dayOfWeek = simpledateformat.format(_date);
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            nDob = selectedDay+"/"+selectedMonth+"/"+selectedYear;
            // set selected date into textview
            dob.setText(nDob);
        }
    };

    public void update(){
        requestUpdateData data = new requestUpdateData();
        data.setRequest_id(APIClient.REQUEST_ID);
        ProfileModel mdata = new ProfileModel();
        mdata.setId(user_id);
        mdata.setAuthor_name(uName);
        mdata.setAuthor_status(status.getText().toString());
        mdata.setAuthor_gender(uGender);
        mdata.setAuthor_contact(contact.getText().toString());
        data.setData(mdata);
        apiInterface apiService = APIClient.getClient().create(apiInterface.class);
        Call<responseUpdateData> call = apiService.updateProfile(data);
        call.enqueue(new Callback<responseUpdateData>() {
            @Override
            public void onResponse(Call<responseUpdateData> call, Response<responseUpdateData> response) {
                pg.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                if(response.body().getSuccess().equals("true")){
                    Toast.makeText(getApplicationContext(),"Data successfully updated",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),response.body().getError(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<responseUpdateData> call, Throwable t) {
                pg.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),t.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
