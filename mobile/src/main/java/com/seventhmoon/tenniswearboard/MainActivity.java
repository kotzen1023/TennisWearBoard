package com.seventhmoon.tenniswearboard;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
//import android.support.v4.app.ActivityCompat
import androidx.core.app.ActivityCompat;

//import android.support.v4.content.ContextCompat
import androidx.core.content.ContextCompat;
//import android.support.v7.app.ActionBar;
import androidx.appcompat.app.ActionBar;
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import com.seventhmoon.tenniswearboard.Data.Constants;

import com.seventhmoon.tenniswearboard.Data.State;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.seventhmoon.tenniswearboard.Audio.VoicePlay.init_voice_folder;

import static com.seventhmoon.tenniswearboard.Data.Constants.ACTION.GET_SYNC_COMMAND;

import static com.seventhmoon.tenniswearboard.Data.FileOperation.init_folder_and_files;

import static com.seventhmoon.tenniswearboard.Data.InitData.mGoogleApiClient;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    //private SensorManager mSensorManager;
    private static final int REQUEST_ENABLE_BT = 3;
    //private Context context;
    //BluetoothAdapter mBluetoothAdapter;
    //BroadcastReceiver mReceiver;
    //private boolean isRegister;
    private static MenuItem voiceItem;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean isRegister;
    private static int count = 0;

    private TextView step_count;
    private TextView step_total_count;

    private TextView set1_up;
    private TextView set1_down;
    private TextView set1_tibreak_up;
    private TextView set1_tibreak_down;

    private TextView set2_up;
    private TextView set2_down;
    private TextView set2_tibreak_up;
    private TextView set2_tibreak_down;

    private TextView set3_up;
    private TextView set3_down;
    private TextView set3_tibreak_up;
    private TextView set3_tibreak_down;

    private TextView set4_up;
    private TextView set4_down;
    private TextView set4_tibreak_up;
    private TextView set4_tibreak_down;

    private TextView set5_up;
    private TextView set5_down;
    private TextView set5_tibreak_up;
    private TextView set5_tibreak_down;

    private TextView pointUp;
    private TextView pointDown;
    private ImageView imgServeUp;
    private ImageView imgServeDown;

    private ImageView imgWinCheckUp;
    private ImageView imgWinCheckDown;

    public static Deque<State> stack = new ArrayDeque<>();

    //private static boolean is_second_serve = false;
    //private static boolean is_break_point = false;




    private static long startTime = 0;
    private static long endTime = 0;
    private static long spentTime = 0;
    private static Handler handler;
    private static long time_use = 0;

    private static String set;
    private static String games;
    private static String tiebreak;
    private static String deuce;
    private static String serve;

    private static String step="";
    private static String prev_step="";
    private static long totalCount = 0;

    private static boolean is_fisnish = false;

    LinearLayout wearLayout;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";

    private static boolean am_I_Tiebreak_First_Serve = false;

    private static boolean is_In_SuperTiebreak = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        String total_count = pref.getString("TOTAL_STEP_COUNT", "0");
        totalCount = Long.valueOf(total_count);

        Log.i(TAG, "onCreate");

        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }

        //is_second_serve = false;
        //is_break_point = false;
        //is_unforced_error = false;
        //is_forehand_winner = false;
        //is_backhand_winner = false;
        //is_forehand_volley = false;
        //is_backhand_volley = false;


        handler = new Handler();

        //startTime = System.currentTimeMillis();

        //handler.removeCallbacks(updateTimer);
        //handler.postDelayed(updateTimer, 1000);

        Intent intent = getIntent();

        wearLayout = (LinearLayout) findViewById(R.id.activity_main);

        step_count = (TextView) findViewById(R.id.stepCount);
        step_total_count = (TextView) findViewById(R.id.totalCount);

        set1_up = (TextView) findViewById(R.id.set1_up);
        set1_down = (TextView) findViewById(R.id.set1_down);
        set1_tibreak_up = (TextView) findViewById(R.id.set1_tibreak_up);
        set1_tibreak_down = (TextView) findViewById(R.id.set1_tibreak_down);

        set2_up = (TextView) findViewById(R.id.set2_up);
        set2_down = (TextView) findViewById(R.id.set2_down);
        set2_tibreak_up = (TextView) findViewById(R.id.set2_tibreak_up);
        set2_tibreak_down = (TextView) findViewById(R.id.set2_tibreak_down);

        set3_up = (TextView) findViewById(R.id.set3_up);
        set3_down = (TextView) findViewById(R.id.set3_down);
        set3_tibreak_up = (TextView) findViewById(R.id.set3_tibreak_up);
        set3_tibreak_down = (TextView) findViewById(R.id.set3_tibreak_down);

        set4_up = (TextView) findViewById(R.id.set4_up);
        set4_down = (TextView) findViewById(R.id.set4_down);
        set4_tibreak_up = (TextView) findViewById(R.id.set4_tibreak_up);
        set4_tibreak_down = (TextView) findViewById(R.id.set4_tibreak_down);

        set5_up = (TextView) findViewById(R.id.set5_up);
        set5_down = (TextView) findViewById(R.id.set5_down);
        set5_tibreak_up = (TextView) findViewById(R.id.set5_tibreak_up);
        set5_tibreak_down = (TextView) findViewById(R.id.set5_tibreak_down);

        pointUp = (TextView) findViewById(R.id.textViewPointUp);
        pointDown = (TextView) findViewById(R.id.textViewPointDown);

        imgServeUp = (ImageView) findViewById(R.id.imageViewServeUp);
        imgServeDown = (ImageView) findViewById(R.id.imageViewServeDown);


        imgWinCheckUp = (ImageView) findViewById(R.id.imageWincheckUp);
        imgWinCheckDown = (ImageView) findViewById(R.id.imageWincheckDown);

        //init score board

        step="";
        prev_step="";

        step_count.setText("0");
        step_total_count.setText(total_count);

        pointUp.setText("0");
        pointDown.setText("0");

        imgServeUp.setVisibility(View.INVISIBLE);
        imgServeDown.setVisibility(View.INVISIBLE);

        imgWinCheckUp.setVisibility(View.INVISIBLE);
        imgWinCheckDown.setVisibility(View.INVISIBLE);



        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initGoogleApi();
            init_folder_and_files();
            init_voice_folder();

        } else {
            if (checkAndRequestPermissions()) {
                // carry on the normal flow, as the case of  permissions  granted.
                initGoogleApi();
                init_folder_and_files();
                init_voice_folder();

            }
        }



        /*mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Log.d(TAG, "This device is not support bluetooth");
        } else {
            if (mBluetoothAdapter.isEnabled()) {

                Log.d(TAG, "Bluetooth is enabled");
            } else {
                Log.d(TAG, "Bluetooth is disabled");
            }
        }*/



        //Button btnStandalone = (Button) findViewById(R.id.btnStandalone);
        //Button btnWearMode = (Button) findViewById(R.id.btnWearMode);

        //if (mBluetoothAdapter == null)
        //    btnWearMode.setVisibility(View.GONE);

        /*btnStandalone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NormalModeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnWearMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();


            }
        });*/

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override

            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                switch (action) {
                    case GET_SYNC_COMMAND:
                        String msg = intent.getStringExtra("command");
                        //Log.d(TAG, "command action "+msg);
                        if (msg != null) {
                            if (msg.contains("checklanguage")) {

                            } else if (msg.contains("init")) {

                                Log.d(TAG, "=> init");


                                stack.clear();
                                String msgArray[] = msg.split(";");

                                set = msgArray[1];
                                games = msgArray[2];
                                tiebreak = msgArray[3];
                                deuce = msgArray[4];
                                serve = msgArray[5];
                                //startTime = Long.valueOf(msgArray[5]);

                                //Log.d(TAG, "startTime = "+startTime);

                                //gameUp.setText("0");
                                //gameDown.setText("0");
                                step = "";
                                prev_step = "";

                                step_count.setText("0");

                                set1_up.setText("0");
                                set1_down.setText("0");
                                set1_tibreak_up.setText("");
                                set1_tibreak_down.setText("");

                                set2_up.setText("");
                                set2_down.setText("");
                                set2_tibreak_up.setText("");
                                set2_tibreak_down.setText("");

                                set3_up.setText("");
                                set3_down.setText("");
                                set3_tibreak_up.setText("");
                                set3_tibreak_down.setText("");

                                set4_up.setText("");
                                set4_down.setText("");
                                set4_tibreak_up.setText("");
                                set4_tibreak_down.setText("");

                                set5_up.setText("");
                                set5_down.setText("");
                                set5_tibreak_up.setText("");
                                set5_tibreak_down.setText("");

                                imgWinCheckUp.setVisibility(View.INVISIBLE);
                                imgWinCheckDown.setVisibility(View.INVISIBLE);

                                pointUp.setText("0");
                                pointDown.setText("0");

                                if (serve != null) {
                                    if (serve.equals("0")) { //you serve first
                                        imgServeUp.setVisibility(View.INVISIBLE);
                                        imgServeDown.setVisibility(View.VISIBLE);
                                    } else {
                                        imgServeUp.setVisibility(View.VISIBLE);
                                        imgServeDown.setVisibility(View.INVISIBLE);
                                    }
                                } else {
                                    serve = "0";
                                    imgServeUp.setVisibility(View.INVISIBLE);
                                    imgServeDown.setVisibility(View.VISIBLE);
                                }



                            /*if (playerUp != null && playerDown != null) {
                                if (playerUp.equals(""))
                                    playerUp = "Player1";
                                if (playerDown.equals(""))
                                    playerDown = "Player2";
                                nameLayout.setVisibility(View.VISIBLE);
                            } else {
                                if (playerUp == null)
                                    playerUp = "Player1";
                                if (playerDown == null)
                                    playerDown = "Player2";
                                nameLayout.setVisibility(View.VISIBLE);
                            }*/

                                //init time
                                time_use = 0;
                                handler.removeCallbacks(updateTimer);
                                handler.postDelayed(updateTimer, 1000);


                            } else if (msg.contains("calibrate")) {
                                Log.d(TAG, "=> calibrate");

                                stack.clear();
                                String msgArray[] = msg.split("&");
                                step = msgArray[1];
                                set = msgArray[2];
                                games = msgArray[3];
                                tiebreak = msgArray[4];
                                deuce = msgArray[5];
                                serve = msgArray[6];
                                //String end_time = msgArray[5];
                                startTime = Long.valueOf(msgArray[7]);
                                endTime = Long.valueOf(msgArray[8]);

                                if (!step.equals(prev_step)) {
                                    totalCount = totalCount + Long.valueOf(step);

                                    editor = pref.edit();
                                    editor.putString("TOTAL_STEP_COUNT", String.valueOf(totalCount));
                                    editor.apply();

                                    prev_step = step;
                                }

                                step_count.setText(step);
                                step_total_count.setText(String.valueOf(totalCount));

                                if (msgArray.length > 9) {
                                    String stateArray[] = msgArray[9].split(";");
                                    for (int i = 0; i < stateArray.length; i++) {
                                        Log.d(TAG, "stateArray[" + i + "]=" + stateArray[i]);
                                        calculateScore(Boolean.valueOf(stateArray[i]));
                                    }

                                    if (!stack.peek().isFinish()) {
                                        is_fisnish = false;
                                    }
                                }

                            } else {

                                switch (msg) {
                                    case "result":
                                    /*State current_state = stack.peek();
                                    Intent myintent = new Intent(WearModeGameActivity.this, ResultActivity.class);
                                    myintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    myintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    myintent.putExtra("SET1_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x01)));
                                    myintent.putExtra("SET1_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x01)));
                                    myintent.putExtra("SET2_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x02)));
                                    myintent.putExtra("SET2_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x02)));
                                    myintent.putExtra("SET3_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x03)));
                                    myintent.putExtra("SET3_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x03)));
                                    myintent.putExtra("SET4_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x04)));
                                    myintent.putExtra("SET4_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x04)));
                                    myintent.putExtra("SET5_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x05)));
                                    myintent.putExtra("SET5_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x05)));

                                    myintent.putExtra("SET1_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x01)));
                                    myintent.putExtra("SET1_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x01)));
                                    myintent.putExtra("SET2_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x02)));
                                    myintent.putExtra("SET2_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x02)));
                                    myintent.putExtra("SET3_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x03)));
                                    myintent.putExtra("SET3_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x03)));
                                    myintent.putExtra("SET4_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x04)));
                                    myintent.putExtra("SET4_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x04)));
                                    myintent.putExtra("SET5_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x05)));
                                    myintent.putExtra("SET5_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x05)));

                                    myintent.putExtra("GAME_DURATION", String.valueOf(String.valueOf(time_use)));

                                    myintent.putExtra("PLAYER_UP", playerUp);
                                    myintent.putExtra("PLAYER_DOWN", playerDown);
                                    if (current_state.getSetsUp() > current_state.getSetsDown()) {
                                        myintent.putExtra("WIN_PLAYER", playerUp);
                                        myintent.putExtra("LOSE_PLAYER", playerDown);
                                    } else {
                                        myintent.putExtra("WIN_PLAYER", playerDown);
                                        myintent.putExtra("LOSE_PLAYER", playerUp);
                                    }

                                    myintent.putExtra("WEAR_MODE", "true");

                                    myintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    myintent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                    startActivity(myintent);*/
                                        break;
                                    case "close":
                                        Intent myIntent = new Intent(Constants.ACTION.CLOSE_RESULT_ACTIVITY);
                                        sendBroadcast(myIntent);
                                        break;
                                    case "pause":
                                        //handler.removeCallbacks(updateTimer);
                                        break;
                                    case "play":
                                        //handler.removeCallbacks(updateTimer);
                                        //handler.postDelayed(updateTimer, 1000);
                                        break;
                                    case "you":
                                        Log.d(TAG, "=> you");
                                        calculateScore(true);
                                        break;
                                    case "oppt":
                                        Log.d(TAG, "=> oppt");
                                        calculateScore(false);
                                        break;
                                    case "reset":
                                        Log.d(TAG, "=> reset");

                                    /*Intent backintent = new Intent(WearModeGameActivity.this, MainActivity.class);
                                    startActivity(backintent);
                                    finish();*/
                                        break;
                                    case "back":
                                        Log.d(TAG, "=> back");

                                        imgWinCheckUp.setVisibility(View.INVISIBLE);
                                        imgWinCheckDown.setVisibility(View.INVISIBLE);

                                        if (stack.isEmpty()) {
                                            Log.d(TAG, "stack is empty");
                                        } else {
                                            byte current_set;
                                            //stack.pop();
                                            if (stack.pop() != null) { //pop out current
                                                State back_state = stack.peek();
                                                if (back_state != null) {
                                                    current_set = back_state.getCurrent_set();

                                                    //gameUp.setText(String.valueOf(back_state.getSet_game_up(current_set)));
                                                    //gameDown.setText(String.valueOf(back_state.getSet_game_down(current_set)));

                                                    if (back_state.isServe()) {
                                                        imgServeUp.setVisibility(View.INVISIBLE);
                                                        imgServeDown.setVisibility(View.VISIBLE);
                                                    } else {
                                                        imgServeUp.setVisibility(View.VISIBLE);
                                                        imgServeDown.setVisibility(View.INVISIBLE);
                                                    }

                                                    if (!back_state.isInTiebreak()) { //not in tiebreak
                                                        if (back_state.getSet_point_up(current_set) == 1) {
                                                            pointUp.setText(String.valueOf(15));
                                                        } else if (back_state.getSet_point_up(current_set) == 2) {
                                                            pointUp.setText(String.valueOf(30));
                                                        } else if (back_state.getSet_point_up(current_set) == 3) {
                                                            pointUp.setText(String.valueOf(40));
                                                        } else if (back_state.getSet_point_up(current_set) == 4) {
                                                            String pointMsg = String.valueOf(40) + "A";
                                                            pointUp.setText(pointMsg);
                                                        } else {
                                                            pointUp.setText("0");
                                                        }
                                                    } else { //tie break;
                                                        pointUp.setText(String.valueOf(back_state.getSet_point_up(current_set)));
                                                    }

                                                    if (!back_state.isInTiebreak()) { //not in tiebreak
                                                        if (back_state.getSet_point_down(current_set) == 1) {
                                                            pointDown.setText(String.valueOf(15));
                                                        } else if (back_state.getSet_point_down(current_set) == 2) {
                                                            pointDown.setText(String.valueOf(30));
                                                        } else if (back_state.getSet_point_down(current_set) == 3) {
                                                            pointDown.setText(String.valueOf(40));
                                                        } else if (back_state.getSet_point_down(current_set) == 4) {
                                                            String pointMsg = String.valueOf(40) + "A";
                                                            pointDown.setText(pointMsg);
                                                        } else {
                                                            pointDown.setText("0");
                                                        }
                                                    } else {
                                                        pointDown.setText(String.valueOf(back_state.getSet_point_down(current_set)));
                                                    }

                                                    if (back_state.getSetsUp() > 0 || back_state.getSetsDown() > 0) {
                                                        //setLayout.setVisibility(View.VISIBLE);
                                                        //setUp.setText(String.valueOf(back_state.getSetsUp()));
                                                        //setDown.setText(String.valueOf(back_state.getSetsDown()));
                                                    } else {
                                                        //setLayout.setVisibility(View.GONE);
                                                        //setUp.setText("0");
                                                        //setDown.setText("0");
                                                    }

                                                    Log.d(TAG, "########## back state start ##########");
                                                    Log.d(TAG, "current set : " + back_state.getCurrent_set());
                                                    Log.d(TAG, "Serve : " + back_state.isServe());
                                                    Log.d(TAG, "In tiebreak : " + back_state.isInTiebreak());
                                                    Log.d(TAG, "Finish : " + back_state.isFinish());

                                                    int set_limit;
                                                    switch (set) {
                                                        case "0":
                                                            set_limit = 1;
                                                            break;
                                                        case "1":
                                                            set_limit = 3;
                                                            break;
                                                        case "2":
                                                            set_limit = 5;
                                                            break;
                                                        default:
                                                            set_limit = 1;
                                                            break;
                                                    }


                                                    for (int i = 1; i <= set_limit; i++) {
                                                        Log.d(TAG, "================================");
                                                        Log.d(TAG, "[set " + i + "]");
                                                        Log.d(TAG, "[Game : " + back_state.getSet_game_up((byte) i) + " / " + back_state.getSet_game_down((byte) i) + "]");
                                                        Log.d(TAG, "[Point : " + back_state.getSet_point_up((byte) i) + " / " + back_state.getSet_point_down((byte) i) + "]");
                                                        Log.d(TAG, "[tiebreak : " + back_state.getSet_tiebreak_point_up((byte) i) + " / " + back_state.getSet_tiebreak_point_down((byte) i) + "]");
                                                    }


                                                    Log.d(TAG, "########## back state end ##########");

                                                } else {
                                                    //gameUp.setText("0");
                                                    //gameDown.setText("0");

                                                    imgServeUp.setVisibility(View.INVISIBLE);
                                                    imgServeDown.setVisibility(View.INVISIBLE);

                                                    pointUp.setText("0");
                                                    pointDown.setText("0");

                                                    if (serve.equals("0")) { //you server first
                                                        imgServeUp.setVisibility(View.INVISIBLE);
                                                        imgServeDown.setVisibility(View.VISIBLE);
                                                    } else {
                                                        imgServeUp.setVisibility(View.VISIBLE);
                                                        imgServeDown.setVisibility(View.INVISIBLE);
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                }
                            }
                        }

                        break;
                }
            }
        };

        if (!isRegister) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(GET_SYNC_COMMAND);
            registerReceiver(mBroadcastReceiver, filter);
            isRegister = true;
        }

        time_use = 0;
        handler.removeCallbacks(updateTimer);
        handler.postDelayed(updateTimer, 1000);
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause");
        //mGoogleApiClient.disconnect();
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        //Log.d(TAG, "mGoogleApiClient.isConnected = "+mGoogleApiClient.isConnected());
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");

        if (mGoogleApiClient != null) {

            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.disconnect();

            if (isRegister && mBroadcastReceiver != null) {

                try {
                    unregisterReceiver(mBroadcastReceiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                isRegister = false;
                mBroadcastReceiver = null;
                Log.d(TAG, "unregisterReceiver mReceiver");

            }
        }

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "ORIENTATION_LANDSCAPE");
            wearLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
        else {
            Log.d(TAG, "ORIENTATION_PORTRAIT");
            wearLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }

    private void initGoogleApi() {
        Log.d(TAG, "initGoogleApi");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    /*private void initFolder() {



        init_folder_and_files();
        init_voice_folder();

        Intent intent = new Intent(MainActivity.this, WearModeGameActivity.class);
        startActivity(intent);
        finish();
    }*/




    public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }



    private  boolean checkAndRequestPermissions() {
        //int permissionSendMessage = ContextCompat.checkSelfPermission(this,
        //        android.Manifest.permission.WRITE_CALENDAR);
        int locationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        //if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.WRITE_CALENDAR);
        //}
        //if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
        //    listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        //}

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }


    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Log.e(TAG, "result size = "+grantResults.length+ "result[0] = "+grantResults[0]+", result[1] = "+grantResults[1]);


        /*switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                    Log.i(TAG, "WRITE_CALENDAR permissions granted");
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "READ_CONTACTS permissions denied");

                    RetryDialog();
                }
            }
            break;

            // other 'case' lines to check for other
            // permissions this app might request
        }*/
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                //perms.put(android.Manifest.permission.WRITE_CALENDAR, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                //perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (//perms.get(android.Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED &&
                            perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
                                    //&& perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        Log.d(TAG, "write permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        initGoogleApi();
                        init_folder_and_files();
                        init_voice_folder();

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (//ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_CALENDAR) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        //|| ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)
                                ) {
                            showDialogOK(getResources().getString(R.string.permission_descript),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            /*case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;*/
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session

                    Log.d(TAG, "Bluetooth now enable");
                    //setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "Bluetooth not enabled");
                    //Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                    //        Toast.LENGTH_SHORT).show();
                    //getActivity().finish();
                }
        }
    }



    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        voiceItem = menu.findItem(R.id.action_lang_support);

        voiceItem.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Intent intent;
        switch (item.getItemId()) {
            case R.id.action_lang_support:
                //intent = new Intent(MainActivity.this, VoiceSelectActivity.class);
                //startActivity(intent);
                /*new Thread() {

                    @Override

                    public void run() {
                        syncVoiceToWear("gbr/man", mGoogleApiClient, count);
                    }
                }.start();*/
                syncSendCommand("test");
                break;

            default:
                break;
        }
        return true;
    }

    private void syncSendCommand(String cmd) {
        Log.d(TAG, "syncSendCommand");
        if(mGoogleApiClient==null) {
            Log.e(TAG, "mGoogleApiClient = null");
        } else {
            String modify = cmd + count;
            Log.d(TAG, "mGoogleApiClient = " + mGoogleApiClient.isConnected() + " modify = " +modify);
            PutDataMapRequest putRequest = PutDataMapRequest.create("/MOBILE_COMMAND");
            DataMap map = putRequest.getDataMap();

            //map.putInt("color", Color.RED);
            map.putString("cmd", modify);
            map.putLong("count", count);
            count++;
            Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());
        }
    }


    private void calculateScore(boolean you_score) {
        byte current_set;
        State new_state=null;
        //load top state first
        State current_state = stack.peek();

        int set_limit;

        if (set == null) {
            set = "0";
        }

        if (games == null) {
            games = "0";
        }

        if (tiebreak == null) {
            tiebreak = "0";
        }

        if (deuce == null) {
            deuce = "0";
        }

        if (serve == null) {
            serve = "0";
        }

        switch (set)
        {
            case "0":
                set_limit = 1;
                break;
            case "1":
                set_limit = 3;
                break;
            case "2":
                set_limit = 5;
                break;
            default:
                set_limit = 1;
                break;
        }

        if (current_state != null) {
            current_set = current_state.getCurrent_set();
            /*Log.d(TAG, "########## current state start ##########");
            Log.d(TAG, "default:");
            Log.d(TAG, "set = " + set);
            //Log.d(TAG, "game = " + game);
            Log.d(TAG, "tiebreak = " + tiebreak);
            Log.d(TAG, "deuce = " + deuce);
            Log.d(TAG, "serve = " + serve);
            Log.d(TAG, "======================");

            Log.d(TAG, "current set : " + current_state.getCurrent_set());
            Log.d(TAG, "Serve : " + current_state.isServe());
            Log.d(TAG, "In tiebreak : " + current_state.isInTiebreak());
            Log.d(TAG, "Finish : " + current_state.isFinish());

            //Log.d(TAG, "set 1:");
            Log.d(TAG, "Game : " + current_state.getSet_game_up(current_set) + " / " + current_state.getSet_game_down(current_set));
            Log.d(TAG, "Point : " + current_state.getSet_point_up(current_set) + " / " + current_state.getSet_point_down(current_set));
            Log.d(TAG, "tiebreak : " + current_state.getSet_tiebreak_point_up(current_set) + " / " + current_state.getSet_tiebreak_point_down(current_set));
            Log.d(TAG, "########## current state end ##########");*/

            if (current_state.isFinish()) {
                Log.d(TAG, "*** Game is Over ***");

                for (int i=1; i <= current_set; i++) {
                    if (i == 1) {
                        set1_up.setText(String.valueOf(current_state.getSet_game_up((byte)0x01)));
                        set1_down.setText(String.valueOf(current_state.getSet_game_down((byte)0x01)));
                        if (current_set > 1) {
                            if (current_state.getSet_tiebreak_point_up((byte)0x01) > 0 &&
                                    current_state.getSet_tiebreak_point_down((byte)0x01) > 0) {
                                set1_tibreak_up.setVisibility(View.VISIBLE);
                                set1_tibreak_up.setText(String.valueOf(current_state.getSet_tiebreak_point_up((byte) 0x01)));
                                set1_tibreak_down.setVisibility(View.VISIBLE);
                                set1_tibreak_down.setText(String.valueOf(current_state.getSet_tiebreak_point_down((byte) 0x01)));
                            } else {
                                set1_tibreak_up.setVisibility(View.GONE);
                                set1_tibreak_down.setVisibility(View.GONE);
                            }
                        } else {
                            set1_tibreak_up.setVisibility(View.GONE);
                            set1_tibreak_down.setVisibility(View.GONE);
                        }
                    } else if (i == 2) {
                        set2_up.setText(String.valueOf(current_state.getSet_game_up((byte)0x02)));
                        set2_down.setText(String.valueOf(current_state.getSet_game_down((byte)0x02)));
                        if (current_set > 2) {
                            if (current_state.getSet_tiebreak_point_up((byte)0x02) > 0 &&
                                    current_state.getSet_tiebreak_point_down((byte)0x02) > 0) {
                                set2_tibreak_up.setVisibility(View.VISIBLE);
                                set2_tibreak_up.setText(String.valueOf(current_state.getSet_tiebreak_point_up((byte) 0x02)));
                                set2_tibreak_down.setVisibility(View.VISIBLE);
                                set2_tibreak_down.setText(String.valueOf(current_state.getSet_tiebreak_point_down((byte) 0x02)));
                            } else {
                                set2_tibreak_up.setVisibility(View.GONE);
                                set2_tibreak_down.setVisibility(View.GONE);
                            }
                        } else {
                            set2_tibreak_up.setVisibility(View.GONE);
                            set2_tibreak_down.setVisibility(View.GONE);
                        }
                    } else if (i == 3) {
                        set3_up.setText(String.valueOf(current_state.getSet_game_up((byte)0x03)));
                        set3_down.setText(String.valueOf(current_state.getSet_game_down((byte)0x03)));
                        if (current_set > 3) {
                            if (current_state.getSet_tiebreak_point_up((byte)0x03) > 0 &&
                                    current_state.getSet_tiebreak_point_down((byte)0x03) > 0) {
                                set3_tibreak_up.setVisibility(View.VISIBLE);
                                set3_tibreak_up.setText(String.valueOf(current_state.getSet_tiebreak_point_up((byte) 0x03)));
                                set3_tibreak_down.setVisibility(View.VISIBLE);
                                set3_tibreak_down.setText(String.valueOf(current_state.getSet_tiebreak_point_down((byte) 0x03)));
                            } else {
                                set3_tibreak_up.setVisibility(View.GONE);
                                set3_tibreak_down.setVisibility(View.GONE);
                            }
                        } else {
                            set3_tibreak_up.setVisibility(View.GONE);
                            set3_tibreak_down.setVisibility(View.GONE);
                        }
                    } else if (i == 4) {
                        set4_up.setText(String.valueOf(current_state.getSet_game_up((byte)0x04)));
                        set4_down.setText(String.valueOf(current_state.getSet_game_down((byte)0x04)));
                        if (current_set > 4) {
                            if (current_state.getSet_tiebreak_point_up((byte)0x04) > 0 &&
                                    current_state.getSet_tiebreak_point_down((byte)0x04) > 0) {
                                set4_tibreak_up.setVisibility(View.VISIBLE);
                                set4_tibreak_up.setText(String.valueOf(current_state.getSet_tiebreak_point_up((byte) 0x04)));
                                set4_tibreak_down.setVisibility(View.VISIBLE);
                                set4_tibreak_down.setText(String.valueOf(current_state.getSet_tiebreak_point_down((byte) 0x04)));
                            } else {
                                set4_tibreak_up.setVisibility(View.GONE);
                                set4_tibreak_down.setVisibility(View.GONE);
                            }
                        } else {
                            set4_tibreak_up.setVisibility(View.GONE);
                            set4_tibreak_down.setVisibility(View.GONE);
                        }
                    } else if (i == 5) {
                        set5_up.setText(String.valueOf(current_state.getSet_game_up((byte)0x05)));
                        set5_down.setText(String.valueOf(current_state.getSet_game_up((byte)0x05)));
                        if (new_state.isFinish()) {
                            if (current_state.getSet_tiebreak_point_up((byte)0x05) > 0 &&
                                    current_state.getSet_tiebreak_point_down((byte)0x05) > 0) {
                                set5_tibreak_up.setVisibility(View.VISIBLE);
                                set5_tibreak_up.setText(String.valueOf(current_state.getSet_tiebreak_point_up((byte) 0x05)));
                                set5_tibreak_down.setVisibility(View.VISIBLE);
                                set5_tibreak_down.setText(String.valueOf(current_state.getSet_tiebreak_point_down((byte) 0x05)));
                            } else {
                                set5_tibreak_up.setVisibility(View.GONE);
                                set5_tibreak_down.setVisibility(View.GONE);
                            }
                        } else {
                            set5_tibreak_up.setVisibility(View.GONE);
                            set5_tibreak_down.setVisibility(View.GONE);
                        }

                    }
                }

                //handler.removeCallbacks(updateTimer);

                /*Intent intent = new Intent(WearModeGameActivity.this, ResultActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("SET1_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x01)));
                intent.putExtra("SET1_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x01)));
                intent.putExtra("SET2_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x02)));
                intent.putExtra("SET2_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x02)));
                intent.putExtra("SET3_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x03)));
                intent.putExtra("SET3_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x03)));
                intent.putExtra("SET4_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x04)));
                intent.putExtra("SET4_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x04)));
                intent.putExtra("SET5_GAME_UP",   String.valueOf(current_state.getSet_game_up((byte)0x05)));
                intent.putExtra("SET5_GAME_DOWN", String.valueOf(current_state.getSet_game_down((byte)0x05)));

                intent.putExtra("SET1_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x01)));
                intent.putExtra("SET1_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x01)));
                intent.putExtra("SET2_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x02)));
                intent.putExtra("SET2_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x02)));
                intent.putExtra("SET3_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x03)));
                intent.putExtra("SET3_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x03)));
                intent.putExtra("SET4_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x04)));
                intent.putExtra("SET4_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x04)));
                intent.putExtra("SET5_TIEBREAK_UP",   String.valueOf(current_state.getSet_tiebreak_point_up((byte)0x05)));
                intent.putExtra("SET5_TIEBREAK_DOWN", String.valueOf(current_state.getSet_tiebreak_point_down((byte)0x05)));

                intent.putExtra("GAME_DURATION", String.valueOf(String.valueOf(time_use)));

                intent.putExtra("PLAYER_UP", playerUp);
                intent.putExtra("PLAYER_DOWN", playerDown);
                if (current_state.getSetsUp() > current_state.getSetsDown()) {
                    intent.putExtra("WIN_PLAYER", playerUp);
                    intent.putExtra("LOSE_PLAYER", playerDown);
                } else {
                    intent.putExtra("WIN_PLAYER", playerDown);
                    intent.putExtra("LOSE_PLAYER", playerUp);
                }

                intent.putExtra("WEAR_MODE", "true");

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);*/
            } else { //not finish

                Log.d(TAG, "*** Game is running ***");
                if (you_score) {
                    Log.d(TAG, "=== I score start ===");

                    if (stack.isEmpty()) { //the state stack is empty
                        new_state = new State();
                        Log.d(TAG, "==>[Stack empty]");
                        if (serve.equals("0"))
                            new_state.setServe(true);
                        else
                            new_state.setServe(false);

                        //set current set = 1
                        new_state.setCurrent_set((byte) 0x01);

                        new_state.setSet_point_down((byte) 0x01, (byte) 0x01);
                        //new_state.setSet_1_point_down((byte)0x01);


                        //Log.e(TAG, "get_set_1_point_down = "+new_state.getSet_1_point_down()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));
                    } else {
                        Log.d(TAG, "==>[Stack not empty]");

                        if (current_state.isFinish()) {
                            Log.d(TAG, "**** Game Finish ****");
                        } else {
                            new_state = new State();
                            //new_state = stack.peek();
                            // copy previous state;
                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }


                            //you score!
                            byte point = current_state.getSet_point_down(current_set);
                            Log.d(TAG, "Your point " + point + " change to " + (++point));
                            new_state.setSet_point_down(current_set, point);

                            checkPoint(new_state);

                            checkGames(new_state);
                        }
                    }

                    Log.d(TAG, "=== I score end ===");
                } else {
                    Log.d(TAG, "=== Oppt score start ===");
                    if (stack.isEmpty()) { //the state stack is empty
                        new_state = new State();
                        Log.d(TAG, "==>[Stack empty]");
                        if (serve.equals("0"))
                            new_state.setServe(true);
                        else
                            new_state.setServe(false);

                        //set current set = 1
                        new_state.setCurrent_set((byte) 0x01);

                        new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                        //Log.e(TAG, "get_set_1_point_up = "+new_state.getSet_1_point_up()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));

                    } else {
                        Log.d(TAG, "==>[Stack not empty]");
                        if (current_state.isFinish()) {
                            Log.d(TAG, "**** Game Finish ****");
                        } else {
                            new_state = new State();
                            //new_state = stack.peek();
                            // copy previous state;
                            new_state.setCurrent_set(current_state.getCurrent_set());
                            new_state.setServe(current_state.isServe());
                            new_state.setInTiebreak(current_state.isInTiebreak());
                            new_state.setFinish(current_state.isFinish());
                            new_state.setSetsUp(current_state.getSetsUp());
                            new_state.setSetsDown(current_state.getSetsDown());

                            for (byte i=1; i<=set_limit; i++) {
                                new_state.setSet_game_up(i, current_state.getSet_game_up(i));
                                new_state.setSet_game_down(i, current_state.getSet_game_down(i));
                                new_state.setSet_point_up(i, current_state.getSet_point_up(i));
                                new_state.setSet_point_down(i, current_state.getSet_point_down(i));
                                new_state.setSet_tiebreak_point_up(i, current_state.getSet_tiebreak_point_up(i));
                                new_state.setSet_tiebreak_point_down(i, current_state.getSet_tiebreak_point_down(i));
                            }

                            //oppt score!
                            byte point = current_state.getSet_point_up(current_set);
                            Log.d(TAG, "Opponent point " + point + " change to " + (++point));
                            new_state.setSet_point_up(current_set, point);

                            checkPoint(new_state);

                            checkGames(new_state);
                        }
                    }
                    Log.d(TAG, "=== Oppt score end ===");
                }

                if (new_state != null) {

                    Log.d(TAG, "########## new state start ##########");
                    Log.d(TAG, "current set : " + new_state.getCurrent_set());
                    Log.d(TAG, "Serve : " + new_state.isServe());
                    Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                    Log.d(TAG, "Finish : " + new_state.isFinish());

                    for (int i = 1; i <= set_limit; i++) {
                        Log.d(TAG, "================================");
                        Log.d(TAG, "[set " + i + "]");
                        Log.d(TAG, "[Game : " + new_state.getSet_game_up((byte) i) + " / " + new_state.getSet_game_down((byte) i) + "]");
                        Log.d(TAG, "[Point : " + new_state.getSet_point_up((byte) i) + " / " + new_state.getSet_point_down((byte) i) + "]");
                        Log.d(TAG, "[tiebreak : " + new_state.getSet_tiebreak_point_up((byte) i) + " / " + new_state.getSet_tiebreak_point_down((byte) i) + "]");
                    }

                    Log.d(TAG, "########## new state end ##########");

                    //then look up top state
                    //State new_current_state = stack.peek();
                    current_set = new_state.getCurrent_set();

                    if (new_state.getSetsUp() > 0 || new_state.getSetsDown() > 0) {
                        //setLayout.setVisibility(View.VISIBLE);
                        //setUp.setText(String.valueOf(new_state.getSetsUp()));
                        //setDown.setText(String.valueOf(new_state.getSetsDown()));
                    } else {
                        //setLayout.setVisibility(View.GONE);
                        //setUp.setText("0");
                        //setDown.setText("0");
                    }

                    //gameUp.setText(String.valueOf(new_state.getSet_game_up(current_set)));
                    //gameDown.setText(String.valueOf(new_state.getSet_game_down(current_set)));

                    if (new_state.isFinish()) {
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);

                        if (new_state.getSetsUp() > new_state.getSetsDown()) {
                            imgWinCheckUp.setVisibility(View.VISIBLE);
                            imgWinCheckDown.setVisibility(View.INVISIBLE);
                        } else {
                            imgWinCheckUp.setVisibility(View.INVISIBLE);
                            imgWinCheckDown.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (new_state.isServe()) {
                            imgServeUp.setVisibility(View.INVISIBLE);
                            imgServeDown.setVisibility(View.VISIBLE);
                        } else {
                            imgServeUp.setVisibility(View.VISIBLE);
                            imgServeDown.setVisibility(View.INVISIBLE);
                        }
                        imgWinCheckUp.setVisibility(View.INVISIBLE);
                        imgWinCheckDown.setVisibility(View.INVISIBLE);
                    }

                    if (new_state.isServe()) {
                        imgServeUp.setVisibility(View.INVISIBLE);
                        imgServeDown.setVisibility(View.VISIBLE);
                    } else {
                        imgServeUp.setVisibility(View.VISIBLE);
                        imgServeDown.setVisibility(View.INVISIBLE);
                    }

                    if (!new_state.isInTiebreak()) { //not in tiebreak
                        if (new_state.getSet_point_up(current_set) == 1) {
                            pointUp.setText(String.valueOf(15));
                        } else if (new_state.getSet_point_up(current_set) == 2) {
                            pointUp.setText(String.valueOf(30));
                        } else if (new_state.getSet_point_up(current_set) == 3) {
                            pointUp.setText(String.valueOf(40));
                        } else if (new_state.getSet_point_up(current_set) == 4) {
                            String msg = "Ad";
                            pointUp.setText(msg);
                        } else {
                            pointUp.setText("0");
                        }
                    } else { //tie break;
                        pointUp.setText(String.valueOf(new_state.getSet_point_up(current_set)));
                    }

                    if (!new_state.isInTiebreak()) { //not in tiebreak
                        if (new_state.getSet_point_down(current_set) == 1) {
                            pointDown.setText(String.valueOf(15));
                        } else if (new_state.getSet_point_down(current_set) == 2) {
                            pointDown.setText(String.valueOf(30));
                        } else if (new_state.getSet_point_down(current_set) == 3) {
                            pointDown.setText(String.valueOf(40));
                        } else if (new_state.getSet_point_down(current_set) == 4) {
                            String msg = "Ad";
                            pointDown.setText(msg);
                        } else {
                            pointDown.setText("0");
                        }
                    } else {
                        pointDown.setText(String.valueOf(new_state.getSet_point_down(current_set)));
                    }

                    for (int i=1; i <= new_state.getCurrent_set(); i++) {
                        if (i == 1) {
                            set1_up.setText(String.valueOf(new_state.getSet_game_up((byte)0x01)));
                            set1_down.setText(String.valueOf(new_state.getSet_game_down((byte)0x01)));
                            if (new_state.isFinish()) {
                                set1_tibreak_up.setVisibility(View.VISIBLE);
                                set1_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x01)));
                                set1_tibreak_down.setVisibility(View.VISIBLE);
                                set1_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x01)));
                            } else if (new_state.getCurrent_set() > 1) {
                                if (new_state.getSet_tiebreak_point_up((byte)0x01) > 0 &&
                                        new_state.getSet_tiebreak_point_down((byte)0x01) > 0) {
                                    set1_tibreak_up.setVisibility(View.VISIBLE);
                                    set1_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x01)));
                                    set1_tibreak_down.setVisibility(View.VISIBLE);
                                    set1_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x01)));
                                } else {
                                    set1_tibreak_up.setVisibility(View.GONE);
                                    set1_tibreak_down.setVisibility(View.GONE);
                                }
                            } else {
                                set1_tibreak_up.setVisibility(View.GONE);
                                set1_tibreak_down.setVisibility(View.GONE);
                            }
                        } else if (i == 2) {
                            set2_up.setText(String.valueOf(new_state.getSet_game_up((byte)0x02)));
                            set2_down.setText(String.valueOf(new_state.getSet_game_down((byte)0x02)));
                            if (new_state.isFinish()) {
                                set2_tibreak_up.setVisibility(View.VISIBLE);
                                set2_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x02)));
                                set2_tibreak_down.setVisibility(View.VISIBLE);
                                set2_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x02)));
                            } else if (new_state.getCurrent_set() > 2) {
                                if (new_state.getSet_tiebreak_point_up((byte)0x02) > 0 &&
                                        new_state.getSet_tiebreak_point_down((byte)0x02) > 0) {
                                    set2_tibreak_up.setVisibility(View.VISIBLE);
                                    set2_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x02)));
                                    set2_tibreak_down.setVisibility(View.VISIBLE);
                                    set2_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x02)));
                                } else {
                                    set2_tibreak_up.setVisibility(View.GONE);
                                    set2_tibreak_down.setVisibility(View.GONE);
                                }
                            } else {
                                set2_tibreak_up.setVisibility(View.GONE);
                                set2_tibreak_down.setVisibility(View.GONE);
                            }
                        } else if (i == 3) {
                            set3_up.setText(String.valueOf(new_state.getSet_game_up((byte)0x03)));
                            set3_down.setText(String.valueOf(new_state.getSet_game_down((byte)0x03)));
                            if (new_state.isFinish()) {
                                set3_tibreak_up.setVisibility(View.VISIBLE);
                                set3_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x03)));
                                set3_tibreak_down.setVisibility(View.VISIBLE);
                                set3_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x03)));
                            } else if (new_state.getCurrent_set() > 3) {
                                if (new_state.getSet_tiebreak_point_up((byte)0x03) > 0 &&
                                        new_state.getSet_tiebreak_point_down((byte)0x03) > 0) {
                                    set3_tibreak_up.setVisibility(View.VISIBLE);
                                    set3_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x03)));
                                    set3_tibreak_down.setVisibility(View.VISIBLE);
                                    set3_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x03)));
                                } else {
                                    set3_tibreak_up.setVisibility(View.GONE);
                                    set3_tibreak_down.setVisibility(View.GONE);
                                }
                            }  else {
                                set3_tibreak_up.setVisibility(View.GONE);
                                set3_tibreak_down.setVisibility(View.GONE);
                            }
                        } else if (i == 4) {
                            set4_up.setText(String.valueOf(new_state.getSet_game_up((byte)0x04)));
                            set4_down.setText(String.valueOf(new_state.getSet_game_down((byte)0x04)));
                            if (new_state.isFinish()) {
                                set4_tibreak_up.setVisibility(View.VISIBLE);
                                set4_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x04)));
                                set4_tibreak_down.setVisibility(View.VISIBLE);
                                set4_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x04)));
                            } else if (new_state.getCurrent_set() > 4) {
                                if (new_state.getSet_tiebreak_point_up((byte)0x04) > 0 &&
                                        new_state.getSet_tiebreak_point_down((byte)0x04) > 0) {
                                    set4_tibreak_up.setVisibility(View.VISIBLE);
                                    set4_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x04)));
                                    set4_tibreak_down.setVisibility(View.VISIBLE);
                                    set4_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x04)));
                                } else {
                                    set4_tibreak_up.setVisibility(View.GONE);
                                    set4_tibreak_down.setVisibility(View.GONE);
                                }
                            } else {
                                set4_tibreak_up.setVisibility(View.GONE);
                                set4_tibreak_down.setVisibility(View.GONE);
                            }
                        } else if (i == 5) {
                            set5_up.setText(String.valueOf(new_state.getSet_game_up((byte)0x05)));
                            set5_down.setText(String.valueOf(new_state.getSet_game_up((byte)0x05)));
                            if (new_state.isFinish()) {
                                set5_tibreak_up.setVisibility(View.VISIBLE);
                                set5_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x05)));
                                set5_tibreak_down.setVisibility(View.VISIBLE);
                                set5_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x05)));
                            } else if (new_state.isFinish()) {
                                if (new_state.getSet_tiebreak_point_up((byte)0x05) > 0 &&
                                        new_state.getSet_tiebreak_point_down((byte)0x05) > 0) {
                                    set5_tibreak_up.setVisibility(View.VISIBLE);
                                    set5_tibreak_up.setText(String.valueOf(new_state.getSet_tiebreak_point_up((byte) 0x05)));
                                    set5_tibreak_down.setVisibility(View.VISIBLE);
                                    set5_tibreak_down.setText(String.valueOf(new_state.getSet_tiebreak_point_down((byte) 0x05)));
                                } else {
                                    set5_tibreak_up.setVisibility(View.GONE);
                                    set5_tibreak_down.setVisibility(View.GONE);
                                }
                            } else {
                                set5_tibreak_up.setVisibility(View.GONE);
                                set5_tibreak_down.setVisibility(View.GONE);
                            }

                        }
                    }

                    //push into stack
                    stack.push(new_state);

            /*Log.d(TAG, "@@@@@@ stack @@@@@@");
            for (State s : stack) {
                Log.d(TAG, "current set : " + s.getCurrent_set());
                Log.d(TAG, "Serve : " + s.isServe());
                Log.d(TAG, "In tiebreak : " + s.isInTiebreak());
                Log.d(TAG, "Finish : " + s.isFinish());

                for (int i = 1; i <= set_limit; i++) {

                    Log.d(TAG, "[set " + i + "]");
                    Log.d(TAG, "[Game : " + s.getSet_game_up((byte) i) + " / " + s.getSet_game_down((byte) i) + "]");
                    Log.d(TAG, "[Point : " + s.getSet_point_up((byte) i) + " / " + s.getSet_point_down((byte) i) + "]");
                    Log.d(TAG, "[tiebreak : " + s.getSet_tiebreak_point_up((byte) i) + " / " + s.getSet_tiebreak_point_down((byte) i) + "]");
                }
                Log.d(TAG, "================================");
            }
            Log.d(TAG, "@@@@@@ stack @@@@@@");*/
                }






            }
        } else {
            Log.d(TAG, "Stack is empty!");


            Log.d(TAG, "*** Game is running ***");
            if (you_score) {
                Log.d(TAG, "=== I score start ===");

                //if (stack.isEmpty()) { //the state stack is empty
                new_state = new State();
                Log.d(TAG, "==>[Stack empty]");
                if (serve.equals("0"))
                    new_state.setServe(true);
                else
                    new_state.setServe(false);

                //set current set = 1
                new_state.setCurrent_set((byte) 0x01);

                new_state.setSet_point_down((byte) 0x01, (byte) 0x01);
                //new_state.setSet_1_point_down((byte)0x01);


                //Log.e(TAG, "get_set_1_point_down = "+new_state.getSet_1_point_down()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));
                //}

                Log.d(TAG, "=== I score end ===");
            } else {
                Log.d(TAG, "=== Oppt score start ===");
                //if (stack.isEmpty()) { //the state stack is empty
                new_state = new State();
                Log.d(TAG, "==>[Stack empty]");
                if (serve.equals("0"))
                    new_state.setServe(true);
                else
                    new_state.setServe(false);

                //set current set = 1
                new_state.setCurrent_set((byte) 0x01);

                new_state.setSet_point_up((byte) 0x01, (byte) 0x01);

                //Log.e(TAG, "get_set_1_point_up = "+new_state.getSet_1_point_up()+", isServe ? "+ (new_state.isServe() ? "YES" : "NO"));

                //}
                Log.d(TAG, "=== Oppt score end ===");
            }

            if (new_state != null) {

                Log.d(TAG, "########## new state start ##########");
                Log.d(TAG, "current set : " + new_state.getCurrent_set());
                Log.d(TAG, "Serve : " + new_state.isServe());
                Log.d(TAG, "In tiebreak : " + new_state.isInTiebreak());
                Log.d(TAG, "Finish : " + new_state.isFinish());

                for (int i = 1; i <= set_limit; i++) {
                    Log.d(TAG, "================================");
                    Log.d(TAG, "[set " + i + "]");
                    Log.d(TAG, "[Game : " + new_state.getSet_game_up((byte) i) + " / " + new_state.getSet_game_down((byte) i) + "]");
                    Log.d(TAG, "[Point : " + new_state.getSet_point_up((byte) i) + " / " + new_state.getSet_point_down((byte) i) + "]");
                    Log.d(TAG, "[tiebreak : " + new_state.getSet_tiebreak_point_up((byte) i) + " / " + new_state.getSet_tiebreak_point_down((byte) i) + "]");
                }

                Log.d(TAG, "########## new state end ##########");

                //then look up top state
                //State new_current_state = stack.peek();
                current_set = new_state.getCurrent_set();

                //gameUp.setText(String.valueOf(new_state.getSet_game_up(current_set)));
                //gameDown.setText(String.valueOf(new_state.getSet_game_down(current_set)));

                if (new_state.isServe()) {
                    imgServeUp.setVisibility(View.INVISIBLE);
                    imgServeDown.setVisibility(View.VISIBLE);
                } else {
                    imgServeUp.setVisibility(View.VISIBLE);
                    imgServeDown.setVisibility(View.INVISIBLE);
                }

                if (!new_state.isInTiebreak()) { //not in tiebreak
                    if (new_state.getSet_point_up(current_set) == 1) {
                        pointUp.setText(String.valueOf(15));
                    } else if (new_state.getSet_point_up(current_set) == 2) {
                        pointUp.setText(String.valueOf(30));
                    } else if (new_state.getSet_point_up(current_set) == 3) {
                        pointUp.setText(String.valueOf(40));
                    } else if (new_state.getSet_point_up(current_set) == 4) {
                        String msg = String.valueOf(40)+"A";
                        pointUp.setText(msg);
                    } else {
                        pointUp.setText("0");
                    }
                } else { //tie break;
                    pointUp.setText(String.valueOf(new_state.getSet_point_up(current_set)));
                }

                if (!new_state.isInTiebreak()) { //not in tiebreak
                    if (new_state.getSet_point_down(current_set) == 1) {
                        pointDown.setText(String.valueOf(15));
                    } else if (new_state.getSet_point_down(current_set) == 2) {
                        pointDown.setText(String.valueOf(30));
                    } else if (new_state.getSet_point_down(current_set) == 3) {
                        pointDown.setText(String.valueOf(40));
                    } else if (new_state.getSet_point_down(current_set) == 4) {
                        String msg = String.valueOf(40)+"A";
                        pointDown.setText(msg);
                    } else {
                        pointDown.setText("0");
                    }
                } else {
                    pointDown.setText(String.valueOf(new_state.getSet_point_down(current_set)));
                }

                //push into stack
                stack.push(new_state);
            }
        }
    }

    private void checkPoint(State new_state) {
        Log.d(TAG, "[Check point Start]");

        byte current_set = new_state.getCurrent_set();
        if (new_state.isInTiebreak()) { //in tiebreak
            Log.d(TAG, "[In Tiebreak]");
            byte game;


            if (tiebreak.equals("2")) { //super tiebreak
                if (new_state.getSet_point_up(current_set) == 10 && new_state.getSet_point_down(current_set) <= 8) {
                    //7 : 0,1,2,3,4,5 => oppt win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                } else if (new_state.getSet_point_up(current_set) <= 8 && new_state.getSet_point_down(current_set) == 10) {
                    //0,1,2,3,4,5 : 7 => you win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                } else if (new_state.getSet_point_up(current_set) >= 9 &&
                        new_state.getSet_point_down(current_set) >= 9 &&
                        (new_state.getSet_point_up(current_set) - new_state.getSet_point_down(current_set)) == 2) {
                    //8:6, 9:7, 10:8.... => oppt win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                } else if (new_state.getSet_point_up(current_set) >= 9 &&
                        new_state.getSet_point_down(current_set) >= 9 &&
                        (new_state.getSet_point_down(current_set) - new_state.getSet_point_up(current_set)) == 2) {
                    //6:8, 7:9, 8:10.... => you win this game
                    //set tiebreak point
                    new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                    new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    //if (new_state.isServe()) {
                    if (am_I_Tiebreak_First_Serve) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                    //leave tiebreak;
                    new_state.setInTiebreak(false);
                }  else {
                    Log.d(TAG, "Other tie break, isServe = " + new_state.isServe());

                    byte plus = (byte) (new_state.getSet_point_up(current_set)+new_state.getSet_point_down(current_set));

                    if (plus%2 == 1) {
                        //change serve
                        Log.d(TAG, "==>Points plus become odd, change serve!");
                        if (new_state.isServe()) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }
                    }
                }
            } else {
                if (games.equals("0")) { //6 game in a set
                    Log.d(TAG, "[6 games in a set]"); //6:6 => tiebreak

                    if (new_state.getSet_point_up(current_set) == 7 && new_state.getSet_point_down(current_set) <= 5) {
                        //7 : 0,1,2,3,4,5 => oppt win this game
                        //set tiebreak point
                        new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                        new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                        //set point clean
                        new_state.setSet_point_up(current_set, (byte)0);
                        new_state.setSet_point_down(current_set, (byte)0);
                        //add to game
                        game = new_state.getSet_game_up(current_set);
                        game++;
                        new_state.setSet_game_up(current_set, game);
                        //change serve
                        //if (new_state.isServe()) {
                        if (am_I_Tiebreak_First_Serve) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }

                        //leave tiebreak;
                        new_state.setInTiebreak(false);
                    } else if (new_state.getSet_point_up(current_set) <= 5 && new_state.getSet_point_down(current_set) == 7) {
                        //0,1,2,3,4,5 : 7 => you win this game
                        //set tiebreak point
                        new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                        new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                        //set point clean
                        new_state.setSet_point_up(current_set, (byte)0);
                        new_state.setSet_point_down(current_set, (byte)0);
                        //add to game
                        game = new_state.getSet_game_down(current_set);
                        game++;
                        new_state.setSet_game_down(current_set, game);
                        //change serve
                        //if (new_state.isServe()) {
                        if (am_I_Tiebreak_First_Serve) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }
                        //leave tiebreak;
                        new_state.setInTiebreak(false);
                    } else if (new_state.getSet_point_up(current_set) >= 6 &&
                            new_state.getSet_point_down(current_set) >= 6 &&
                            (new_state.getSet_point_up(current_set) - new_state.getSet_point_down(current_set)) == 2) {
                        //8:6, 9:7, 10:8.... => oppt win this game
                        //set tiebreak point
                        new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                        new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                        //set point clean
                        new_state.setSet_point_up(current_set, (byte)0);
                        new_state.setSet_point_down(current_set, (byte)0);
                        //add to game
                        game = new_state.getSet_game_up(current_set);
                        game++;
                        new_state.setSet_game_up(current_set, game);
                        //change serve
                        //if (new_state.isServe()) {
                        if (am_I_Tiebreak_First_Serve) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }

                        //leave tiebreak;
                        new_state.setInTiebreak(false);
                    } else if (new_state.getSet_point_up(current_set) >= 6 &&
                            new_state.getSet_point_down(current_set) >= 6 &&
                            (new_state.getSet_point_down(current_set) - new_state.getSet_point_up(current_set)) == 2) {
                        //6:8, 7:9, 8:10.... => you win this game
                        //set tiebreak point
                        new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                        new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                        //set point clean
                        new_state.setSet_point_up(current_set, (byte)0);
                        new_state.setSet_point_down(current_set, (byte)0);
                        //add to game
                        game = new_state.getSet_game_down(current_set);
                        game++;
                        new_state.setSet_game_down(current_set, game);
                        //change serve
                        //if (new_state.isServe()) {
                        if (am_I_Tiebreak_First_Serve) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }

                        //leave tiebreak;
                        new_state.setInTiebreak(false);
                    }  else {
                        Log.d(TAG, "Other tie break, isServe = " + new_state.isServe());

                        byte plus = (byte) (new_state.getSet_point_up(current_set)+new_state.getSet_point_down(current_set));

                        if (plus%2 == 1) {
                            //change serve
                            Log.d(TAG, "==>Points plus become odd, change serve!");
                            if (new_state.isServe()) {
                                new_state.setServe(false);
                            } else {
                                new_state.setServe(true);
                            }
                        }
                    }


                } else {
                    Log.d(TAG, "[4 games in a set]"); //4:4 => tiebreak

                    if (new_state.getSet_point_up(current_set) == 5 && new_state.getSet_point_down(current_set) <= 3) {
                        //7 : 0,1,2,3,4,5 => oppt win this game
                        //set tiebreak point
                        new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                        new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                        //set point clean
                        new_state.setSet_point_up(current_set, (byte)0);
                        new_state.setSet_point_down(current_set, (byte)0);
                        //add to game
                        game = new_state.getSet_game_up(current_set);
                        game++;
                        new_state.setSet_game_up(current_set, game);
                        //change serve
                        //if (new_state.isServe()) {
                        if (am_I_Tiebreak_First_Serve) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }

                        //leave tiebreak;
                        new_state.setInTiebreak(false);

                    } else if (new_state.getSet_point_up(current_set) <= 3 && new_state.getSet_point_down(current_set) == 5) {
                        //0,1,2,3,4,5 : 7 => you win this game
                        //set tiebreak point
                        new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                        new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                        //set point clean
                        new_state.setSet_point_up(current_set, (byte)0);
                        new_state.setSet_point_down(current_set, (byte)0);
                        //add to game
                        game = new_state.getSet_game_down(current_set);
                        game++;
                        new_state.setSet_game_down(current_set, game);
                        //change serve
                        //if (new_state.isServe()) {
                        if (am_I_Tiebreak_First_Serve) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }
                        //leave tiebreak;
                        new_state.setInTiebreak(false);

                    } else if (new_state.getSet_point_up(current_set) >= 4 &&
                            new_state.getSet_point_down(current_set) >= 4 &&
                            (new_state.getSet_point_up(current_set) - new_state.getSet_point_down(current_set)) == 2) {
                        //8:6, 9:7, 10:8.... => oppt win this game
                        //set tiebreak point
                        new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                        new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                        //set point clean
                        new_state.setSet_point_up(current_set, (byte)0);
                        new_state.setSet_point_down(current_set, (byte)0);
                        //add to game
                        game = new_state.getSet_game_up(current_set);
                        game++;
                        new_state.setSet_game_up(current_set, game);
                        //change serve
                        //if (new_state.isServe()) {
                        if (am_I_Tiebreak_First_Serve) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }

                        //leave tiebreak;
                        new_state.setInTiebreak(false);

                    } else if (new_state.getSet_point_up(current_set) >= 4 &&
                            new_state.getSet_point_down(current_set) >= 4 &&
                            (new_state.getSet_point_down(current_set) - new_state.getSet_point_up(current_set)) == 2) {
                        //6:8, 7:9, 8:10.... => you win this game
                        //set tiebreak point
                        new_state.setSet_tiebreak_point_up(current_set, new_state.getSet_point_up(current_set));
                        new_state.setSet_tiebreak_point_down(current_set, new_state.getSet_point_down(current_set));
                        //set point clean
                        new_state.setSet_point_up(current_set, (byte)0);
                        new_state.setSet_point_down(current_set, (byte)0);
                        //add to game
                        game = new_state.getSet_game_down(current_set);
                        game++;
                        new_state.setSet_game_down(current_set, game);
                        //change serve
                        //if (new_state.isServe()) {
                        if (am_I_Tiebreak_First_Serve) {
                            new_state.setServe(false);
                        } else {
                            new_state.setServe(true);
                        }

                        //leave tiebreak;
                        new_state.setInTiebreak(false);

                    } else {
                        Log.d(TAG, "Other tie break, isServe = "+new_state.isServe());



                        byte plus = (byte) (new_state.getSet_point_up(current_set)+new_state.getSet_point_down(current_set));

                        if (plus%2 == 1) {
                            //change serve
                            Log.d(TAG, "==>Points plus become odd, change serve!");
                            if (new_state.isServe()) {
                                new_state.setServe(false);
                            } else {
                                new_state.setServe(true);
                            }
                        }
                    }
                }
            }






        } else { //not in tiebreak;

            Log.d(TAG, "[Not in Tiebreak]");
            if (deuce.equals("0")) { //use deuce
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) ==4) { //40A:40A => 40:40
                    new_state.setSet_point_up(current_set, (byte)0x03);
                    new_state.setSet_point_down(current_set, (byte)0x03);

                    //if (check_voice_file_exist("Allegro_from_Duet_in_C_Major.mp3")) {

                    //    VoicePlay.audioPlayer(context, "Allegro_from_Duet_in_C_Major.mp3");
                    //}

                } else if (new_state.getSet_point_up(current_set) == 5 &&
                        new_state.getSet_point_down(current_set) == 3) { //40A+ : 40 => oppt win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }

                } else if (new_state.getSet_point_up(current_set) == 3 &&
                        new_state.getSet_point_down(current_set) == 5) { //40 : 40A+ => you win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                } else if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 2) { //40A : 0, 40A : 15, 40A : 30 => oppt win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                } else if (new_state.getSet_point_up(current_set) <=2 &&
                        new_state.getSet_point_down(current_set) == 4) { //0 : 40A, 15 : 40A, 30: 40A => you win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                }
                else {

                    Log.d(TAG, "[points change without arrange]");
                }
            } else { //use deciding point
                byte game;
                if (new_state.getSet_point_up(current_set) == 4 &&
                        new_state.getSet_point_down(current_set) <= 3) { //40A : 40,30,15,0 => oppt win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_up(current_set);
                    game++;
                    new_state.setSet_game_up(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                } else if (new_state.getSet_point_up(current_set) <= 3 &&
                        new_state.getSet_point_down(current_set) == 4) { //40,30,15,0 : 40A => you win this game
                    //set point clean
                    new_state.setSet_point_up(current_set, (byte)0);
                    new_state.setSet_point_down(current_set, (byte)0);
                    //add to game
                    game = new_state.getSet_game_down(current_set);
                    game++;
                    new_state.setSet_game_down(current_set, game);
                    //change serve
                    if (new_state.isServe()) {
                        new_state.setServe(false);
                    } else {
                        new_state.setServe(true);
                    }
                } else {

                    Log.d(TAG, "[points change without arrange]");
                }
            }
        }

        Log.d(TAG, "current_set = " + current_set);
        Log.d(TAG, "[Check point End]");

    }

    private void checkGames(State new_state) {

        Log.d(TAG, "[Check Games Start]");
        byte current_set = new_state.getCurrent_set();
        byte setsWinUp = new_state.getSetsUp();
        byte setsWinDown = new_state.getSetsDown();
        if (tiebreak.equals("0")) { //use tibreak

            Log.d(TAG, "[Use Tiebreak]");

            if (games.equals("0") || tiebreak.equals("2")) { //6 game in a set
                Log.d(TAG, "[6 game in a set start]");

                if (is_In_SuperTiebreak) {
                    if (new_state.getSet_game_up(current_set) == 1 &&
                            new_state.getSet_game_down(current_set) == 0) { // 7:5 => oppt win this set
                        //set sets win
                        setsWinUp++;
                        new_state.setSetsUp(setsWinUp);
                        checkSets(new_state);
                    } else if (new_state.getSet_game_up(current_set) == 0 &&
                            new_state.getSet_game_down(current_set) == 1) { // 5:7 => you win this set
                        //set sets win
                        setsWinDown++;
                        new_state.setSetsDown(setsWinDown);
                        checkSets(new_state);
                    } else {
                        Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));

                    }
                } else {
                    if (new_state.getSet_game_up(current_set) == 6 &&
                            new_state.getSet_game_down(current_set) == 6) {
                        new_state.setInTiebreak(true); //into tiebreak;

                        //am I(down) first serve?
                        if (new_state.isServe()) {
                            am_I_Tiebreak_First_Serve = true;
                        } else {
                            am_I_Tiebreak_First_Serve = false;
                        }
                    } else if (new_state.getSet_game_up(current_set) == 7 &&
                            new_state.getSet_game_down(current_set) == 5) { // 7:5 => oppt win this set
                        //set sets win
                        setsWinUp++;
                        new_state.setSetsUp(setsWinUp);
                        checkSets(new_state);
                    } else if (new_state.getSet_game_up(current_set) == 5 &&
                            new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                        //set sets win
                        setsWinDown++;
                        new_state.setSetsDown(setsWinDown);
                        checkSets(new_state);
                    } else if (new_state.getSet_game_up(current_set) == 7 &&
                            new_state.getSet_game_down(current_set) == 6) { // 7:6 => oppt win this set
                        //set sets win
                        setsWinUp++;
                        new_state.setSetsUp(setsWinUp);
                        checkSets(new_state);
                    } else if (new_state.getSet_game_up(current_set) == 6 &&
                            new_state.getSet_game_down(current_set) == 7) { // 5:7 => you win this set
                        //set sets win
                        setsWinDown++;
                        new_state.setSetsDown(setsWinDown);
                        checkSets(new_state);
                    } else if (new_state.getSet_game_up(current_set) == 6 &&
                            new_state.getSet_game_down(current_set) <=4 ) { // 6:0,1,2,3,4 => oppt win this set
                        //set sets win
                        setsWinUp++;
                        new_state.setSetsUp(setsWinUp);
                        checkSets(new_state);
                    } else if (new_state.getSet_game_up(current_set) <= 4 &&
                            new_state.getSet_game_down(current_set) == 6) { // 0,1,2,3,4:6 => you win this set
                        //set sets win
                        setsWinDown++;
                        new_state.setSetsDown(setsWinDown);
                        checkSets(new_state);
                    } else {
                        Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));

                    }
                }


            } else {
                Log.d(TAG, "[4 game in a set start]");

                if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) == 4) {
                    new_state.setInTiebreak(true); //into tiebreak;

                    //am I(down) first serve?
                    if (new_state.isServe()) {
                        am_I_Tiebreak_First_Serve = true;
                    } else {
                        am_I_Tiebreak_First_Serve = false;
                    }

                } else if (new_state.getSet_game_up(current_set) == 5 &&
                        new_state.getSet_game_down(current_set) == 3) { // 5:3 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                } else if (new_state.getSet_game_up(current_set) == 3 &&
                        new_state.getSet_game_down(current_set) == 5) { // 3:5 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                } else if (new_state.getSet_game_up(current_set) == 5 &&
                        new_state.getSet_game_down(current_set) == 4) { // 5:4 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                } else if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) == 5) { // 4:5 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                } else if (new_state.getSet_game_up(current_set) == 4 &&
                        new_state.getSet_game_down(current_set) <=2 ) { // 4:0,1,2 => oppt win this set
                    //set sets win
                    setsWinUp++;
                    new_state.setSetsUp(setsWinUp);
                    checkSets(new_state);

                } else if (new_state.getSet_game_up(current_set) <= 2 &&
                        new_state.getSet_game_down(current_set) == 4) { // 0,1,2:6 => you win this set
                    //set sets win
                    setsWinDown++;
                    new_state.setSetsDown(setsWinDown);
                    checkSets(new_state);

                } else {
                    Log.d(TAG, "set "+current_set+" game: up = "+new_state.getSet_game_up(current_set)+", down = "+new_state.getSet_game_down(current_set));

                }
                Log.d(TAG, "[4 game in a set end]");
            }


        } else {
            if (new_state.getSet_game_up(current_set) == 6 &&
                    new_state.getSet_game_down(current_set) <= 5) { // 6:5 => oppt win this set
                //set sets win
                setsWinUp++;
                new_state.setSetsUp(setsWinUp);
                checkSets(new_state);
            } else if (new_state.getSet_game_up(current_set) <= 5 &&
                    new_state.getSet_game_down(current_set) == 6) { // 5:6 => you win this set
                //set sets win
                setsWinDown++;
                new_state.setSetsDown(setsWinDown);
                checkSets(new_state);
            }
        }


        Log.d(TAG, "[Check Games End]");
    }

    private void checkSets(State new_state) {

        Log.d(TAG, "[Check sets Start]");
        //check if the game is over
        byte current_set = new_state.getCurrent_set();
        byte setsWinUp = new_state.getSetsUp();
        byte setsWinDown = new_state.getSetsDown();

        switch (set) {
            case "0":
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    is_fisnish = true;
                } else {
                    is_fisnish = false;
                    endTime = 0;
                }
                break;
            case "1":
                if (setsWinUp == 2 || setsWinDown == 2) {
                    new_state.setFinish(true);
                    is_fisnish = true;
                } else { // new set

                    if (current_set == 2) {
                        if (tiebreak.equals("2")) { //super tiebreak
                            new_state.setInTiebreak(true);
                            is_In_SuperTiebreak = true;
                        } else {
                            new_state.setInTiebreak(false);
                            is_In_SuperTiebreak = false;
                        }
                    } else {
                        new_state.setInTiebreak(false);
                        is_In_SuperTiebreak = false;
                    }


                    current_set++;
                    new_state.setCurrent_set(current_set);
                    is_fisnish = false;
                    endTime = 0;
                }
                break;
            case "2":
                if (setsWinUp == 3 || setsWinDown == 3) {
                    new_state.setFinish(true);
                    is_fisnish = true;
                } else { // new set

                    if (current_set == 4) {
                        if (tiebreak.equals("2")) { //super tiebreak
                            new_state.setInTiebreak(true);
                            is_In_SuperTiebreak = true;
                        } else {
                            new_state.setInTiebreak(false);
                            is_In_SuperTiebreak = false;
                        }
                    } else {
                        new_state.setInTiebreak(false);
                        is_In_SuperTiebreak = false;
                    }

                    current_set++;
                    new_state.setCurrent_set(current_set);
                    is_fisnish = false;
                    endTime = 0;
                }
                break;
            default:
                if (setsWinUp == 1 || setsWinDown == 1) {
                    new_state.setFinish(true);
                    is_fisnish = true;
                } else {
                    is_fisnish = false;
                    endTime = 0;
                }
                break;
        }



        Log.d(TAG, "[Check sets End]");
    }



    /*public void toast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }*/



    private Runnable updateTimer = new Runnable() {
        public void run() {
            //final TextView time = (TextView) findViewById(R.id.currentTime);
            NumberFormat f = new DecimalFormat("00");
            //Long spentTime = System.currentTimeMillis() - startTime;
            //計算目前已過分鐘數

            //計算目前已過秒數
            //Long seconds = (time_use) % 60;
            //time.setText(minius+":"+seconds);

            handler.postDelayed(this, 1000);
            /*if (!is_fisnish) {
                if (startTime == 0)
                    spentTime = 0L;
                else {
                    spentTime = System.currentTimeMillis() - startTime;
                }
            } else {
                spentTime = endTime - startTime;
            }*/

            spentTime = System.currentTimeMillis() - startTime;
            if (endTime > 0)
                spentTime = endTime - startTime;
            //    time_use++;
            time_use = spentTime;


            //Log.d(TAG, "startTime = " +startTime+ " endTime = "+endTime+" spentTime = "+spentTime);

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            sdf.setTimeZone(tz);//set time zone.
            Date netDate = new Date(System.currentTimeMillis());

            Long hour = (spentTime/1000)/3600;
            Long min = (spentTime/1000)%3600/60;
            Long sec = (spentTime/1000)%60;
            //textCurrentTime.setText(sdf.format(netDate));
            //textGameTime.setText(f.format(hour)+":"+f.format(min)+":"+f.format(sec));
        }
    };
}
