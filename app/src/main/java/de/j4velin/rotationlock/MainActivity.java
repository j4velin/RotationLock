package de.j4velin.rotationlock;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        }

        Notification.Builder b = new Notification.Builder(this);
        b.addAction(new Notification.Action.Builder(
                Icon.createWithResource(this, R.drawable.ic_done_black_24dp), "Start lock",
                PendingIntent
                        .getService(this, 0, new Intent(this, Lockservice.class).setAction("start"),
                                0)).build());

        b.addAction(new Notification.Action.Builder(
                Icon.createWithResource(this, R.drawable.ic_close_black_24dp), "Stop lock",
                PendingIntent
                        .getService(this, 0, new Intent(this, Lockservice.class).setAction("stop"),
                                0)).build());
        b.setOngoing(true);
        b.setAutoCancel(false);
        b.setContentText("Rotate device to lock or unlock the screen");
        b.setLargeIcon(Icon.createWithResource(this, R.mipmap.ic_launcher));
        b.setSmallIcon(Icon.createWithResource(this, R.mipmap.ic_launcher));

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(0, b.build());

    }
}
