package io.chinesebmwheadunits.eventcenter;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.szchoiceway.eventcenter.EventService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;

/**
 * Handles the get and put requests for the navigation apps route.
 */
public class NavigationAppsHandler extends RouterNanoHTTPD.DefaultHandler {

    /**
     * Reference to the event service.
     */
    private EventService eventService;

    private class NavigationApp implements Comparable<NavigationApp> {
        public String name = "";
        @SerializedName("package")
        public String _package = "";
        public String icon = "";
        public Boolean enabled = false;

        @Override
        public int compareTo(NavigationApp navigationApp)
        {
            return this.name.compareTo(navigationApp.name);
        }
    }

    private boolean isNaviPkg(String pkgName) {

        String[] mapApkList = eventService.getMapApkList();

        if (mapApkList == null) {
            return false;
        }
        for (int loop = 0; loop < mapApkList.length; loop++) {
            if (pkgName.equals(mapApkList[loop])) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the default response body for this route handler.
     * @return The response body.
     */
    @Override
    public String getText() {
        Gson gson = new Gson();

        List<NavigationApp> navigationApps = new ArrayList<NavigationApp>();

        try {

            PackageManager packageManager = eventService.getApplicationContext().getPackageManager();
            List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
            for (int i = 0; i < applicationInfos.size(); i++) {
                ApplicationInfo applicationInfo = applicationInfos.get(i);

                NavigationApp navigationApp = new NavigationApp();

                navigationApp.name = applicationInfo.loadLabel(packageManager).toString();
                navigationApp._package = applicationInfo.packageName;
                navigationApp.icon = "/navigationapps/" + applicationInfo.packageName + "/icon";
                navigationApp.enabled = isNaviPkg(applicationInfo.packageName);

                navigationApps.add(navigationApp);
            }
        } catch (Exception e)
        {

        }

        Collections.sort(navigationApps);

        return gson.toJson(navigationApps);
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
        eventService = uriResource.initParameter(EventService.class);
        if (urlParams.containsKey("package_name"))
        {
            return getIcon(uriResource, urlParams.get("package_name"), session);
        }
        return super.get(uriResource, urlParams, session);
    }

    private NanoHTTPD.Response getIcon(RouterNanoHTTPD.UriResource uriResource, String packageName, NanoHTTPD.IHTTPSession session) {
        PackageManager packageManager = eventService.getApplicationContext().getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            Drawable packageIcon = packageInfo.applicationInfo.loadIcon(packageManager);
            Bitmap packageIconBitmap = drawableToBitmap(packageIcon);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                packageIconBitmap.compress(Bitmap.CompressFormat.PNG, 100,  outputStream);

                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray()))
                {
                    return NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK,"image/png", inputStream);
                }

            } catch (IOException e) {
                return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.INTERNAL_ERROR, "text/html", "<html><body><h3>Error 500: Internal server error.</h3><p>" + e.getMessage() + "</p></body></html>");
            }

        } catch (PackageManager.NameNotFoundException e) {

        }
        return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.NOT_FOUND, "text/html", "<html><body><h3>Error 404: Package not found.</h3><p>" + packageName + "</p></body></html>");
    }

    private static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public NanoHTTPD.Response post(RouterNanoHTTPD.UriResource uriResource, Map<String, String> urlParams, NanoHTTPD.IHTTPSession session) {
        eventService = uriResource.initParameter(EventService.class);
        try {
            Gson gson = new Gson();
            Map<String, String> files = new HashMap<String, String>();
            session.parseBody(files);
            String postData = files.get("postData");
            Log.i("postData", postData);
            NavigationApp[] navigationApps = gson.fromJson(postData, NavigationApp[].class);

            ArrayList<String> navigationAppsToWrite = new ArrayList<String>();

            for (NavigationApp navigationApp : navigationApps) {
                if (navigationApp.enabled)
                {
                    navigationAppsToWrite.add(navigationApp._package);
                }
            }

            this.eventService.writeApkList(navigationAppsToWrite);

            return get(uriResource, urlParams, session);
        } catch (Exception e) {
            return NanoHTTPD.newFixedLengthResponse(NanoHTTPD.Response.Status.BAD_REQUEST, "text/html", "<html><body><h3>Error 400: Bad Request.</h3><p>" + e.getMessage() + "</p></body></html>");
        }
    }
}
