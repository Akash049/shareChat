package com.assignment.akashchandra.sharechatassignment.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.assignment.akashchandra.sharechatassignment.SaveImgFunction;
import com.assignment.akashchandra.sharechatassignment.databases.DataBaseHandler;
import com.assignment.akashchandra.sharechatassignment.model.DataModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash Chandra on 11-04-2017.
 */

public class DownloadImage extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // code to execute when the service is first created
        super.onCreate();
        Log.i("MyService", "Service Started.");
        new DownloadImagesTask().execute("");
    }



    public class DownloadImagesTask extends AsyncTask<String, Void, Bitmap> {
        DataBaseHandler db = new DataBaseHandler(getApplicationContext());
        List<DataModel> list = db.getData();
        int size = list.size();
        int iterator = 0;
        String id;
        String dataURL;
        @Override
        protected void onPreExecute() {
        }
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap img = null;
            while(iterator<size)
            {
                //If Image has already been downloaded, then not to do it again

                if(list.get(iterator).getURI().equals(""))
                    continue;
                id = list.get(iterator).getId();
                if (list.get(iterator).getType().equals("profile")) {
                    dataURL = list.get(iterator).getProfile_url();
                } else {
                    dataURL = list.get(iterator).getUrl();
                }
                img = download_Image(dataURL);
            }
            return img;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            SaveImgFunction obj= new SaveImgFunction(result,id);
            Uri imageUri = obj.GetUri();
            try{
                db.UpdateURI(Integer.valueOf(id),imageUri.toString());
            }catch (Exception e){

            }

        }
        private Bitmap download_Image(String url) {
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
        }
    }



}
