package com.example.carlbai.gpshacktx2;

import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by carlbai on 10/18/14.
 */
public class ListenerServiceWatch extends WearableListenerService {

    private TextView mTextView;

    public void onMessageReceived(MessageEvent messageEvent){
        Log.v("Hello", "WATCH");
        Toast.makeText(this, messageEvent.getPath(), Toast.LENGTH_SHORT).show();
    }




}
