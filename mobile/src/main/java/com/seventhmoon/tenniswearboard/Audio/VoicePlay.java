package com.seventhmoon.tenniswearboard.Audio;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.ParcelUuid;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class VoicePlay {
    private static final String TAG = VoicePlay.class.getName();

    public static File RootDirectory = new File("/");

    private static MediaPlayer mediaPlayer;


    public static boolean init_voice_folder() {
        Log.i(TAG, "init_voice_folder() --- start ---");
        boolean ret = true;
        //RootDirectory = null;

        //path = new File("/");
        //RootDirectory = new File("/");
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }

        File folder_tennis = new File(RootDirectory.getAbsolutePath() + "/.tennisVoice/");

        if(!folder_tennis.exists()) {
            Log.i(TAG, "folder not exist");
            ret = folder_tennis.mkdirs();
            if (!ret)
                Log.e(TAG, "init_folder_and_files: failed to mkdir hidden");
            try {
                ret = folder_tennis.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!ret)
                Log.e(TAG, "init_info: failed to create hidden file");
        }

        while(true) {
            if(folder_tennis.exists())
                break;
        }

        Log.i(TAG, "init_voice_folder() ---  end  ---");
        return ret;
    }

    public static boolean check_voice_file_exist(String fileName) {
        Log.i(TAG, "check_file_exist --- start ---");
        boolean ret = false;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //path = Environment.getExternalStorageDirectory();
            RootDirectory = Environment.getExternalStorageDirectory();
        }

        File file = new File(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);

        if(file.exists()) {
            Log.i(TAG, "file exist");
            ret = true;
        }

        return ret;
    }

    public static void audioPlayer(Context context, String fileName){
        Log.e(TAG, "audioPlayer start");
        //set up MediaPlayer

        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = new MediaPlayer();
            }

            mediaPlayer.setDataSource(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+fileName);

            mediaPlayer.prepare();

            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e(TAG, "audioPlayer end");
    }

    public static void syncVoiceToWear(String path, GoogleApiClient mGoogleApiClient, int count) {
        File src_path = new File(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+path);
        if (src_path.exists() && src_path.isDirectory()) {
            String children[] = src_path.list();
            for (String s : children) {
                File voice_file = new File(RootDirectory.getAbsolutePath() + "/.tennisVoice/"+path+"/"+s);
                Log.d(TAG, "file name: "+voice_file.getName());
                if (voice_file.exists()) {
                    Log.d(TAG, "send start");

                    //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
                    //final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    //bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
                    int size = (int) voice_file.length();
                    byte[] bytes = new byte[size];
                    try {
                        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(voice_file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();

                        Asset asset = Asset.createFromBytes(bytes);
                        PutDataMapRequest dataMap = PutDataMapRequest.create("/VOICE");
                        dataMap.getDataMap().putAsset("profileVoice", asset);
                        dataMap.getDataMap().putString("filename", voice_file.getName());
                        dataMap.getDataMap().putLong("datasize", size);
                        dataMap.getDataMap().putLong("count", count);

                        PutDataRequest request = dataMap.asPutDataRequest();
                        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                                .putDataItem(mGoogleApiClient, request);
                        count++;

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                    Log.d(TAG, "send end");
                }
            }
        }
    }
}
