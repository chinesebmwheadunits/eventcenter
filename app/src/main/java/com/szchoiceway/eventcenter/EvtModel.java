package com.szchoiceway.eventcenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import lanchon.dexpatcher.annotation.DexEdit;
import lanchon.dexpatcher.annotation.DexIgnore;
import lanchon.dexpatcher.annotation.DexWrap;

import static io.chinesebmwheadunits.eventcenter.SettingsHandler.IO_C_BMW_HU_BRIGHTNESS_INTENTS_ENABLED;

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
        boolean brightnessIntentsEnabled = true;

        try
        {
            if (mContext.mSysProviderOpt != null)
            {
                brightnessIntentsEnabled = mContext.mSysProviderOpt.getRecordBoolean(IO_C_BMW_HU_BRIGHTNESS_INTENTS_ENABLED, brightnessIntentsEnabled);
            }
        } catch (Exception e)
        {
        }

        if (brightnessIntentsEnabled )
        {
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
        }
        onReceive(context, intent);
    }
}
