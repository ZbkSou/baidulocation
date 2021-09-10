package com.zbk.savedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ZBK on 2021-09-02.
 *
 * @function
 */
public class LocationBroadcast extends BroadcastReceiver {

    OnUpdateUI onUpdateUI;
    @Override
    public void onReceive(Context context, Intent intent) {
        double latitude = intent.getDoubleExtra("latitude",0);
        double longitude = intent.getDoubleExtra("longitude",0);
        String data = intent.getStringExtra("data");
        onUpdateUI.updateUI( latitude,  longitude,data);
    }

    public void SetOnUpdateUI(OnUpdateUI onUpdateUI){
        this.onUpdateUI = onUpdateUI;
    }

    public interface OnUpdateUI {
        void updateUI(double latitude, double longitude,String data);
    }
}