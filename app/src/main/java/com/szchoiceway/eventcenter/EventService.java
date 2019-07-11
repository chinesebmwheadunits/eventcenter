package com.szchoiceway.eventcenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.chinesebmwheadunits.eventcenter.EventCenterHttpServer;
import io.chinesebmwheadunits.eventcenter.NavigationAppsHandler;
import lanchon.dexpatcher.annotation.DexAction;
import lanchon.dexpatcher.annotation.DexAdd;
import lanchon.dexpatcher.annotation.DexEdit;
import lanchon.dexpatcher.annotation.DexIgnore;
import lanchon.dexpatcher.annotation.DexWrap;

import static io.chinesebmwheadunits.eventcenter.SettingsHandler.IO_C_BMW_HU_TELEPHONE_MUTE_ENABLED;

@DexEdit
public class EventService extends Service implements View.OnLongClickListener, View.OnClickListener {

    @DexIgnore
    private String ksw_map_apk_list_data_local_path;

    @DexIgnore
    public EventService()
    {

    }

    @DexAdd
    private EventCenterHttpServer eventCenterHttpServer = null;

    @DexIgnore
    public SysProviderOpt mSysProviderOpt;

    @DexIgnore
    private String[] mapApkLst;

    @DexAdd
    public String[] getMapApkList()
    {
        return this.mapApkLst;
    }

    @DexIgnore
    private void ksw_set_mapApkLst() {
    }

    @DexIgnore
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @DexIgnore
    @Override
    public void onClick(View v) {

    }

    @DexIgnore
    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @DexAdd
    /**
     * Write an array of strings to the APK list.
     */
    public void writeApkList(ArrayList<String> navigationAppsToWrite) {
        File file_mapApkLst = new File(this.ksw_map_apk_list_data_local_path);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file_mapApkLst))) {
            for (String line : navigationAppsToWrite) {
                writer.append(line);
                writer.newLine();
            }
            this.mapApkLst = (String[]) navigationAppsToWrite.toArray();
        } catch (IOException e) {
            Log.e("EventService", "Error writing APK list ", e);
        }
    }

    @DexIgnore
    private class SendThread {

    }

    @DexIgnore
    static class ServiceStub {

    }

    @DexEdit(defaultAction = DexAction.IGNORE)
    private class UNavSndObserver extends android.os.UEventObserver {

        @DexWrap
        @Override
        public void onUEvent(UEvent event) {
            boolean telephoneMuteEnabled = true;

            try
            {
                if (EventService.this.mSysProviderOpt != null)
                {
                    telephoneMuteEnabled = EventService.this.mSysProviderOpt.getRecordBoolean(IO_C_BMW_HU_TELEPHONE_MUTE_ENABLED, telephoneMuteEnabled);
                }
            } catch (Exception e)
            {
            }

            if (telephoneMuteEnabled )
            {
                onUEvent(event);
            } else {
                Log.i("telephoneMute", "UNavSndObserver onUEvent disabled ");
            }
        }
    }

    @DexIgnore
    public void SendBlackState(boolean bBlack) {

    }

    @DexIgnore
    public void SendBLVal(byte iBLVal, byte iNBLVal) {

    }

    @DexWrap
    private void initSysEventState() {
        initSysEventState();
        EventUtils.sysProviderOpt = this.mSysProviderOpt;
        if (eventCenterHttpServer != null)
        {
            eventCenterHttpServer.stop();
        }
        try {
            eventCenterHttpServer = new EventCenterHttpServer(this);
        } catch (IOException ioException)
        {
            Log.e("httpServer", "unable to start HTTP Server ", ioException);
        }
    }
}