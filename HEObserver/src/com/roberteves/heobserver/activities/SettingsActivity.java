package com.roberteves.heobserver.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.roberteves.heobserver.R;

public class SettingsActivity extends PreferenceActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        addPreferencesFromResource(R.xml.preferences_layout);
    }
}
