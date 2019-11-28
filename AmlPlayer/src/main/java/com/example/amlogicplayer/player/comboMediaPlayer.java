/*
 * Copyright (C) 2013-2014 Zhang Rui <bbcallen@gmail.com>
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

package com.example.amlogicplayer.player;

import java.io.IOException;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.example.amlogicplayer.tv.IMediaPlayer;
import com.example.amlogicplayer.tv.IjkMediaPlayer;

/**
 * @author bbcallen
 * 
 *         Common IMediaPlayer implement
 */
public class comboMediaPlayer implements icMediaPlayer {
    private OnPreparedListener mOnPreparedListener;
    private OnCompletionListener mOnCompletionListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
    private OnErrorListener mOnErrorListener;
    private OnInfoListener mOnInfoListener;
    private boolean hwAcceleration = true;
    private IMediaPlayer mswMediaPlayer = null;
    private MediaPlayer mhwMediaPlayer = null;
    private icMediaPlayer mInstance = null;
    
    public comboMediaPlayer(boolean hwS) {
    	hwAcceleration = hwS;
    	mInstance = this;
    	
    	if(hwAcceleration)
    	{
    		mhwMediaPlayer = new MediaPlayer();
    		if(mswMediaPlayer!=null)
    		{
    			mswMediaPlayer.stop();
    			mswMediaPlayer = null;
    		}
    	}
    	else
    	{
    		mswMediaPlayer = new IjkMediaPlayer();
    		
    		if(mhwMediaPlayer!=null)
    		{
    			mhwMediaPlayer.stop();
    			mhwMediaPlayer = null;
    		}
    	}
    }

    public final void setOnPreparedListener(OnPreparedListener listener) {
        mOnPreparedListener = listener;
        
    	if(hwAcceleration)
    	{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
					
					@Override
					public void onPrepared(MediaPlayer arg0) {
						if(mOnPreparedListener!=null)
						{
							mOnPreparedListener.onPrepared(mInstance);
						}
					}
				});
			}
    	}
    	else
    	{
    		if(mswMediaPlayer!=null)
			{
    			mswMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
					
					@Override
					public void onPrepared(IMediaPlayer mp) {
						if(mOnPreparedListener!=null)
						{
							mOnPreparedListener.onPrepared(mInstance);
						}
					}
				});
			}
    	}
    }

    public final void setOnCompletionListener(OnCompletionListener listener) {
        mOnCompletionListener = listener;
    	if(hwAcceleration)
    	{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer arg0) {
						if(mOnCompletionListener!=null)
						{
							mOnCompletionListener.onCompletion(mInstance);
						}
					}
					
				});
			}
    	}
    	else
    	{
    		if(mswMediaPlayer!=null)
			{
    			mswMediaPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
					
					@Override
					public void onCompletion(IMediaPlayer mp) {
						if(mOnCompletionListener!=null)
						{
							mOnCompletionListener.onCompletion(mInstance);
						}
					}
				});
			}
    	}
    }

    public final void setOnBufferingUpdateListener(
            OnBufferingUpdateListener listener) {
        mOnBufferingUpdateListener = listener;
        
    	if(hwAcceleration)
    	{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
					@Override
					public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
						if(mOnBufferingUpdateListener!=null)
						{
							mOnBufferingUpdateListener.onBufferingUpdate(mInstance, arg1);
						}
					}
					
				});
			}
    	}
    	else
    	{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
					
					@Override
					public void onBufferingUpdate(IMediaPlayer mp, int percent) {
						if(mOnBufferingUpdateListener!=null)
						{
							mOnBufferingUpdateListener.onBufferingUpdate(mInstance, percent);
						}
					}
				});
			}
    	}
    }

    public final void setOnSeekCompleteListener(OnSeekCompleteListener listener)
	{
    	mOnSeekCompleteListener = listener;
    	if(hwAcceleration)
    	{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
	
					@Override
					public void onSeekComplete(MediaPlayer arg0) {
						if(mOnSeekCompleteListener!=null)
						{
							mOnSeekCompleteListener.onSeekComplete(mInstance);
						}
					}
					
				});
			}
		}
    	else
    	{
    		if(mswMediaPlayer!=null)
			{
    			mswMediaPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
					
					@Override
					public void onSeekComplete(IMediaPlayer mp) {
						if(mOnSeekCompleteListener!=null)
						{
							mOnSeekCompleteListener.onSeekComplete(mInstance);
						}
					}
				});
			}
    	}
	}

    public final void setOnVideoSizeChangedListener(
            OnVideoSizeChangedListener listener) {
        mOnVideoSizeChangedListener = listener;

    	if(hwAcceleration)
    	{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
					
					@Override
					public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2) {
						if(mOnVideoSizeChangedListener!=null)
						{
							mOnVideoSizeChangedListener.onVideoSizeChanged(mInstance, arg1, arg2, 0, 0);
						}
					}
				});
			}
	}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {

					@Override
					public void onVideoSizeChanged(IMediaPlayer mp, int width,
							int height, int sar_num, int sar_den) {
						if(mOnVideoSizeChangedListener!=null)
						{
							mOnVideoSizeChangedListener.onVideoSizeChanged(mInstance, width, height, sar_num, sar_den);
						}
					}
				});
			}
		}
    }

    public final void setOnErrorListener(OnErrorListener listener) {
        mOnErrorListener = listener;
    	if(hwAcceleration)
    	{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {					
					@Override
					public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
						if(mOnErrorListener!=null)
						{
							mOnErrorListener.onError(mInstance, arg1, arg2);
						}
						return false;
					}
				});
			}
    	}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
					@Override
					public boolean onError(IMediaPlayer mp, int what, int extra) {
						if(mOnErrorListener!=null)
						{
							mOnErrorListener.onError(mInstance, what, extra);
						}
						return false;
					}
				});
			}
		}
    }

    public final void setOnInfoListener(OnInfoListener listener) {
        mOnInfoListener = listener;
    	if(hwAcceleration)
    	{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {					

					@Override
					public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
						if(mOnInfoListener!=null)
						{
							mOnInfoListener.onInfo(mInstance, arg1, arg2);
						}
						return false;
					}
				});
			}
    	}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {

					@Override
					public boolean onInfo(IMediaPlayer mp, int what, int extra) {
						if(mOnInfoListener!=null)
						{
							mOnInfoListener.onInfo(mInstance, what, extra);
						}
						return false;
					}
				});
			}
		}
    }

    public void resetListeners() {
        mOnPreparedListener = null;
        mOnBufferingUpdateListener = null;
        mOnCompletionListener = null;
        mOnSeekCompleteListener = null;
        mOnVideoSizeChangedListener = null;
        mOnErrorListener = null;
        mOnInfoListener = null;
    }

    protected final void notifyOnPrepared() {
        if (mOnPreparedListener != null)
            mOnPreparedListener.onPrepared(this);
    }

    protected final void notifyOnCompletion() {
        if (mOnCompletionListener != null)
            mOnCompletionListener.onCompletion(this);
    }

    protected final void notifyOnBufferingUpdate(int percent) {
        if (mOnBufferingUpdateListener != null)
            mOnBufferingUpdateListener.onBufferingUpdate(this, percent);
    }

    protected final void notifyOnSeekComplete() {
        if (mOnSeekCompleteListener != null)
            mOnSeekCompleteListener.onSeekComplete(this);
    }

    protected final void notifyOnVideoSizeChanged(int width, int height,
            int sarNum, int sarDen) {
        if (mOnVideoSizeChangedListener != null)
            mOnVideoSizeChangedListener.onVideoSizeChanged(this, width, height,
                    sarNum, sarDen);
    }

    protected final boolean notifyOnError(int what, int extra) {
        if (mOnErrorListener != null)
            return mOnErrorListener.onError(this, what, extra);
        return false;
    }

    protected final boolean notifyOnInfo(int what, int extra) {
        if (mOnInfoListener != null)
            return mOnInfoListener.onInfo(this, what, extra);
        return false;
    }

	@Override
	public void setDisplay(SurfaceHolder sh) {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setDisplay(sh);
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setDisplay(sh);
			}
		}
	}

	@Override
	public void setDataSource(Context context, Uri uri) throws IOException,
			IllegalArgumentException, SecurityException, IllegalStateException {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setDataSource(context, uri);
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setDataSource(uri.toString());
			}
		}
	}

	@Override
	public void setDataSource(Context context, Uri uri, Map<String, String> header)
			throws IOException, IllegalArgumentException, SecurityException,
			IllegalStateException {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setDataSource(context, uri, header);
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setDataSource(context, uri, header);
			}
		}
	}


	@Override
	public void prepareAsync() throws IllegalStateException {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.prepareAsync();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.prepareAsync();
			}
		}
	}

	@Override
	public void start() throws IllegalStateException {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.start();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.start();
			}
		}
	}

	@Override
	public void stop() throws IllegalStateException {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.stop();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.stop();
			}
		}
	}

	@Override
	public void pause() throws IllegalStateException {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.pause();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.pause();
			}
		}
	}

	@Override
	public void setScreenOnWhilePlaying(boolean screenOn) {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setScreenOnWhilePlaying(screenOn);
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setScreenOnWhilePlaying(screenOn);
			}
		}
	}

	@Override
	public int getVideoWidth() {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				return mhwMediaPlayer.getVideoWidth();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				return mswMediaPlayer.getVideoWidth();
			}
		}
		return -1;
	}

	@Override
	public int getVideoHeight() {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				return mhwMediaPlayer.getVideoHeight();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				return mswMediaPlayer.getVideoHeight();
			}
		}
		return -1;
	}

	@Override
	public boolean isPlaying() {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				return mhwMediaPlayer.isPlaying();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				return mswMediaPlayer.isPlaying();
			}
		}
		return false;
	}

	@Override
	public void seekTo(long msec) throws IllegalStateException {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.seekTo((int) msec);
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.seekTo(msec);
			}
		}
	}

	@Override
	public long getCurrentPosition() {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				return mhwMediaPlayer.getCurrentPosition();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				return mswMediaPlayer.getCurrentPosition();
			}
		}
		return 0;
	}

	@Override
	public long getDuration() {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				return mhwMediaPlayer.getDuration();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				return mswMediaPlayer.getDuration();
			}
		}
		return 0;
	}

	@Override
	public void release() {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.release();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.release();
			}
		}
	}

	@Override
	public void reset() {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.reset();
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.reset();
			}
		}
	}

	@Override
	public void setVolume(float leftVolume, float rightVolume) {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setVolume(leftVolume, rightVolume);
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setVolume(leftVolume, rightVolume);
			}
		}
	}

	@Override
	public void setLogEnabled(boolean enable) {
		if(!hwAcceleration)
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setLogEnabled(enable);
			}
		}
	}

	@Override
	public boolean isPlayable() {
		if(hwAcceleration)
		{
			return true;
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				return mswMediaPlayer.isPlayable();
			}
		}
		return false;
	}

	@Override
	public void setAudioStreamType(int streamtype) {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setAudioStreamType(streamtype);
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setAudioStreamType(streamtype);
			}
		}
	}

	@Override
	public void setKeepInBackground(boolean keepInBackground) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getVideoSarNum() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getVideoSarDen() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Deprecated
	public void setWakeMode(Context context, int mode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@TargetApi(14)
	public void setSurface(Surface surface) {
		if(hwAcceleration)
		{
			if(mhwMediaPlayer!=null)
			{
				mhwMediaPlayer.setSurface(surface);
			}
		}
		else
		{
			if(mswMediaPlayer!=null)
			{
				mswMediaPlayer.setSurface(surface);
			}
		}
	}
}
