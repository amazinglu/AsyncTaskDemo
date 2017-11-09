package com.example.amazinglu.myasynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * use Async task and OkHttp to implement the AsyncHttpClient
 * */

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();
        client = new OkHttpClient();
    }

    private void setUpUI() {
        imageView = (ImageView) findViewById(R.id.image);
        textView = (TextView) findViewById(R.id.text);

        Button loadText = (Button) findViewById(R.id.text_button);
        loadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Executes the task with the specified parameters,
                // allowing multiple tasks to run in parallel on a pool of threads managed by AsyncTask.
                AsyncTaskCompat.executeParallel(new LoadTextTask("http://www.jiuzhang.com/api/course/?format=json"));
            }
        });

        Button loadImage = (Button) findViewById(R.id.load_iamge);
        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTaskCompat.executeParallel(new LoadImageTask("http://www.jiuzhang.com/media/avatars/guojing2.png"));
            }
        });
    }

    private class LoadTextTask extends AsyncTask<Void, Void, String> {

        private String url;

        public LoadTextTask(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            // the request of OkHttp
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            try {
                // execute the request and get the response
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(s);
        }
    }

    private class LoadImageTask extends AsyncTask<Void, Void, byte[]> {

        private String imageUrl;

        public LoadImageTask(String url) {
            imageUrl = url;
        }

        @Override
        protected byte[] doInBackground(Void... voids) {
            Request request = new Request.Builder()
                    .url(imageUrl)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                return response.body().bytes();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);

            /**
             * the way to load image from bytes
             * */
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imageView.setImageBitmap(bitmap);
        }
    }
}
