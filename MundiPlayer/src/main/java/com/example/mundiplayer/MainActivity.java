package com.example.mundiplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.amplayer.player.comboMediaPlayer;
import com.example.amplayer.player.icMediaPlayer;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";
    private EditText mtext;
    private Button mbutton;
    private VideoView mvideoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtext = findViewById(R.id.uri_text);
        mbutton = findViewById(R.id.ok_button);
        mbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = mtext.getText().toString();
                mvideoview = findViewById(R.id.video_view);
                URLUtil task = new URLUtil();
                task.execute(uri);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public class URLUtil extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onCancelled() {
            if (mvideoview != null && mvideoview.isPlaying()) mvideoview.stopPlayback();
        }

        @Override
        protected void onPostExecute(String result) {
            if (mvideoview == null || result == null)
                return;
            if (mvideoview.isPlaying()) mvideoview.stopPlayback();

            Uri localUrl = Uri.parse(result);
            mvideoview.setVideoURI(localUrl);
            mvideoview.setOnPreparedListener(new comboMediaPlayer.OnPreparedListener() {

                public void onPrepared(icMediaPlayer arg0) {
                    Log.d(TAG, "Progress is " + mvideoview.getBufferPercentage());
                    //vv.requestFocus();
                    mvideoview.start();
                }
            });

            mvideoview.setOnErrorListener(new comboMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(icMediaPlayer mp, int what, int extra) {

                    Log.d(TAG, "MediaPlay error");

                    if (mvideoview.isPlaying()) mvideoview.stopPlayback();
                    return false;
                }
            });
        }

        @Override
        protected void onPreExecute() {
            if (mvideoview.isPlaying()) mvideoview.stopPlayback();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
