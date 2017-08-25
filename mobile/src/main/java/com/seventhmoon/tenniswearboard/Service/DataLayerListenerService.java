package com.seventhmoon.tenniswearboard.Service;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import static com.seventhmoon.tenniswearboard.Data.Constants.ACTION.GET_SYNC_COMMAND;

public class DataLayerListenerService extends WearableListenerService {
    private static final String TAG = DataLayerListenerService.class.getName();
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for(DataEvent event : events) {
            final Uri uri = event.getDataItem().getUri();
            final String path = uri!=null ? uri.getPath() : null;
            Log.d(TAG, "path = "+path);
            if("/WEAR_COMMAND".equals(path)) {
                final DataMap map = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                // read your values from map:
                //int color = map.getInt("color");
                String command = map.getString("cmd");
                Long count = map.getLong("count");
                Log.e(TAG, "command = "+command+" count = "+count);

                Intent intent = new Intent();
                intent.setAction(GET_SYNC_COMMAND);
                intent.putExtra("command", command);
                sendBroadcast(intent);
            }
        }
    }
}
