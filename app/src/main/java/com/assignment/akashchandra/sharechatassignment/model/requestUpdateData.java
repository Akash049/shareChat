package com.assignment.akashchandra.sharechatassignment.model;

/**
 * Created by Akash Chandra on 10-04-2017.
 */

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

public class requestUpdateData implements Serializable {
    @SerializedName("request_id")
    private String request_id;

    @SerializedName("data")
    private ProfileModel data;


    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public ProfileModel getData() {
        return data;
    }

    public void setData(ProfileModel data) {
        this.data = data;
    }
}
