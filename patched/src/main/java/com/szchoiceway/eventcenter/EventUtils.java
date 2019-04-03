package com.szchoiceway.eventcenter;

import android.util.Log;
import lanchon.dexpatcher.annotation.DexEdit;
import lanchon.dexpatcher.annotation.DexIgnore;
import lanchon.dexpatcher.annotation.DexReplace;
import java.io.IOException;

@DexEdit
public class EventUtils {

    @DexReplace
    public static void killProcess(String packageName) {
      Log.i("killProcess", "closeApplication patched " + packageName);
    }

    @DexIgnore
    public enum eSrcMode {

    }

}
