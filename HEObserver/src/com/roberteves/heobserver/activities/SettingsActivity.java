package com.roberteves.heobserver.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.roberteves.heobserver.R;
import com.roberteves.heobserver.core.SettingsManager;
import com.roberteves.heobserver.core.Util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import sheetrock.panda.changelog.ChangeLog;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity {
    private CheckBoxPreference localnews;
    private CheckBoxPreference sport;
    private CheckBoxPreference lifestyle;
    private CheckBoxPreference retail;
    private CheckBoxPreference weather;
    private CheckBoxPreference family;
    private CheckBoxPreference misc;
    private SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Util.LogMessage("SettingsActivity", "Activity Started");
        super.onCreate(savedInstanceState);
        settingsManager = new SettingsManager(this);
        addPreferencesFromResource(R.xml.preferences_layout);

        localnews = (CheckBoxPreference) getPreferenceManager().findPreference("feed_localnews");
        sport = (CheckBoxPreference) getPreferenceManager().findPreference("feed_sport");
        sport.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedSport((Boolean) newValue);
                return true;
            }
        });
        lifestyle = (CheckBoxPreference) getPreferenceManager().findPreference("feed_lifestyle");
        lifestyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedLifestyle((Boolean) newValue);
                return true;
            }
        });
        retail = (CheckBoxPreference) getPreferenceManager().findPreference("feed_retail");
        retail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedRetail((Boolean) newValue);
                return true;
            }
        });
        weather = (CheckBoxPreference) getPreferenceManager().findPreference("feed_weather");
        weather.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedWeather((Boolean) newValue);
                return true;
            }
        });
        family = (CheckBoxPreference) getPreferenceManager().findPreference("feed_family");
        family.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedFamily((Boolean) newValue);
                return true;
            }
        });
        misc = (CheckBoxPreference) getPreferenceManager().findPreference("feed_misc");
        misc.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedMisc((Boolean) newValue);
                return true;
            }
        });

        Preference reset = getPreferenceManager().findPreference("reset");
        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage("Reset all settings?")
                        .setTitle("Reset All?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        settingsManager.resetSettings();
                                        localnews.setChecked(true);
                                        sport.setChecked(settingsManager.getFeedSport());
                                        lifestyle.setChecked(settingsManager.getFeedLifestyle());
                                        retail.setChecked(settingsManager.getFeedRetail());
                                        weather.setChecked(settingsManager.getFeedWeather());
                                        family.setChecked(settingsManager.getFeedFamily());
                                        misc.setChecked(settingsManager.getFeedMisc());
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                    }
                                });

                builder.create().show();
                return true;
            }
        });

        Preference about = getPreferenceManager().findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.readme);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int i;
                try {
                    i = inputStream.read();
                    while (i != -1) {
                        byteArrayOutputStream.write(i);
                        i = inputStream.read();
                    }
                    inputStream.close();

                    Intent intent = new Intent(SettingsActivity.this, MarkdownActivity.class);
                    intent.putExtra("text", byteArrayOutputStream.toString());
                    intent.putExtra("title", "About");
                    startActivity(intent);
                    return true;
                }
                catch (Exception e)
                {
                    Util.LogException("Read Raw File", "readme", e);
                    Util.DisplayToast(SettingsActivity.this, "Failed to load file");
                    return false;
                }
            }
        });
        Preference license = getPreferenceManager().findPreference("license");
        license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                InputStream inputStream = getApplicationContext().getResources().openRawResource(R.raw.license);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int i;
                try {
                    i = inputStream.read();
                    while (i != -1) {
                        byteArrayOutputStream.write(i);
                        i = inputStream.read();
                    }
                    inputStream.close();

                    Intent intent = new Intent(SettingsActivity.this, MarkdownActivity.class);
                    intent.putExtra("text", byteArrayOutputStream.toString());
                    intent.putExtra("title", "License");
                    startActivity(intent);
                    return true;
                }
                catch (Exception e)
                {
                    Util.LogException("Read Raw File", "license", e);
                    Util.DisplayToast(SettingsActivity.this, "Failed to load file");
                    return false;
                }
            }
        });
        Preference changelog = getPreferenceManager().findPreference("changelog");
        changelog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ChangeLog cl = new ChangeLog(SettingsActivity.this);
                cl.getFullLogDialog().show();
                return true;
            }
        });

        localnews.setChecked(true);
        sport.setChecked(settingsManager.getFeedSport());
        lifestyle.setChecked(settingsManager.getFeedLifestyle());
        retail.setChecked(settingsManager.getFeedRetail());
        weather.setChecked(settingsManager.getFeedWeather());
        family.setChecked(settingsManager.getFeedFamily());
        misc.setChecked(settingsManager.getFeedMisc());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.LogMessage("SettingsActivity", "Activity Ended");
    }
}
