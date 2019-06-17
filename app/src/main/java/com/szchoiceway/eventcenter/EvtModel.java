package com.szchoiceway.eventcenter;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import lanchon.dexpatcher.annotation.DexEdit;
import lanchon.dexpatcher.annotation.DexIgnore;
import lanchon.dexpatcher.annotation.DexWrap;

@DexEdit
public class EvtModel extends BroadcastReceiver {

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
            if (StrTmp.equals("backlightPercent")) {
                int percentage = intent.getIntExtra("percentage", 100);

                this.mContext.SendBlackState(false);
                this.mContext.SendBLVal((byte) percentage, (byte) 0);
            } else {
                onReceive(context, intent);
            }
        }
    }
}
