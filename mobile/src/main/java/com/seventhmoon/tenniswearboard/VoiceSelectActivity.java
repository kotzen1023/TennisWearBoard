package com.seventhmoon.tenniswearboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.seventhmoon.tenniswearboard.Data.GridViewVoiceAdapter;
import com.seventhmoon.tenniswearboard.Data.ImageBuyItem;
import com.seventhmoon.tenniswearboard.util.IabBroadcastReceiver;
import com.seventhmoon.tenniswearboard.util.IabHelper;
import com.seventhmoon.tenniswearboard.util.IabResult;
import com.seventhmoon.tenniswearboard.util.Inventory;
import com.seventhmoon.tenniswearboard.util.Purchase;

import java.io.File;
import java.util.ArrayList;


public class VoiceSelectActivity extends AppCompatActivity implements IabBroadcastReceiver.IabBroadcastListener {
    private static final String TAG = VoiceSelectActivity.class.getName();

    public ArrayList<ImageBuyItem> imageBuyItems = new ArrayList<>();
    private LinearLayout linearLayout;

    private GridViewVoiceAdapter gridViewVoiceAdapter;
    private GridView gridView;

    private boolean [] selected;

    static SharedPreferences pref ;
    static SharedPreferences.Editor editor;
    private static final String FILE_NAME = "Preference";

    IabHelper mHelper;
    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;

    private static boolean debug = true;
    private Window window;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.voice_select);

        //in-app billing

        //1.prepare string
        String EncryptKey = "HyAqKiAPEiYrNQISAAIMFFEeYisiOSwjEikmESgyUCgoGiErEQ4oKyg0FikAZBwoMSJKYQYvNCwn" +
                "UR1KER4tFQhSLD1VG0M4ZyRWIyNTYQkkZAQLLSUQPzIKZC4OOgoNJCQkG1wBGy9OAwsxHwRVWhAk" +
                "ZgozGT0iIw08eCwNIioaOVA9JyReFzsxRzEvJxAueVEqPRMoYlheEQEFJCpWJwcPYx4BQwVSIBA9" +
                "ACpVLBA1KRkOHCggJT4CfC0dBAUxDF5VOBFCOV9bLAhOMlERMQ0aXgUyMBIuGigJEF0QBhEFMQER" +
                "IQ8OJiVaZAQLJjpRPBwMBVsyPTM8NV8AKigRGQ8CJz8iOBsIAVkTPi1dFFk2DwVUZDIQOyEoQzsx" +
                "HSdGYA02IlwEBx0lKiwsAyBcEgsceTkvLV8PAVAbYCwtWigtBjFZJlARESsMKglePFsEWz1KIyoR" +
                "BR4pMB5UCwMTPiZMMlhcERxeBS0TEQ03FBkDKipUHxwGBiceNSMMHjsuHTkgFigyKSs=";

        //let's decrypt this string
        String base64EncodedPublicKey = xorDecrypt(EncryptKey, "RichieShih");

        // Create the helper, passing it our context and the public key to verify signatures with
        Log.d(TAG, "Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().

                mBroadcastReceiver = new IabBroadcastReceiver(VoiceSelectActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");

                ArrayList<String> additionalSkuList = new ArrayList<>();
                additionalSkuList.add("sku_voice_gbr_man_1");
                //additionalSkuList.add("sku_theme_cat");
                //additionalSkuList.add("sku_theme_classic");

                //mHelper.queryInventoryAsync(mGotInventoryListener);
                mHelper.queryInventoryAsync(true, additionalSkuList, mGotInventoryListener);
            }
        });

        //btnBack = (Button) findViewById(R.id.btnThemeBack);
        //btnConfirm = (Button) findViewById(R.id.btnThemeConfirm);

        linearLayout = (LinearLayout) findViewById(R.id.voice_select_layout);

        window = getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        //for action bar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true);
            //actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ball_icon);
        }



        gridViewVoiceAdapter = new GridViewVoiceAdapter(this, R.layout.grid_item_voice_layout, getData());

        gridView = (GridView) findViewById(R.id.gridViewVoice);
        gridView.setAdapter(gridViewVoiceAdapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                //ImageBuyItem item = (ImageBuyItem) parent.getItemAtPosition(position);
                Log.i(TAG, "item" + position + " was select");

                for (int i = 0; i < gridView.getCount(); i++) {
                    ImageBuyItem item = (ImageBuyItem) parent.getItemAtPosition(i);

                    if (i == position) {
                        selected[position] = true;
                        item.setSelected(true);
                    } else {
                        selected[position] = false;
                        item.setSelected(false);
                    }

                }


                gridViewVoiceAdapter.notifyDataSetChanged();

                if (!debug) {

                    if (position > 0 && !imageBuyItems.get(position).getPurchased()) //buy items
                    {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(VoiceSelectActivity.this);
                        dialog.setTitle(getResources().getString(R.string.voice_change_ask_to_buy));
                        dialog.setIcon(R.drawable.ball_icon);
                        dialog.setCancelable(false);

                        dialog.setPositiveButton(getResources().getString(R.string.dialog_confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //buy it
                                do_buy_theme(position);
                            }
                        });

                        dialog.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //don't buy it
                            }
                        });
                        dialog.show();
                    }
                }
            }
        });

        selected = new boolean[gridView.getCount()];

    }

    private ArrayList<ImageBuyItem> getData() {
        //clear
        imageBuyItems.clear();
        Bitmap bitmap_simple = BitmapFactory.decodeResource(getResources(), R.drawable.ball_icon);

        imageBuyItems.add(new ImageBuyItem(bitmap_simple, "Default"));


        return imageBuyItems;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (mHelper != null) mHelper.dispose();
        mHelper = null;

        unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }

    public static String xorDecrypt(String input, String key) {
        byte[] inputBytes = Base64.decode(input, Base64.DEFAULT);
        //byte[] inputBytes = input.getBytes(Charset.forName("ISO-8859-1"));
        int inputSize = inputBytes.length;

        byte[] keyBytes = key.getBytes();
        int keySize = keyBytes.length - 1;

        byte[] outBytes = new byte[inputSize];
        for (int i = 0; i < inputSize; i++) {
            outBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keySize]);
        }
        return new String(outBytes);
    }

    public static String xorEncrypt(byte[] input, String key) {
        int inputSize = input.length;
        byte[] keyBytes = key.getBytes();
        int keySize = keyBytes.length - 1;

        byte[] outTempBytes = new byte[inputSize];
        for (int i = 0; i < inputSize; i++) {
            outTempBytes[i] = (byte) (input[i] ^ keyBytes[i % keySize]);
        }

        String output = Base64.encodeToString(outTempBytes, Base64.DEFAULT);

        return output;
    }

    @Override
    public void receivedBroadcast() {
        // Received a broadcast notification that the inventory of items has changed
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.e(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");


            if (inventory.getPurchase("sku_voice_gbr_man_1") != null) {

                //String theme_cat_price = inventory.getSkuDetails("sku_theme_cat").getPrice();
                //String theme_bear_price = inventory.getSkuDetails("sku_theme_bear").getPrice();
                Log.i(TAG, "sku_voice_gbr_man_1 = " + inventory.getSkuDetails("sku_voice_gbr_man_1").getPriceCurrencyCode() + " " +
                        inventory.getSkuDetails("sku_voice_gbr_man_1").getPrice() + "purchase " + inventory.getPurchase("sku_voice_gbr_man_1"));

            /*Log.i(TAG, "cat ="+inventory.getSkuDetails("sku_theme_cat").getPriceCurrencyCode()+ " "+
                    inventory.getSkuDetails("sku_theme_cat").getPrice()+ "purchase "+inventory.getPurchase("sku_theme_cat"));

            Log.i(TAG, "classic = " + inventory.getSkuDetails("sku_theme_classic").getPriceCurrencyCode() + " " +
                    inventory.getSkuDetails("sku_theme_classic").getPrice() + "purchase " + inventory.getPurchase("sku_theme_classic"));*/
                //theme bear
                if (inventory.getPurchase("sku_voice_gbr_man_1") == null) { //not buy yet
                    if (debug)
                        imageBuyItems.get(0).setTitle("GBR Man 1 Month");
                    else
                        imageBuyItems.get(0).setTitle("GBR Man 1 Month\n" + inventory.getSkuDetails("sku_voice_gbr_man_1").getPrice());
                    imageBuyItems.get(0).setPurchased(false);
                } else {
                    if (debug)
                        imageBuyItems.get(0).setTitle("GBR Man 1 Month");
                    else
                        imageBuyItems.get(0).setTitle("GBR Man 1 Month\n" + getResources().getString(R.string.voice_change_purchased));
                    imageBuyItems.get(0).setPurchased(true);
                }
            }
            //theme cat
            /*if(inventory.getPurchase("sku_theme_cat") == null) { //not buy yet
                if (debug)
                    imageBuyItems.get(2).setTitle("Cat");
                else
                    imageBuyItems.get(2).setTitle("Cat\n" + inventory.getSkuDetails("sku_theme_cat").getPrice());
                imageBuyItems.get(2).setPurchased(false);
            }
            else { //bought
                if (debug)
                    imageBuyItems.get(2).setTitle("Cat");
                else
                    imageBuyItems.get(2).setTitle("Cat\n" + getResources().getString(R.string.voice_change_purchased));
                imageBuyItems.get(2).setPurchased(true);
            }
            //theme classic
            if(inventory.getPurchase("sku_theme_classic") == null) {
                if (debug)
                    imageBuyItems.get(3).setTitle("Classic");
                else
                    imageBuyItems.get(3).setTitle("Classic\n" + inventory.getSkuDetails("sku_theme_classic").getPrice());
                imageBuyItems.get(3).setPurchased(false);
            } else {
                if (debug)
                    imageBuyItems.get(3).setTitle("Classic");
                else
                    imageBuyItems.get(3).setTitle("Classic\n" + getResources().getString(R.string.voice_change_purchased));
                imageBuyItems.get(3).setPurchased(true);
            }*/

            gridViewVoiceAdapter.notifyDataSetChanged();
            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            /*
            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            // First find out which subscription is auto renewing
            Purchase gasMonthly = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);
            Purchase gasYearly = inventory.getPurchase(SKU_INFINITE_GAS_YEARLY);
            if (gasMonthly != null && gasMonthly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_MONTHLY;
                mAutoRenewEnabled = true;
            } else if (gasYearly != null && gasYearly.isAutoRenewing()) {
                mInfiniteGasSku = SKU_INFINITE_GAS_YEARLY;
                mAutoRenewEnabled = true;
            } else {
                mInfiniteGasSku = "";
                mAutoRenewEnabled = false;
            }

            // The user is subscribed if either subscription exists, even if neither is auto
            // renewing
            mSubscribedToInfiniteGas = (gasMonthly != null && verifyDeveloperPayload(gasMonthly))
                    || (gasYearly != null && verifyDeveloperPayload(gasYearly));
            Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
                    + " infinite gas subscription.");
            if (mSubscribedToInfiniteGas) mTank = TANK_MAX;

            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
            Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_GAS), mConsumeFinishedListener);
                return;
            }

            updateUi();
            setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            */
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                Log.d(TAG, "Error purchasing: " + result);

                //editor.putInt("CurrentTheme", previous_theme);
                //editor.apply();
            }
            else if (purchase.getSku().equals("sku_voice_gbr_man_1")) {
                imageBuyItems.get(0).setPurchased(true);

            }
        }
    };

    protected void do_buy_theme(int position)
    {
        switch (position)
        {
            case 1: //bear
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSelectActivity.this, "sku_theme_cat", 10001, mPurchaseFinishedListener, null);
                break;
            case 2: //cat
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSelectActivity.this, "sku_theme_bear", 10001, mPurchaseFinishedListener, null);
                break;
            case 3: //classic
                if (mHelper != null)
                    mHelper.launchPurchaseFlow(VoiceSelectActivity.this, "sku_theme_classic", 10001, mPurchaseFinishedListener, null);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (mHelper != null && mHelper.handleActivityResult(requestCode, resultCode, data))
        {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
            return;
        }

        // not handled, so handle it ourselves (here's where you'd
        // perform any handling of activity results not related to in-app
        // billing...

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        //editor.putInt("CurrentTheme", previous_theme);
        //editor.apply();
        //Data.current_theme = previous_theme;

        //Intent intent = new Intent(VoiceSelectActivity.this, MainActivity.class);
        //startActivity(intent);
        finish();
    }
}
