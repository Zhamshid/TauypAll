package kz.app.taýypall.data;


import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPrefsHelper {

    public static final String MY_PREFS = "MY_PREFS";

    public static final String USERNAME = "EMAIL";
    public static final String ISLOGGED = "ISLOGGED";

    SharedPreferences mSharedPreferences;

    public SharedPrefsHelper(Context context) {
        mSharedPreferences = context.getSharedPreferences(MY_PREFS, MODE_PRIVATE);
    }

    public SharedPrefsHelper() {

    }

    public void clear() {
        mSharedPreferences.edit().clear().apply();
    }

    public String getPhoneNumber() {
        return mSharedPreferences.getString("PhoneNumber", "");
    }

    public void setPhoneNumber(String PhoneNumber) {
        mSharedPreferences.edit().putString("PhoneNumber", PhoneNumber).apply();
    }
    public String getToken() {
        return mSharedPreferences.getString("Token", "");
    }

    public void setToken(String Token) {
        mSharedPreferences.edit().putString("Token", Token).apply();
    }

    public boolean getIslogged() {
        return mSharedPreferences.getBoolean("ISLOGGED", false);
    }

    public void setIslogged(boolean islogged) {
        mSharedPreferences.edit().putBoolean("ISLOGGED", islogged).apply();
    }


    public String getCustom(String key) {
        return mSharedPreferences.getString(key,"");
    }

    public void setCustom(String key , String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }



}
