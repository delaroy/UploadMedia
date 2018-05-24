package com.delaroystudios.uploadmedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.delaroystudios.uploadmedia.networking.ApiConfig;
import com.delaroystudios.uploadmedia.networking.AppConfig;
import com.delaroystudios.uploadmedia.networking.ServerResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by delaroy on 5/5/18.
 */

public class VideoActivity extends AppCompatActivity {
    Button button;
    public static final int REQUEST_PICK_VIDEO = 3;
    public ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_layout);

        button = (Button) findViewById(R.id.pickVideo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickVideoIntent.setType("video/*");
                startActivityForResult(pickVideoIntent, REQUEST_PICK_VIDEO);
            }
        });

        initDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_PICK_VIDEO) {
                if (data != null) {
                    Toast.makeText(this, "Video content URI: " + data.getData(),
                            Toast.LENGTH_LONG).show();
                    Uri video = data.getData();
                    uploadFile(video.getPath());

                }
            }
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, "Sorry, there was an error!", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFile(String postPath) {
        if (postPath == null || postPath.equals("")) {
            Toast.makeText(this, "please select an image ", Toast.LENGTH_LONG).show();
            return;
        } else {
            showpDialog();

            // Map is used to multipart the file using okhttp3.RequestBody
            Map<String, RequestBody> map = new HashMap<>();
            File file = new File(postPath);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
            map.put("file\"; filename=\"" + file.getName() + "\"", requestBody);
            ApiConfig getResponse = AppConfig.getRetrofit().create(ApiConfig.class);
            Call<ServerResponse> call = getResponse.upload("token", map);
            call.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            showpDialog();
                            ServerResponse serverResponse = response.body();
                            Toast.makeText(getApplicationContext(), serverResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }else {
                        Toast.makeText(getApplicationContext(), "problem uploading image", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    hidepDialog();
                    Log.v("Response gotten is", t.getMessage());
                    Toast.makeText(getApplicationContext(), "problem uploading image " + t.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    protected void initDialog() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(true);
    }


    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }

}
