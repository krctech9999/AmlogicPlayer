package com.example.mundiplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.amplayer.player.comboMediaPlayer;
import com.example.amplayer.player.icMediaPlayer;

import java.io.IOException;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "MainActivity";

    private comboMediaPlayer mPlayer = null;
    private EditText mtext;
    private Button mButton;
    private String mURL = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPlayer = new comboMediaPlayer(true);
        mtext = findViewById(R.id.url_input);
        mButton = findViewById(R.id.commit_button);
        mButton.setOnClickListener(new listener);
        return;
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mURL = mtext.getText().toString();
        }
    };

    private void openVideo() {
        mPlayer = new comboMediaPlayer(false);
        //mPlayer.setDataSource(this, mURL);
        mPlayer.prepareAsync();
        mPlayer.start();
    }

}
