package com.danny_mcoy.simplecommad.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.danny_mcoy.simplecommad.R;
import com.danny_mcoy.simplecommad.extra.Params;

/**
 * Created by Danny_姜新星 on 5/22/2015.
 */
public class Storage {

    public static void saveDeviceId(Context context, String deviceId) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(Params.Persistent.PARAM_DEVICE_ID, deviceId);
        editor.apply();
    }

    public static String getDeviceId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Params.Persistent.PARAM_DEVICE_ID, "");
    }

    public static void invalidAuthToken(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(Params.Persistent.PARAM_TOKEN);
        editor.apply();
    }

    public static void resetSettings(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(context.getString(R.string.preference_key_wifi_only));
        editor.apply();
    }

    public static void saveAuthToken(Context context, final String token) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(Params.Persistent.PARAM_TOKEN, token);
        editor.apply();
    }

    public static String getAuthToken(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(Params.Persistent.PARAM_TOKEN, "");
    }

    public static boolean isAlreadyLogin(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.contains(Params.Persistent.PARAM_TOKEN);
    }

    public static void saveLastMediaSync(Context context, final long syncTime) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(Params.Persistent.PARAM_MEDIA_SYNC, syncTime);
        editor.apply();
    }

    public static long getLastMediaSync(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getLong(Params.Persistent.PARAM_MEDIA_SYNC, 0);
    }

    public static void saveSchoolCode(Context context, String schoolCode) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(Params.Persistent.PARAM_SCHOOL_CODE, schoolCode);
        editor.apply();
    }

    public static String getSchoolCode(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(Params.Persistent.PARAM_SCHOOL_CODE, "");
    }

    public static void saveAccount(Context context, String account) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(Params.Persistent.PARAM_ACCOUNT, account);
        editor.apply();
    }

    public static String getAccount(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(Params.Persistent.PARAM_ACCOUNT, "");
    }

    public static void saveFirstLaunch(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(Params.Persistent.PARAM_FIRST_LAUNCH, true);
        editor.apply();
    }

    public static boolean isFirstLaunch(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(Params.Persistent.PARAM_FIRST_LAUNCH, false);
    }

    public static void saveShowWarning(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(Params.Persistent.PARAM_SHOW_WARNING, true);
        editor.apply();
    }

    public static boolean isWarningShown(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(Params.Persistent.PARAM_SHOW_WARNING, false);
    }

    public static boolean isWifiOnly(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.preference_key_wifi_only), false);
    }


    public static void saveLocation(Context context, boolean isChinaRegion) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(Params.Persistent.PARAM_LOCATION, isChinaRegion);
        editor.apply();
    }

    public static boolean isChinaLocation(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(Params.Persistent.PARAM_LOCATION, false);
    }

    public static void saveShowcaseFirstLaunch(Context context, boolean isFirstLaunch) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(Params.Persistent.PARAM_SHOWCASE, isFirstLaunch);
        editor.apply();
    }

    public static boolean isShowcaseFirstLaunch(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(Params.Persistent.PARAM_SHOWCASE, false);
    }
}
