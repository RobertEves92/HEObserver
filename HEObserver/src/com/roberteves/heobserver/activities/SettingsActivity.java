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
        super.onCreate(savedInstanceState);
        settingsManager = new SettingsManager(this);
        addPreferencesFromResource(R.xml.preferences_layout);

        createPreferences();
        updatePreferences();
    }

    private void createPreferences() {
        localnews = (CheckBoxPreference) getPreferenceManager().findPreference("feed_localnews");
        sport = (CheckBoxPreference) getPreferenceManager().findPreference("feed_sport");
        sport.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedSport((Boolean) newValue);
                updatePreferences();
                return true;
            }
        });
        lifestyle = (CheckBoxPreference) getPreferenceManager().findPreference("feed_lifestyle");
        lifestyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedLifestyle((Boolean) newValue);
                updatePreferences();
                return true;
            }
        });
        retail = (CheckBoxPreference) getPreferenceManager().findPreference("feed_retail");
        retail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedRetail((Boolean) newValue);
                updatePreferences();
                return true;
            }
        });
        weather = (CheckBoxPreference) getPreferenceManager().findPreference("feed_weather");
        weather.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedWeather((Boolean) newValue);
                updatePreferences();
                return true;
            }
        });
        family = (CheckBoxPreference) getPreferenceManager().findPreference("feed_family");
        family.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedFamily((Boolean) newValue);
                updatePreferences();
                return true;
            }
        });
        misc = (CheckBoxPreference) getPreferenceManager().findPreference("feed_misc");
        misc.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedMisc((Boolean) newValue);
                updatePreferences();
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
                                        updatePreferences();
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
                Intent i = new Intent(SettingsActivity.this, MarkdownActivity.class);
                i.putExtra("url", "https://raw.githubusercontent.com/RobertEves92/HEObserver/master/README.md");
                i.putExtra("title", "About");
                startActivity(i);
                return true;
            }
        });
        Preference license = getPreferenceManager().findPreference("license");
        license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, MarkdownActivity.class);
                i.putExtra("url", "https://raw.githubusercontent.com/RobertEves92/HEObserver/master/LICENSE.md");
                i.putExtra("title", "License");
                startActivity(i);
                return true;
            }
        });
        Preference changelog = getPreferenceManager().findPreference("changelog");
        changelog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, MarkdownActivity.class);
                i.putExtra("url", "https://raw.githubusercontent.com/RobertEves92/HEObserver/master/CHANGELOG.md");
                i.putExtra("title", "Whats New");
                startActivity(i);
                return true;
            }
        });
    }

    private void updatePreferences() {
        localnews.setChecked(true);
        sport.setChecked(settingsManager.getFeedSport());
        lifestyle.setChecked(settingsManager.getFeedLifestyle());
        retail.setChecked(settingsManager.getFeedRetail());
        weather.setChecked(settingsManager.getFeedWeather());
        family.setChecked(settingsManager.getFeedFamily());
        misc.setChecked(settingsManager.getFeedMisc());
    }
}
