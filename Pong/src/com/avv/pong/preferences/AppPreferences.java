package com.avv.pong.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

	private final SharedPreferences sharedPreferences;

	private AppPreferences(Context context) {
		this.sharedPreferences = context.getSharedPreferences("Preferencias",
				Context.MODE_PRIVATE);
	}

	private static AppPreferences instance;

	public static AppPreferences getInstance(Context context) {
		if (instance == null) {
			instance = new AppPreferences(context);
		}
		return instance;
	}

	public void savePreference(String key, String name) {
		SharedPreferences.Editor editor = this.sharedPreferences.edit();
		editor.putString(key, name);
		editor.commit();
	}

	public String getPreference(String key) {
		return this.sharedPreferences.getString(key, null);
	}

}
