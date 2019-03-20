package com.erikboesen.arbolwp;

import java.util.ArrayList;
import java.util.List;

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
        private List<MyPoint> circles;
        private Paint paint = new Paint();
        private int width;
        int height;
        private boolean visible = true;
        private int maxNumber;
        private boolean touchEnabled;

        public MyWallpaperEngine() {
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(MyWallpaperService.this);
            maxNumber = Integer
                    .valueOf(prefs.getString("numberOfCircles", "4"));
            touchEnabled = prefs.getBoolean("touch", false);
            circles = new ArrayList<MyPoint>();
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

        @Override
        public void onTouchEvent(MotionEvent event) {
            if (touchEnabled) {

                float x = event.getX();
                float y = event.getY();
                SurfaceHolder holder = getSurfaceHolder();
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    if (canvas != null) {
                        canvas.drawColor(Color.BLACK);
                        circles.clear();
                        circles.add(new MyPoint(
                                String.valueOf(circles.size() + 1), x, y));
                        drawCircles(canvas, circles);

                    }
                } finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas);
                }
                super.onTouchEvent(event);
            }
        }

        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    if (circles.size() >= maxNumber) {
                        circles.clear();
                    }
                    int x = (int) (width * Math.random());
                    int y = (int) (height * Math.random());
                    circles.add(new MyPoint(String.valueOf(circles.size() + 1), x, y));
                    drawCircles(canvas, circles);
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

        private void drawBranch(Canvas canvas, int iteration, float length, int startX, int startY, int angle) {
            int endX = startX + (int)(Math.cos(angle) * length);
            int endY = startY + (int)(Math.sin(angle) * length);
            canvas.drawLine(startX, startY, endX, endY, paint);
            if (iteration > 0) {
                this.drawBranch(canvas,
                        iteration - 1,
                        length * options.branchLengthMultiplier.value / 100,
                        endX, endY,
                        angle + this.radians(Double.parseDouble(options.spread.value) + Double.parseDouble(options.tilt.value) + wind));
                this.drawBranch(canvas,
                        iteration - 1,
                        length * options.middleLengthMultiplier.value / 100,
                        endX, endY,
                        angle + this.radians(Double.parseDouble(options.tilt.value) + wind));
                this.drawBranch(canvas,
                        iteration - 1,
                        length * options.branchLengthMultiplier.value / 100,
                        endX, endY,
                        angle + this.radians(-Double.parseDouble(options.spread.value) + Double.parseDouble(options.tilt.value) + wind));
            }
        }

        // Surface view requires that all elements are drawn completely
        private void drawCircles(Canvas canvas, List<MyPoint> circles) {
            canvas.drawColor(Color.BLACK);
            for (MyPoint point : circles) {
                canvas.drawCircle(point.x, point.y, 20.0f, paint);
            }
        }
    }
}