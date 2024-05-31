package com.example.final_project.SharedPreference;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {

    private static final String PREF_NAME = "MyAppPreferences";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_REMEMBER_ME = "rememberMe";

    private SharedPreferences sharedPreferences;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveEmail(String email) {
        sharedPreferences.edit().putString(KEY_EMAIL, email).apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public void setRememberMe(boolean rememberMe) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, rememberMe).apply();
    }

    public boolean getRememberMe() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

}
