package cl.chihau.ositoswatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MyWatchFace extends CanvasWatchFaceService {
    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine implements SensorEventListener {

        Paint mHourPaint;
        Paint mMinutePaint;
        Paint mSecondPaint;
        Paint mTickPaint;

        Paint mSteps;
        Paint mDatePaint;

        boolean mAmbient;
        Time mTime;
        Date mDate;

        final Handler mUpdateTimeHandler = new EngineHandler(this);

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();
            }
        };
        boolean mRegisteredTimeZoneReceiver = false;


        Bitmap mBackgroundBitmap;
        Bitmap mBackgroundScaledBitmap;

        Bitmap stepsBitmap;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        // Retrieve the sensor manager and the step counter
        SensorManager mgr;
        Sensor stepCtr;


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int steps;
        Date fechaLectura;
        int stepLectura;

        SharedPreferences.Editor editor;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(MyWatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setHotwordIndicatorGravity(Gravity.TOP)
                    .setViewProtectionMode(WatchFaceStyle.PROTECT_STATUS_BAR | WatchFaceStyle.PROTECT_HOTWORD_INDICATOR)
                    .setStatusBarGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                    .setAcceptsTapEvents(true) // Esta opción sólo funciona desde Android Wear 1.3
                    .build());

            Resources resources = MyWatchFace.this.getResources();
            Drawable backgroundDrawable = resources.getDrawable(R.drawable.osos, null /* theme */);
            mBackgroundBitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();

            Drawable stepsDrawable = resources.getDrawable(R.drawable.steps3, null);
            stepsBitmap = ((BitmapDrawable) stepsDrawable).getBitmap();

            mHourPaint = new Paint();
            mHourPaint.setColor(resources.getColor(R.color.analog_hands));
            mHourPaint.setStrokeWidth(resources.getDimension(R.dimen.analog_hour_hand_stroke));
            mHourPaint.setAntiAlias(true);
            mHourPaint.setStrokeCap(Paint.Cap.ROUND);

            mMinutePaint = new Paint();
            mMinutePaint.setARGB(255, 200, 200, 200);
            mMinutePaint.setStrokeWidth(resources.getDimension(R.dimen.analog_min_hand_stroke));
            mMinutePaint.setAntiAlias(true);
            mMinutePaint.setStrokeCap(Paint.Cap.ROUND);

            mSecondPaint = new Paint();
            mSecondPaint.setColor(resources.getColor(R.color.analog_sec_hands));
            mSecondPaint.setStrokeWidth(2.f);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);

            mTickPaint = new Paint();
            mTickPaint.setARGB(100, 255, 255, 255);
            mTickPaint.setStrokeWidth(2.f);
            mTickPaint.setAntiAlias(true);

            mSteps = new Paint();
            mSteps.setARGB(255, 200, 200, 200);
            mSteps.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
            mSteps.setAntiAlias(true);
            mSteps.setTextSize(30);

            mDatePaint = new Paint();
            mDatePaint.setARGB(255, 200, 200, 200);
            mDatePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
            mDatePaint.setAntiAlias(true);
            mDatePaint.setTextSize(30);

            mTime = new Time();

            mDate = new Date();

            SharedPreferences prefs = getSharedPreferences("HelloWatchFace", MODE_PRIVATE);

            try {
                fechaLectura = sdf.parse(prefs.getString("fecha_lectura", "1970-01-01"));
                //stepLectura = prefs.getInt("step_lectura", 0);
                stepLectura = 0;
                steps = prefs.getInt("steps", 0);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            editor = getSharedPreferences("HelloWatchFace", MODE_PRIVATE).edit();

            mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
            stepCtr = mgr.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            mgr.registerListener(this, stepCtr, SensorManager.SENSOR_DELAY_NORMAL);
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            final int type = sensorEvent.sensor.getType();

            if (type == Sensor.TYPE_STEP_COUNTER) {

                Date hoy = null;
                try {
                    hoy = sdf.parse(sdf.format(new Date()));
                    //hoy = sdf.parse("2015-08-25");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (fechaLectura.equals(hoy)) {
                    Log.d("HelloWatchFace", "son iguales");

                    steps += (int) sensorEvent.values[0] - stepLectura;
                    stepLectura = (int) sensorEvent.values[0];

                } else {
                    Log.d("HelloWatchFace", "son distintas");

                    steps = 0; //aquí podría ser (int) sensorEvent.values[0] - stepLectura;
                    stepLectura = (int) sensorEvent.values[0];

                    fechaLectura = hoy;
                    editor.putString("fecha_lectura", sdf.format(hoy));
                }

                //editor.putInt("step_lectura", stepLectura);
                editor.putInt("steps", steps);
                editor.commit();

                //Log.d("HelloWatchFace", Integer.toString(steps) + " " + fechaLectura.toString() + " " + hoy.toString());

                Log.d("HelloWatchFace", "STEP COUNTER " + steps);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.v("HelloWatchFace", "Sensor accuracy changed. New value: " + accuracy);
        }

        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            Log.d("HelloWatchFace", "tapType: " + tapType);
            Log.d("HelloWatchFace", "int x: " + x + " int y: " + y);
            Log.d("HelloWatchFace", "eventTime: " + eventTime);
        }

//        public boolean openApp(Context context, String packageName) {
//            PackageManager manager = context.getPackageManager();
//
//            Intent i = manager.getLaunchIntentForPackage(packageName);
//            if (i == null) {
//                return false;
//                //throw new PackageManager.NameNotFoundException();
//            }
//            i.addCategory(Intent.CATEGORY_LAUNCHER);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
//            return true;
//
//        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mHourPaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mBackgroundScaledBitmap == null
                    || mBackgroundScaledBitmap.getWidth() != width
                    || mBackgroundScaledBitmap.getHeight() != height) {
                mBackgroundScaledBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                        width, height, true /* filter */);
            }
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();

            mDate = new Date();

            int width = bounds.width();
            int height = bounds.height();

            // Draw the background, scaled to fit.
            if (!mAmbient) {
                canvas.drawBitmap(mBackgroundScaledBitmap, 0, 0, null);
            } else {
                canvas.drawColor(Color.BLACK);
            }

            // Find the center. Ignore the window insets so that, on round watches with a
            // "chin", the watch face is centered on the entire screen, not just the usable
            // portion.
            float centerX = width / 2f;
            float centerY = height / 2f;

            // Draw the ticks.
            float innerTickRadius = centerX - 10;
            float outerTickRadius = centerX;
            for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
                float tickRot = tickIndex * (float) Math.PI * 2f / 12;
                float innerX = (float) Math.sin(tickRot) * innerTickRadius;
                float innerY = (float) -Math.cos(tickRot) * innerTickRadius;
                float outerX = (float) Math.sin(tickRot) * outerTickRadius;
                float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
                canvas.drawLine(centerX + innerX, centerY + innerY,
                        centerX + outerX, centerY + outerY, mTickPaint);
            }

            float secRot = mTime.second / 30f * (float) Math.PI;
            int minutes = mTime.minute;
            float minRot = minutes / 30f * (float) Math.PI;
            float hrRot = ((mTime.hour + (minutes / 60f)) / 6f) * (float) Math.PI;

            float secLength = centerX - 20;
            float minLength = centerX - 40;
            float hrLength = centerX - 80;

            if (!mAmbient) {
                float secX = (float) Math.sin(secRot) * secLength;
                float secY = (float) -Math.cos(secRot) * secLength;
                canvas.drawLine(centerX, centerY, centerX + secX, centerY + secY, mSecondPaint);
            }

            float minX = (float) Math.sin(minRot) * minLength;
            float minY = (float) -Math.cos(minRot) * minLength;
            canvas.drawLine(centerX, centerY, centerX + minX, centerY + minY, mMinutePaint);

            float hrX = (float) Math.sin(hrRot) * hrLength;
            float hrY = (float) -Math.cos(hrRot) * hrLength;
            canvas.drawLine(centerX, centerY, centerX + hrX, centerY + hrY, mHourPaint);

            Rect textBounds = new Rect();
            mSteps.getTextBounds(Integer.toString(steps), 0, Integer.toString(steps).length(), textBounds);
            int x = (canvas.getWidth() / 2) - (textBounds.width() / 2);
            int y = (canvas.getHeight() / 2) - (textBounds.height() / 2);
            //Log.d("LALA", "x: " + x + " y: " + y);
            canvas.drawText(Integer.toString(steps), x, y - 50, mSteps);

            canvas.drawText(sdf.format(mDate), 75, 220, mDatePaint);

            if (!mAmbient) {
//                // Para cambiar de color cualquier el bitmap del monito cominando
//                Paint p = new Paint();
//                ColorFilter filter = new LightingColorFilter(Color.BLACK, 1);
//                p.setColorFilter(filter);

                //canvas.drawBitmap(stepsBitmap, x - 30, y - 80, p); <-- p en vez null
                canvas.drawBitmap(stepsBitmap, x - 30, y - 80, null);
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            MyWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            MyWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);


            }
        }
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<MyWatchFace.Engine> mWeakReference;

        public EngineHandler(MyWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            MyWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }
}
