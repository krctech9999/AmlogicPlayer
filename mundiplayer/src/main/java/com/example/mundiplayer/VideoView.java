package com.example.amlogicplayer;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Parcel;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;


import com.example.amplayer.player.comboMediaPlayer;
import com.example.amplayer.player.icMediaPlayer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays a video file.  The VideoView class
 * can load images from various sources (such as resources or content
 * providers), takes care of computing its measurement from the video so that
 * it can be used in any layout manager, and provides various display options
 * such as scaling and tinting.
 */
public class VideoView extends SurfaceView implements MediaPlayerControl {
    private String TAG = "VideoView";

    private Context mContext;

    // settable by the client
    private Uri         mUri;
    private int         mDuration;
    private String USER_AGENT = "Lavf53.302.9o0";
    private String X_USER_AGENT = "Model:MAG250;Link:Ethernet";
    // All the stuff we need for playing and showing a video
    private SurfaceHolder mSurfaceHolder = null;
    private comboMediaPlayer mMediaPlayer = null;
    private boolean     mIsPrepared;
    private boolean     mIsFullScreen = true;
    private boolean     mPaused = false;
    private int         mVideoWidth;
    private int         mVideoHeight;
    private int         mSurfaceWidth;
    private int         mSurfaceHeight;
    private MediaController mMediaController;
    private icMediaPlayer.OnCompletionListener mOnCompletionListener;
    private comboMediaPlayer.OnPreparedListener mOnPreparedListener;
    private int         mCurrentBufferPercentage;
    private icMediaPlayer.OnErrorListener mOnErrorListener;
    private boolean     mStartWhenPrepared;
    private int         mSeekWhenPrepared;
    private Map<String, String> my_headers = new HashMap<String, String>();

    private boolean hwAcceleration = false;

    private MySizeChangeLinstener mMyChangeLinstener;

    private reportVideoEvent mEventReportLinstener = null;

    public int getVideoWidth(){
        return mVideoWidth;
    }

    public int getVideoHeight(){
        return mVideoHeight;
    }

    public void setVideoScale(int width , int height, int x_offset, int y_offset){
        LayoutParams lp = (LayoutParams) getLayoutParams();
        lp.height = height;
        lp.width = width;
        lp.gravity = Gravity.LEFT|Gravity.TOP;
        lp.leftMargin = x_offset;
        lp.topMargin = y_offset;
        setLayoutParams(lp);
        getHolder().setFixedSize(width, height);
        mIsFullScreen = false;
    }

    public void setVideoFull(){
        LayoutParams lp = (LayoutParams) getLayoutParams();
        lp.height = LayoutParams.FILL_PARENT;
        lp.width = LayoutParams.FILL_PARENT;
        lp.gravity = Gravity.CENTER;
        lp.leftMargin = 0;
        lp.bottomMargin = 0;
        setLayoutParams(lp);
        mIsFullScreen = true;
    }

    public boolean isFullScreen(){
        return this.mIsFullScreen;
    }

    public boolean isPaused(){ return this.mPaused;}

    public interface reportVideoEvent{
        public void sendEvent(int event);
    }

    public void setReportEventLinstener(reportVideoEvent l){
        mEventReportLinstener = l;
    }

    public interface MySizeChangeLinstener{
        public void doMyThings();
    }

    public void setMySizeChangeLinstener(MySizeChangeLinstener l){
        mMyChangeLinstener = l;
    }

    public VideoView(Context context) {
        super(context);
        mContext = context;
        initVideoView();
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
        initVideoView();
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        initVideoView();
    }

    public void setHwAcceleration(boolean flag)
    {
        hwAcceleration = flag;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.i("@@@@", "onMeasure");
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);

        setMeasuredDimension(width,height);
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                /* Parent says we can be as big as we want. Just don't be larger
                 * than max size imposed on ourselves.
                 */
                result = desiredSize;
                break;

            case MeasureSpec.AT_MOST:
                /* Parent says we can be as big as we want, up to specSize.
                 * Don't be larger than specSize, and don't be larger than
                 * the max size imposed on ourselves.
                 */
                result = Math.min(desiredSize, specSize);
                break;

            case MeasureSpec.EXACTLY:
                // No choice. Do what we are told.
                result = specSize;
                break;
        }
        return result;
    }

    private void initVideoView() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        getHolder().addCallback(mSHCallback);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        my_headers.clear();

        //my_headers.put("User-Agent", USER_AGENT);
        my_headers.put("X-User-Agent", X_USER_AGENT);
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        Log.v(TAG, "uri="+uri);
        mUri = uri;
        mStartWhenPrepared = false;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setVideoURI(Uri uri,Map<String, String> headers) {
        Log.v(TAG, "uri="+uri);
        mUri = uri;
        mStartWhenPrepared = false;
        mSeekWhenPrepared = 0;

        my_headers.clear();
        my_headers.putAll(headers);

        openVideo();
        requestLayout();
        invalidate();
    }


    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;

            clearSurfaceView();
            mPaused = false;
            Log.d(TAG, "Video is stopped");
        }
    }

    private void clearSurfaceView(){

        Canvas c=null;
        try{
            c=mSurfaceHolder.lockCanvas(null);
            c.drawColor(Color.BLACK);
        }finally{
            if(c!=null)
                mSurfaceHolder.unlockCanvasAndPost(c);
        }
    }

    public void setVolume(float volume)
    {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    private void setVideoBufferSize()
    {
        Parcel data = Parcel.obtain();
        data.writeInt(1);   //when the buffered data is less than 1seconds, begin to buffer
        //mMediaPlayer.setParameter(1252, data);

        data.writeInt(8);
        //mMediaPlayer.setParameter(1253, data); //when the buffer time is more than 3seconds, begin to play
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        // Tell the music playback service to pause
        // TODO: these constants need to be published somewhere in the framework.
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        mContext.sendBroadcast(i);

        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        try {
            mMediaPlayer = new comboMediaPlayer(hwAcceleration);
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mIsPrepared = false;
            mPaused = false;
            Log.v(TAG, "reset duration to -1 in openVideo");
            mDuration = -1;

            setVideoBufferSize();

            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mOnInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, mUri, my_headers);
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            attachMediaController();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            return;
        }
    }

    public void setMediaController(MediaController controller) {
        if (mMediaController != null) {
            mMediaController.hide();
        }
        mMediaController = controller;
        attachMediaController();
    }

    public int getNetworkSpeed()
    {
        if (mMediaPlayer != null) {
            //Parcel data = mMediaPlayer.getParcelParameter(1205);

            //if(data!=null)
            //return data.readInt();
        }
        return -1;
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null) {
            mMediaController.setMediaPlayer(this);
            View anchorView = this.getParent() instanceof View ?
                    (View)this.getParent() : this;
            mMediaController.setAnchorView(anchorView);
            mMediaController.setEnabled(mIsPrepared);
        }
    }

    comboMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new comboMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(icMediaPlayer mp, int width, int height) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();

                    if(mMyChangeLinstener!=null){
                        mMyChangeLinstener.doMyThings();
                    }

                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                    }
                }

                @Override
                public void onVideoSizeChanged(icMediaPlayer mp, int width,
                                               int height, int sar_num, int sar_den) {
                    // TODO Auto-generated method stub

                }
            };

    comboMediaPlayer.OnPreparedListener mPreparedListener = new comboMediaPlayer.OnPreparedListener() {
        public void onPrepared(icMediaPlayer mp) {
            // briefly show the mediacontroller
            mIsPrepared = true;
            if(mEventReportLinstener!=null){
                mEventReportLinstener.sendEvent(4);
            }

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (mMediaController != null) {
                mMediaController.setEnabled(true);
            }
            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                //Log.i("@@@@", "video size: " + mVideoWidth +"/"+ mVideoHeight);
                getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                    // We didn't actually change the size (it was already at the size
                    // we need), so we won't get a "surface changed" callback, so
                    // start the video here instead of in the callback.
                    if (mSeekWhenPrepared != 0) {
                        mMediaPlayer.seekTo(mSeekWhenPrepared);
                        mSeekWhenPrepared = 0;
                    }
                    if (mStartWhenPrepared) {
                        mMediaPlayer.start();
                        setVideoBufferSize();
                        mStartWhenPrepared = false;
                        if (mMediaController != null) {
                            mMediaController.show();
                        }
                    } else if (!isPlaying() &&
                            (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {
                        if (mMediaController != null) {
                            // Show the media controls when we're paused into a video and make 'em stick.
                            mMediaController.show(0);
                        }
                    }
                }
            } else {
                // We don't know the video size yet, but should start anyway.
                // The video size might be reported to us later.
                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                if (mStartWhenPrepared) {
                    mMediaPlayer.start();
                    setVideoBufferSize();
                    mStartWhenPrepared = false;
                }
            }
        }
    };

    private comboMediaPlayer.OnCompletionListener mCompletionListener =
            new comboMediaPlayer.OnCompletionListener() {
                public void onCompletion(icMediaPlayer mp) {
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }

                    if(mEventReportLinstener!=null){
                        mEventReportLinstener.sendEvent(1);
                    }
                }
            };

    private comboMediaPlayer.OnErrorListener mErrorListener =
            new comboMediaPlayer.OnErrorListener() {
                public boolean onError(icMediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    if (mMediaController != null) {
                        mMediaController.hide();
                    }

                    if(mEventReportLinstener!=null){
                        mEventReportLinstener.sendEvent(5);
                    }

            /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }

            /* Otherwise, pop up an error dialog so the user knows that
             * something bad has happened. Only try and pop up the dialog
             * if we're attached to a window. When we're going away and no
             * longer have a window, don't bother showing the user an error.
             */
                    if (getWindowToken() != null) {
                        Resources r = mContext.getResources();
                        int messageId;
                    }
                    return true;
                }
            };

    private comboMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new comboMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(icMediaPlayer mp, int percent) {
                    mCurrentBufferPercentage = percent;
                }
            };

    private comboMediaPlayer.OnInfoListener  mOnInfoListener  =
            new comboMediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(icMediaPlayer mp, int what, int extra) {
                    // TODO Auto-generated method stub
                    Log.v(TAG, "OnInfoListener="+what);
                    if(what == 701)
                    {

                    }
                    return true;
                }
            };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(comboMediaPlayer.OnPreparedListener l)
    {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(icMediaPlayer.OnCompletionListener l)
    {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(icMediaPlayer.OnErrorListener l)
    {
        mOnErrorListener = l;
    }

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback()
    {
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int w, int h)
        {
            mSurfaceWidth = w;
            mSurfaceHeight = h;
            if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w && mVideoHeight == h) {
                if (mSeekWhenPrepared != 0) {
                    mMediaPlayer.seekTo(mSeekWhenPrepared);
                    mSeekWhenPrepared = 0;
                }
                mMediaPlayer.start();
                setVideoBufferSize();
                if (mMediaController != null) {
                    mMediaController.show();
                }
            }
        }

        public void surfaceCreated(SurfaceHolder holder)
        {
            mSurfaceHolder = holder;
            openVideo();
        }

        public void surfaceDestroyed(SurfaceHolder holder)
        {
            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            if (mMediaController != null) mMediaController.hide();
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent ev) {
        if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (mIsPrepared &&
                keyCode != KeyEvent.KEYCODE_BACK &&
                keyCode != KeyEvent.KEYCODE_VOLUME_UP &&
                keyCode != KeyEvent.KEYCODE_VOLUME_DOWN &&
                keyCode != KeyEvent.KEYCODE_MENU &&
                keyCode != KeyEvent.KEYCODE_CALL &&
                keyCode != KeyEvent.KEYCODE_ENDCALL &&
                mMediaPlayer != null &&
                mMediaController != null) {
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK ||
                    keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                if (mMediaPlayer.isPlaying()) {
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP
                    && mMediaPlayer.isPlaying()) {
                pause();
                mMediaController.show();
            } else {
                toggleMediaControlsVisiblity();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (mMediaController.isShowing()) {
            mMediaController.hide();
        } else {
            mMediaController.show();
        }
    }

    public void start() {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.start();
            setVideoBufferSize();
            mStartWhenPrepared = false;
            mPaused = false;
        } else {
            mStartWhenPrepared = true;
        }
    }

    public void pause() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mPaused = true;
            }
        }
        mStartWhenPrepared = false;
    }

    public void resume() {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.start();
            mPaused = false;
        }
        mStartWhenPrepared = false;
    }

    public int getDuration() {
        if (mMediaPlayer != null && mIsPrepared) {
            if (mDuration > 0) {
                return mDuration;
            }
            mDuration = (int) mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null && mIsPrepared) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        if (mMediaPlayer != null && mIsPrepared) {
            mMediaPlayer.seekTo(msec);
        } else {
            mSeekWhenPrepared = msec;
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null && mIsPrepared) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        // TODO Auto-generated method stub
        return 0;
    }

}
