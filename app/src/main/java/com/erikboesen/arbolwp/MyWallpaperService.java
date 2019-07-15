package com.erikboesen.arbolwp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class MyWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new MyWallpaperEngine();
    }

    private class MyWallpaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
            @Override
            public void run() {
                draw();
            }

        };
        private Paint paint = new Paint();
        private int width;
        private int height;
        private boolean visible = true;



        public MyWallpaperEngine() {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(MyWallpaperService.this);
            paint.setAntiAlias(true);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(10f);
            handler.post(drawRunner);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {
            this.width = width;
            this.height = height;
            super.onSurfaceChanged(holder, format, width, height);
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    this.drawBranch(canvas, 10, 350, this.width / 2, this.height, -Math.PI / 2);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            if (visible) {
                handler.postDelayed(drawRunner, 5000);
            }
        }

        private double radians(int degrees) {
            return degrees * Math.PI / 180;
        }

        private void drawBranch(Canvas canvas, int iteration, double length, int startX, int startY, double angle) {
            int branchLengthMultiplier = 75,
                middleLengthMultiplier = 65,
                spread = 30,
                wind = 0,
                tilt = 0;
            int endX = startX + (int)(Math.cos(angle) * length);
            int endY = startY + (int)(Math.sin(angle) * length);
            canvas.drawLine(startX, startY, endX, endY, paint);
            if (iteration > 0) {
                this.drawBranch(canvas,
                        iteration - 1,
                        length * branchLengthMultiplier / 100,
                        endX, endY,
                        angle + this.radians(spread + tilt + wind));
                this.drawBranch(canvas,
                        iteration - 1,
                        length * middleLengthMultiplier / 100,
                        endX, endY,
                        angle + this.radians(tilt + wind));
                this.drawBranch(canvas,
                        iteration - 1,
                        length * branchLengthMultiplier / 100,
                        endX, endY,
                        angle + this.radians(-spread + tilt + wind));
            }
        }
    }
}