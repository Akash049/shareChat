package com.assignment.akashchandra.sharechatassignment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Akash Chandra on 11-04-2017.
 */

public class SaveImgFunction {

    public Uri image_uri;
    public SaveImgFunction(Bitmap bm,String id){
        Bitmap bitmap;
        OutputStream output;

        // Retrieve the image from the res folder
        bitmap = bm;

        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();

        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/Save Image/");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, "_"+id+".png");
        this.image_uri = Uri.fromFile(file);
        try {

            output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        }

        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Uri GetUri()
    {
        return image_uri;
    }
}

