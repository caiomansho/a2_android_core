package com.infoa2.core.widget.util

/**
 * Created by caio on 30/03/17.
 */

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.infoa2.core.R

import java.io.IOException
import java.io.InputStream
import java.util.Properties

object ConfigHelper {

    private val TAG = "Helper"

    fun getStringConfigValue(context: Context, name: String): String? {
        val resources = context.resources

        try {
            val rawResource = resources.openRawResource(R.raw.souconfig)
            val properties = Properties()
            properties.load(rawResource)
            return properties.getProperty(name)
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Unable to find the config file: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open config file.")
        }

        return null
    }

    fun getBooleanConfigValue(context: Context, name: String): Boolean? {
        val resources = context.resources

        try {
            val rawResource = resources.openRawResource(R.raw.souconfig)
            val properties = Properties()
            properties.load(rawResource)
            return java.lang.Boolean.valueOf(properties.getProperty(name))
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Unable to find the config file: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open config file.")
        } catch (e: Exception) {
            Log.d("Config Helper", e.message)
        }

        return null
    }

    fun getIntConfigValue(context: Context, name: String): Int? {

        val resources = context.resources

        try {
            val rawResource = resources.openRawResource(R.raw.souconfig)
            val properties = Properties()
            properties.load(rawResource)
            return Integer.valueOf(properties.getProperty(name))
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Unable to find the config file: " + e.message)
        } catch (e: IOException) {
            Log.e(TAG, "Failed to open config file.")
        } catch (e: Exception) {
            Log.d("Config Helper", e.message)
        }

        return null
    }
}
