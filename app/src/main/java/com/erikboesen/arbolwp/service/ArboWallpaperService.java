package com.erikboesen.arbolwp.service;

import com.erikboesen.arbolwp.app.ArboApp;
import com.erikboesen.arbolwp.graphics.ArboRenderer;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class ArboWallpaperService extends CanvasWallpaperService {
	@Override
	public Engine onCreateEngine() {
		return new ArboEngine();
	}

	private class ArboEngine extends CanvasWallpaperEngine {
		private final ArboRenderer renderer = new ArboRenderer();
		private final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences preferences,
					String key) {
				update();
			}
		};

		public ArboEngine() {
			super();
			ArboApp.preferences.getPreferences()
					.registerOnSharedPreferenceChangeListener(listener);
			update();
		}

		@Override
		public void onSurfaceChanged(
				SurfaceHolder holder,
				int format,
				int width,
				int height) {
			super.onSurfaceChanged(holder, format, width, height);
			renderer.setSize(width, height);
		}

		@Override
		protected void drawFrame(Canvas canvas) {
			renderer.draw(canvas);
		}

		private void update() {
			renderer.setTheme(ArboApp.db.getTheme(
					ArboApp.preferences.getTheme()));
		}
	}
}
