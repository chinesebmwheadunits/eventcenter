package com.szchoiceway.eventcenter;

import android.util.Log;

import lanchon.dexpatcher.annotation.DexAdd;
import lanchon.dexpatcher.annotation.DexEdit;
import lanchon.dexpatcher.annotation.DexIgnore;
import lanchon.dexpatcher.annotation.DexWrap;

import java.io.IOException;

import static io.chinesebmwheadunits.eventcenter.SettingsHandler.IO_C_BMW_HU_TASK_KILLER_ENABLED;

@DexEdit
public class EventUtils {

    @DexAdd
    public static SysProviderOpt sysProviderOpt = null;

    @DexWrap
    public static void killProcess(String packageName) {
        boolean taskKillerEnabled = false;

        try
        {
            if (sysProviderOpt != null)
            {
                taskKillerEnabled = sysProviderOpt.getRecordBoolean(IO_C_BMW_HU_TASK_KILLER_ENABLED, taskKillerEnabled);
            }
        } catch (Exception e)
        {
        }

        if (taskKillerEnabled )
        {
            killProcess(packageName);
        } else {
            Log.i("killProcess", "closeApplication disabled " + packageName);
        }
    }

    @DexIgnore
    public enum eSrcMode {

    }

}
