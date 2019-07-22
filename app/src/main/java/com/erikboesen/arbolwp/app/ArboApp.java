package com.erikboesen.arbolwp.app;

import com.erikboesen.arbolwp.database.Database;
import com.erikboesen.arbolwp.preference.Preferences;

import android.app.Application;

public class ArboApp extends Application {
	public static final Database db = new Database();
	public static final Preferences preferences = new Preferences();

	@Override
	public void onCreate() {
		super.onCreate();
		db.open(this);
		preferences.init(this);
	}
}
