package com.ojambrina.ipatient.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {
    private static final String PREF_FILE = "MyPreferences";

    private static final String EMAIL = "email";
    private static final String CHECKBOX_LOGIN = "loginCheckboxIsChecked";

    private Context context;

    public AppPreferences() {

    }

    private SharedPreferences getSharedPreferences() {
        if (context != null) {
            return context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        } else {
            return App.getInstance().getApplicationContext().getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        }
    }

    /**
     * GETTER'S
     **/

    public String getEmail() {
        return getSharedPreferences().getString(EMAIL, null);
    }

    public boolean getCheckboxLogin() {
        return getSharedPreferences().getBoolean(CHECKBOX_LOGIN, false);
    }

    /**
     * SETTER'S
     **/

    public void setEmail(String email) {
        this.getSharedPreferences().edit().putString(EMAIL, email).apply();
    }

    public void setCheckboxLogin(boolean checkboxLogin) {
        this.getSharedPreferences().edit().putBoolean(CHECKBOX_LOGIN, checkboxLogin).apply();
    }

    /**
     * CLEAR
     **/

    public void clearPreferences() {
        this.getSharedPreferences().edit().clear().apply();
    }

}
