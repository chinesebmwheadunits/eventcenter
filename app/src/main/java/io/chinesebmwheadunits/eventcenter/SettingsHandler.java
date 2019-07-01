package io.chinesebmwheadunits.eventcenter;

import android.util.JsonReader;
import android.util.Log;

import com.google.gson.Gson;
import com.szchoiceway.eventcenter.SysProviderOpt;

import java.util.HashMap;
import java.util.Map;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Handles the get and put requests for the settings route.
 */
public class SettingsHandler extends RouterNanoHTTPD.DefaultHandler {

    /**
     * Reference tot the options system provider.
     */
    private SysProviderOpt sysProviderOpt;

    /**
     * String identifier for task killer setting.
     */
    public static final String IO_C_BMW_HU_TASK_KILLER_ENABLED = "IO_C_BMW_HU_TASK_KILLER_ENABLED";

    /**
     * String identifier for telephone mute setting.
     */
    public static final String IO_C_BMW_HU_TELEPHONE_MUTE_ENABLED = "IO_C_BMW_HU_TELEPHONE_MUTE_ENABLED";

    /**
     * String identifier for brightness intents setting.
     */
    public static final String IO_C_BMW_HU_BRIGHTNESS_INTENTS_ENABLED = "IO_C_BMW_HU_BRIGHTNESS_INTENTS_ENABLED";

    /**
     * Settings model for JSON transfers.
     */
    private class Settings
    {
        public boolean taskKillerEnabled = false;
        public boolean telephoneMuteEnabled = true;
        public boolean brightnessIntentsEnabled = true;
    }

    /**
     * Gets the default response body for this route handler.
     * @return The response body.
     */
    @Override
    public String getText() {
        Gson gson = new Gson();
        Settings settings = new Settings();

        settings.taskKillerEnabled = sysProviderOpt.getRecordBoolean(IO_C_BMW_HU_TASK_KILLER_ENABLED, settings.taskKillerEnabled);
        settings.telephoneMuteEnabled = sysProviderOpt.getRecordBoolean(IO_C_BMW_HU_TELEPHONE_MUTE_ENABLED, settings.telephoneMuteEnabled);
        settings.brightnessIntentsEnabled = sysProviderOpt.getRecordBoolean(IO_C_BMW_HU_BRIGHTNESS_INTENTS_ENABLED, settings.brightnessIntentsEnabled);

        return gson.toJson(settings);
    }

    /**
     * Mime type for the response body.
     * @return the mime type
     */
    @Override
    public String getMimeType() {
        return "application/json";
    }

    /**
     * Response status.
     * @return the status.
     */
    @Override
    public NanoHTTPD.Response.IStatus getStatus() {
        return NanoHTTPD.Response.Status.OK;
    }

    /**
     * Handles the response for the GET request.
     * @param uriResource Resource for the URI
     * @param urlParams The parameters in the URI
     * @param session Session stuff.
     * @return The response body.
     */
    @Override
    public NanoHTTPD.Response get(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        sysProviderOpt = uriResource.initParameter(SysProviderOpt.class);
        return super.get(uriResource, urlParams, session);
    }

    /**
     * Handles the response for the POST request.
     * @param uriResource Resource for the URI
     * @param urlParams The parameters in the URI
     * @param session Session stuff.
     * @return The response body
     */
    @Override
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        sysProviderOpt = uriResource.initParameter(SysProviderOpt.class);
        try {
            Gson gson = new Gson();
            Map<String, String> files = new HashMap<String, String>();
            session.parseBody(files);
            String postData = files.get("postData");
            Log.i("postData", postData);
            Settings settings = gson.fromJson(postData, Settings.class);
            sysProviderOpt.updateRecord(IO_C_BMW_HU_TASK_KILLER_ENABLED, settings.taskKillerEnabled ? "1" : "0");
            sysProviderOpt.updateRecord(IO_C_BMW_HU_TELEPHONE_MUTE_ENABLED, settings.telephoneMuteEnabled ? "1" : "0");
            sysProviderOpt.updateRecord(IO_C_BMW_HU_BRIGHTNESS_INTENTS_ENABLED, settings.brightnessIntentsEnabled ? "1" : "0");
            return get(uriResource, urlParams, session);
        } catch (Exception e) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/html", "<html><body><h3>Error 400: Bad Request.</h3><p>" + e.getMessage() + "</p></body></html>");
        }


    }
}
