package de.j4velin.rotationlock;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class Lockservice extends Service implements View.OnTouchListener {

    private final static String TAG = "RotationLock";

    private final static long MIN_TIME_DIFF = 1000;
    private final static int WINDOW_FLAGS = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
            WindowManager.LayoutParams.FLAG_FULLSCREEN;
    private final static WindowManager.LayoutParams p =
            new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                    WINDOW_FLAGS,
                    PixelFormat.RGBA_8888);

    private static long lastChange = 0;
    private static boolean isLocked;
    private static View v;
    private static OrientationListener listener;
    private static Notification.Builder builder;

    static {
        p.gravity = Gravity.TOP | Gravity.LEFT;
        p.x = 0;
    }

    private class OrientationListener extends OrientationEventListener {

        public OrientationListener(final Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int rotation) {
            if (rotation > ORIENTATION_UNKNOWN && System.currentTimeMillis() - lastChange > MIN_TIME_DIFF) {
                lastChange = System.currentTimeMillis();
                if (rotation > 75 && rotation < 285) {
                    if (!isLocked) {
                        if (BuildConfig.DEBUG) android.util.Log.d(TAG, "rotation: " + rotation);
                        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).addView(v, p);
                        isLocked = true;
                    }
                } else if (isLocked) {
                    if (BuildConfig.DEBUG) android.util.Log.d(TAG, "rotation: " + rotation);
                    try {
                        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).removeView(v);
                    } catch (Exception e) {
                    }
                    isLocked = false;
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (isLocked) {
                Toast.makeText(this, "Please rotate into portrait mode first", Toast.LENGTH_SHORT).show();
                return START_STICKY;
            }
            if (action.equals("stop")) {
                disable();
            } else if (action.equals("start")) {
                enable();
            } else if (action.equals("destroy")) {
                stopSelf();
                return START_NOT_STICKY;
            }
        } else {
            init();
            enable();
        }
        return START_STICKY;
    }

    private void init() {
        if (BuildConfig.DEBUG) android.util.Log.d(TAG, "init");
        if (v == null) {
            v = new View(this);
            v.setBackgroundColor(Color.argb(200, 0, 0, 0));
            v.setOnTouchListener(this);
        }

        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("wakelock", true)) {
            p.flags = WINDOW_FLAGS | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        } else {
            p.flags = WINDOW_FLAGS;
        }

        if (listener == null) {
            listener = new OrientationListener(this);
        }

        if (builder == null) {
            builder = new Notification.Builder(this);
            builder.setOngoing(true);
            builder.setAutoCancel(false);
            builder.setSmallIcon(R.drawable.ic_notification);
            if (Build.VERSION.SDK_INT >= 23) {
                builder.addAction(new Notification.Action.Builder(
                        Icon.createWithResource(this, R.drawable.ic_close_black_24dp), getString(R.string.stop_and_remove),
                        PendingIntent
                                .getService(this, 0, new Intent(this, Lockservice.class).setAction("destroy"),
                                        0)).build());
            } else {
                builder.addAction(R.drawable.ic_close_black_24dp, getString(R.string.stop_and_remove),
                        PendingIntent
                                .getService(this, 0, new Intent(this, Lockservice.class).setAction("destroy"),
                                        0));
            }
        }
    }

    private void enable() {
        if (BuildConfig.DEBUG) android.util.Log.d(TAG, "enable");
        if (listener == null) {
            init();
        }
        listener.enable();
        builder.setContentTitle(getString(R.string.service_enabled));
        builder.setContentText(getString(R.string.click_to_disable));
        builder.setContentIntent(PendingIntent
                .getService(this, 0, new Intent(this, Lockservice.class).setAction("stop"),
                        0));
        builder.setPriority(Notification.PRIORITY_LOW);
        startForeground(1, builder.build());
    }

    private void disable() {
        if (BuildConfig.DEBUG) android.util.Log.d(TAG, "disable");
        if (listener != null) {
            listener.disable();
        }
        if (v != null) {
            try {
                ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).removeView(v);
            } catch (Exception e) {
            }
            isLocked = false;
        }
        builder.setContentTitle(getString(R.string.service_disabled));
        builder.setContentText(getString(R.string.click_to_enable));
        builder.setContentIntent(PendingIntent
                .getService(this, 0, new Intent(this, Lockservice.class).setAction("start"),
                        0));
        builder.setPriority(Notification.PRIORITY_MIN);
        startForeground(1, builder.build());
    }


    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) android.util.Log.d(TAG, "destroy");
        disable();
        stopForeground(true);
        listener = null;
        v = null;
        builder = null;
        super.onDestroy();
    }
}
