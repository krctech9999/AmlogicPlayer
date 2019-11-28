/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.amlogicplayer;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amlogicplayer.player.comboMediaPlayer;
import com.example.amlogicplayer.player.icMediaPlayer;


/*
 * MainActivity class that loads MainFragment
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */

    private VideoView vv;
    private static String TAG= "MainActivity";

    //private static String newUrl="http://192.168.30.4:8080/udp/224.2.2.2:3000/";
    //private static String newUrl="https://api.happytv.com.tw/upload/mp3/CK016818.mp3";
    private static String newUrl="http://107.150.63.130/Hotel_Transylvania_2_2015/Hotel.Transylvania.2.2015.1080p.BluRay.x264.%5BYTS.AG%5D.mp4";

    //private static String newUrl="http://ewnplayer.akamaized.net/live/premium/espn/4200.m3u8";

    //private static String newUrl="http://192.168.3.205/test/warcraft.mkv";

    //private static String newUrl="http://192.168.30.100:9090/ESPN/0.m3u8";
    //private static String newUrl="http://cellontv.vo.llnwd.net/eyewatch/premium/aetv/playlist.m3u8";
    //private static String newUrl="http://208.92.220.118:8000/live/premium/aetv/playlist.m3u8";

    //private static String newUrl="http://192.168.100.200/fx.ts";

    //private static String newUrl="http://192.168.100.200/shrinkage.mp4";
    //private static String newUrl="http://192.168.3.200/ts/1 ";
    //private static String newUrl="http://192.168.100.200:8888/vod/shrinkage.mp4";
    //private static String newUrl="http://tiki.myomnibox.com/hls/kan-mo-oaenc01-master-ion.m3u8";

    //private static String newUrl="http://tv-tikilive-live.hls.adaptive.level3.net/premium-01-3/fx/track_1_3000_playlist.m3u8?op_id=4&userId=1&channelId=24912&stime=1473885713&etime=1476477713&token=03373cf09a4719fef4f36";

    //private static String newUrl="http://tv-tikilive-live.hls.adaptive.level3.net/premium-01-3/fx/playlist.m3u8?op_id=4&userId=1&channelId=24912&stime=1472715510&etime=1475307510&token=0ece0395b8edd1ac7ad23";

    //private static String newUrl="http://tv-tikilive-live.hls.adaptive.level3.net/show_demotiki/651/amlst:mainstream/playlist.m3u8?op_id=4&userId=1&channelId=25429&stime=1472737341&etime=1475329341&token=0958c18b39a1731a4f716";

    //@TargetApi(Build.VERSION_CODES.CUPCAKE)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vv = (VideoView) findViewById(R.id.tutView);

//        novaToken.getToken(1487052232+1800,"192.168.3.100");

        URLUtil task = new URLUtil();
        task.execute(newUrl);

        Log.d(TAG, "This package name="+getPackageName());
        /*
        Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, vv.getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        sendBroadcast(i);*/

        /*

        Intent i = new Intent();
        i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, vv.getAudioSessionId());
        i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
        i.setComponent(new ComponentName(
                new String("com.android.musicfx"), new String("com.android.musicfx.ActivityMusic")));
        startActivity(i);*/
    }

    //@TargetApi(Build.VERSION_CODES.CUPCAKE)
    public class URLUtil extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onCancelled() {
            if (vv != null && vv.isPlaying()) vv.stopPlayback();
        }

        @Override
        protected void onPostExecute(String result) {
            if (vv == null || result == null)
                return;
            if (vv.isPlaying()) vv.stopPlayback();

            Uri localUrl = Uri.parse(result);
            vv.setVideoURI(localUrl);
            vv.setOnPreparedListener(new comboMediaPlayer.OnPreparedListener() {

                public void onPrepared(icMediaPlayer arg0) {
                    Log.d(TAG, "Progress is " + vv.getBufferPercentage());
                    //vv.requestFocus();
                    vv.start();
                }
            });

            vv.setOnErrorListener(new comboMediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(icMediaPlayer mp, int what, int extra) {

                    Log.d(TAG, "MediaPlay error");

                    if (vv.isPlaying()) vv.stopPlayback();
                    return false;
                }
            });
        }

        @Override
        protected void onPreExecute() {
            if (vv.isPlaying()) vv.stopPlayback();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
