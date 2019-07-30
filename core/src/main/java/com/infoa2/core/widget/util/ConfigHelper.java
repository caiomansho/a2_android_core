package com.infoa2.core.widget.util;

/**
 * Created by caio on 30/03/17.
 */

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import com.infoa2.core.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

    private static final String TAG = "Helper";

    public static String getStringConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.souconfig);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        }

        return null;
    }

    public static Boolean getBooleanConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.souconfig);
            Properties properties = new Properties();
            properties.load(rawResource);
            return Boolean.valueOf(properties.getProperty(name));
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        } catch (Exception e){
            Log.d("Config Helper", e.getMessage());
        }

        return null;
    }

    public static Integer getIntConfigValue(Context context, String name) {

        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.souconfig);
            Properties properties = new Properties();
            properties.load(rawResource);
            return Integer.valueOf(properties.getProperty(name));
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "Failed to open config file.");
        } catch (Exception e){
            Log.d("Config Helper", e.getMessage());
        }

        return null;
    }
}
