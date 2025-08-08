package com.example.carwashelcatracho.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.carwashelcatracho.Models.User;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "CarWashSession";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER = "user";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_PROFILE_PHOTO_PATH = "profilePhotoPath";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    public void setLogin(boolean isLoggedIn, String token, User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putString(KEY_USER_TYPE, user.getTipoUsuario());
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public User getUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    public String getUserType() {
        return prefs.getString(KEY_USER_TYPE, null);
    }

    public boolean isAdmin() {
        return "admin".equals(getUserType());
    }

    public boolean isClient() {
        return "cliente".equals(getUserType());
    }

    public void updateUser(User user) {
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
    }

    public void setProfilePhotoPath(String path) {
        editor.putString(KEY_PROFILE_PHOTO_PATH, path);
        editor.apply();
    }

    public String getProfilePhotoPath() {
        return prefs.getString(KEY_PROFILE_PHOTO_PATH, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public String getAuthHeader() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }
}
