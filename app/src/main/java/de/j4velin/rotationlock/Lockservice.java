package de.j4velin.rotationlock;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Icon;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;

public class Lockservice extends Service implements View.OnTouchListener {

    private static boolean isShown;
    private static View v;
    private final static WindowManager.LayoutParams p =
            new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.RGBA_8888);
    private static OrientationListener listener;
    private static Notification.Builder builder;

    static {
        p.gravity = Gravity.TOP | Gravity.LEFT;
        p.x = 0;
    }

    private class OrientationListener extends OrientationEventListener {

        public OrientationListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int rotation) {
            if (rotation > 45 && rotation < 315) {
                if (!isShown) {
                    ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).addView(v, p);
                    isShown = true;
                }
            } else if (isShown) {
                try {
                    ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).removeView(v);
                } catch (Exception e) {
                }
                isShown = false;
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

        if (intent != null && intent.getAction() != null && intent.getAction().equals("stop")) {
            disable();
        } else if (intent != null && intent.getAction() != null && intent.getAction().equals("start")) {
            enable();
        } else if (intent != null && intent.getAction() != null && intent.getAction().equals("destroy")) {
            stopSelf();
            return START_NOT_STICKY;
        } else {

            if (v == null) {
                v = new View(this);
                v.setBackgroundColor(Color.argb(200, 0, 0, 0));
                v.setOnTouchListener(this);
            }

            if (listener == null) {
                listener = new OrientationListener(this);
                listener.enable();
            }

            if (builder == null) {
                builder = new Notification.Builder(this);
                builder.setOngoing(true);
                builder.setAutoCancel(false);
                builder.setSmallIcon(Icon.createWithResource(this, R.mipmap.ic_launcher));
                builder.addAction(new Notification.Action.Builder(
                        Icon.createWithResource(this, R.drawable.ic_close_black_24dp), "Stop and remove",
                        PendingIntent
                                .getService(this, 0, new Intent(this, Lockservice.class).setAction("destroy"),
                                        0)).build());
            }

            enable();
        }
        return START_STICKY;
    }

    private void enable() {
        builder.setContentTitle("Lock service enabled");
        builder.setContentText("Click to disable");
        builder.setContentIntent(PendingIntent
                .getService(this, 0, new Intent(this, Lockservice.class).setAction("stop"),
                        0));
        builder.setPriority(Notification.PRIORITY_LOW);
        startForeground(1, builder.build());
    }

    private void disable() {
        if (listener != null) {
            listener.disable();
        }
        if (v != null) {
            try {
                ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).removeView(v);
            } catch (Exception e) {
            }
        }
        builder.setContentTitle("Lock service disabled");
        builder.setContentText("Click to enable");
        builder.setContentIntent(PendingIntent
                .getService(this, 0, new Intent(this, Lockservice.class).setAction("start"),
                        0));
        builder.setPriority(Notification.PRIORITY_MIN);
        startForeground(1, builder.build());
    }


    @Override
    public void onDestroy() {
        disable();
        stopForeground(true);
        listener = null;
        v = null;
        builder = null;
        super.onDestroy();
    }
}
