package com.szchoiceway.eventcenter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;

import java.util.HashMap;

import lanchon.dexpatcher.annotation.DexAction;
import lanchon.dexpatcher.annotation.DexEdit;
import lanchon.dexpatcher.annotation.DexIgnore;
import lanchon.dexpatcher.annotation.DexReplace;

@DexEdit
public class EventService extends Service implements View.OnLongClickListener, View.OnClickListener {

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

    @DexIgnore
    private class SendThread {

    }

    @DexIgnore
    static class ServiceStub {

    }

    @DexEdit(defaultAction = DexAction.IGNORE)
    private class UNavSndObserver extends android.os.UEventObserver {

        @DexReplace
        @Override
        public void onUEvent(UEvent event) {

        }
    }

    @DexIgnore
    public void SendBlackState(boolean bBlack) {

    }

    @DexIgnore
    public void SendBLVal(byte iBLVal, byte iNBLVal) {

    }
}