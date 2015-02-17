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

public class SettingsActivity extends PreferenceActivity {
    Preference reset, about, license, changelog;
    CheckBoxPreference news, localnews, sport, music, lifestyle, retail, weather, family, misc;
    SettingsManager settingsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsManager = new SettingsManager(this);
        addPreferencesFromResource(R.xml.preferences_layout);

        createPreferences();
        updatePreferences();
    }

    private void createPreferences() {
        news = (CheckBoxPreference) getPreferenceManager().findPreference("feed_news");
        news.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedNews(true);//force always on
                updatePreferences();
                return true;
            }
        });
        localnews = (CheckBoxPreference) getPreferenceManager().findPreference("feed_localnews");
        localnews.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsManager.setFeedLocalNews((Boolean) newValue);
                updatePreferences();
                return true;
            }
        });
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

        reset = getPreferenceManager().findPreference("reset");
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

        about = getPreferenceManager().findPreference("about");
        about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, MarkdownActivity.class);
                i.putExtra("url", "https://raw.githubusercontent.com/RobertEves92/HEObserver/master/README.md");
                i.putExtra("title","About");
                startActivity(i);
                return true;
            }
        });
        license = getPreferenceManager().findPreference("license");
        license.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, MarkdownActivity.class);
                i.putExtra("url", "https://raw.githubusercontent.com/RobertEves92/HEObserver/master/LICENSE.md");
                i.putExtra("title","License");
                startActivity(i);
                return true;
            }
        });
        changelog = getPreferenceManager().findPreference("changelog");
        changelog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(SettingsActivity.this, MarkdownActivity.class);
                i.putExtra("url", "https://raw.githubusercontent.com/RobertEves92/HEObserver/master/CHANGELOG.md");
                i.putExtra("title","Whats New");
                startActivity(i);
                return true;
            }
        });
    }

    private void updatePreferences() {
        news.setChecked(settingsManager.getFeedNews());
        localnews.setChecked(settingsManager.getFeedLocalNews());
        sport.setChecked(settingsManager.getFeedSport());
        lifestyle.setChecked(settingsManager.getFeedLifestyle());
        retail.setChecked(settingsManager.getFeedRetail());
        weather.setChecked(settingsManager.getFeedWeather());
        family.setChecked(settingsManager.getFeedFamily());
        misc.setChecked(settingsManager.getFeedMisc());
    }
}
