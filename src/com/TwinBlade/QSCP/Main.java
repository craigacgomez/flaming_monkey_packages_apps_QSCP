package com.TwinBlade.QSCP;

import java.io.OutputStreamWriter;
import java.lang.Runtime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

@SuppressWarnings("deprecation")
public class Main extends PreferenceActivity implements OnPreferenceChangeListener {

    private static final String TAG = "QuickSettingsControlPanel";

    private Preference mSettings, mBrightness, mSeekbar, mBattery, mRotation, mAirplane, mWifi, mMobileData, mBluetooth, mScreenOff, mRinger, mLocation, mWifiAp, mTorch, mAlarm, mWifiDisplay, mIme;

    private final String QS_SETTINGS = "QS_SETTINGS";
    private final String QS_SEEKBAR = "QS_SEEKBAR";
    private final String QS_BATTERY = "QS_BATTERY";
    private final String QS_ROTATION = "QS_ROTATION";
    private final String QS_AIRPLANE = "QS_AIRPLANE";
    private final String QS_WIFI = "QS_WIFI";
    private final String QS_WIFI_AP = "QS_WIFI_AP";
    private final String QS_MOBILE_DATA = "QS_MOBILE_DATA";
    private final String QS_BLUETOOTH = "QS_BLUETOOTH";
    private final String QS_SCREEN_OFF = "QS_SCREEN_OFF";
    private final String QS_LOCATION = "QS_LOCATION";
    private final String QS_RINGER = "QS_RINGER";
    private final String QS_TORCH = "QS_TORCH";
    private final String QS_BRIGHTNESS = "QS_BRIGHTNESS";
    private final String QS_ENABLED_TILES = "QS_ENABLED_TILES";
    private final String QS_COLUMNS = "QS_COLUMNS";
    private final String QS_ALARM = "QS_ALARM";
    private final String QS_WIFI_DISPLAY = "QS_WIFI_DISPLAY";
    private final String QS_IME = "QS_IME";
    private final String APPLY = "QS_APPLY";

    private List<String> mTilesList = Arrays.asList(QS_SETTINGS, QS_BATTERY, QS_BRIGHTNESS, QS_RINGER, QS_WIFI, QS_BLUETOOTH, QS_LOCATION);

    private ArrayList<String> mTilesOrderedList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadSettings();
        addPreferencesFromResource(R.xml.main);

        mSettings = findPreference(QS_SETTINGS);
        mBrightness = findPreference(QS_BRIGHTNESS);
        mSeekbar = findPreference(QS_SEEKBAR);
        mBattery = findPreference(QS_BATTERY);
        mRotation = findPreference(QS_ROTATION);
        mAirplane = findPreference(QS_AIRPLANE);
        mWifi = findPreference(QS_WIFI);
        //TODO: Remove dynamically
        //mMobileData = findPreference(QS_MOBILE_DATA);
        mBluetooth = findPreference(QS_BLUETOOTH);
        mScreenOff = findPreference(QS_SCREEN_OFF);
        mRinger = findPreference(QS_RINGER);
        mLocation = findPreference(QS_LOCATION);
        //TODO: Remove dynamically
        //mWifiAp = findPreference(QS_WIFI_AP);
        mTorch = findPreference(QS_TORCH);
        mAlarm = findPreference(QS_ALARM);
        mWifiDisplay = findPreference(QS_WIFI_DISPLAY);
        mIme = findPreference(QS_IME);

        mSettings.setOnPreferenceChangeListener(this);
        mBrightness.setOnPreferenceChangeListener(this);
        mSeekbar.setOnPreferenceChangeListener(this);
        mBattery.setOnPreferenceChangeListener(this);
        mRotation.setOnPreferenceChangeListener(this);
        mAirplane.setOnPreferenceChangeListener(this);
        mWifi.setOnPreferenceChangeListener(this);
        //TODO: Remove dynamically
        //mMobileData.setOnPreferenceChangeListener(this);
        mBluetooth.setOnPreferenceChangeListener(this);
        mScreenOff.setOnPreferenceChangeListener(this);
        mRinger.setOnPreferenceChangeListener(this);
        mLocation.setOnPreferenceChangeListener(this);
        //TODO: Remove dynamically
        //mWifiAp.setOnPreferenceChangeListener(this);
        mTorch.setOnPreferenceChangeListener(this);
        mAlarm.setOnPreferenceChangeListener(this);
        mWifiDisplay.setOnPreferenceChangeListener(this);
        mIme.setOnPreferenceChangeListener(this);

        Preference mApply = findPreference(APPLY);
        mApply.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                writeSettings();
                try {
                    ProcessBuilder pb = new ProcessBuilder("su", "-c", "/system/bin/sh");
                    Process p = pb.start();
                    OutputStreamWriter osw = new OutputStreamWriter(p.getOutputStream());
                    osw.write("killall com.android.systemui" + "\n");
                    osw.write("\nexit\n");
                    osw.flush();
                    osw.close();
                    int rc = p.waitFor();
                    if (rc != 0) {
                        Log.e(TAG, "Non-zero response. Error restarting SystemUI.");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error restarting SystemUI", e);
                }
                return true;
            }
        });
    }

    private void loadSettings() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tilesListString = Settings.System.getString(getContentResolver(), QS_ENABLED_TILES);
        List<String> tilesList = new ArrayList<String>();
        if (tilesListString != null) {
            tilesList = Arrays.asList(tilesListString.split(";"));
        } else {
            tilesList = mTilesList;
        }
        for (String tile : tilesList) {
            mTilesOrderedList.add(tile);
        }
        mSharedPreferences.edit().putBoolean(QS_COLUMNS, Settings.System.getInt(getContentResolver(), QS_COLUMNS, 4) == 3);
    }

    private void writeSettings() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String tile : mTilesOrderedList) {
            stringBuilder.append(tile);
            stringBuilder.append(";");
        }
        Settings.System.putString(getContentResolver(), QS_ENABLED_TILES, stringBuilder.toString());
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Settings.System.putInt(getContentResolver(), QS_COLUMNS, mSharedPreferences.getBoolean(QS_COLUMNS, true) ? 3 : 4);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if ((Boolean) newValue) {
            mTilesOrderedList.add(preference.getKey().toString());
        } else {
            mTilesOrderedList.remove(preference.getKey().toString());
        }
        return true;
    }
}
