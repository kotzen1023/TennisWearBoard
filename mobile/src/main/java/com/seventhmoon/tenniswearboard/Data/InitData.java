package com.seventhmoon.tenniswearboard.Data;

import android.bluetooth.BluetoothAdapter;

import com.google.android.gms.common.api.GoogleApiClient;


public class InitData {
    //public static boolean mode;
    /**
     * Name of the connected device
     */
    //public static String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    //private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    //public static StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    public static BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    //public static BluetoothService mChatService = null;
    public static boolean is_in_wear_mode = false;

    public static int current_voice = 0;

    public static GoogleApiClient mGoogleApiClient;

    public static boolean is_debug = false;
}
