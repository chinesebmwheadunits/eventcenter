package com.szchoiceway.eventcenter;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import lanchon.dexpatcher.annotation.DexEdit;
import lanchon.dexpatcher.annotation.DexIgnore;
import lanchon.dexpatcher.annotation.DexWrap;

@DexEdit
public class EvtModel extends BroadcastReceiver {

    public static final String DIRECT_BRIGHTNESS = "com.choiceway.eventcenter.EventUtils.DIRECT_BRIGHTNESS";

    @DexIgnore
    protected EventService mContext = null;

    @DexIgnore
    public EvtModel(EventService context) {

    }

    @DexWrap
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals("com.android.quicksetting.BROADCAST")) {
            String StrTmp = intent.getStringExtra(NotificationCompat.CATEGORY_MESSAGE);
            Log.i("TAG","com.android.quicksetting.BROADCAST");
            if (StrTmp.equals("backlightPercent")) {
                Log.i("TAG","backlightPercent");
                int percentage = intent.getIntExtra("percentage", 100);
                Log.i("TAG","percentage " + percentage);

                this.mContext.SendBlackState(false);
                this.mContext.SendBLVal((byte) percentage, (byte) 0);
                return;
            }
        }
        onReceive(context, intent);
    }
}
