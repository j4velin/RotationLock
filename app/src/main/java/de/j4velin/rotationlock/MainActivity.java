package de.j4velin.rotationlock;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView text = (TextView) findViewById(R.id.text);

        if (!Settings.canDrawOverlays(this)) {
            text.setText("App requires permission to lock the screen. Click here to grant that permission");
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1);
                }
            });
        } else {
            text.setText("The app will lock (most of) the screen, when you tilt your device for more then 45°. The screen will turn dark, to indicate that touch events are blocked. Rotate your device back in portrait mode to unlock the screen again.\n\nNote: Not everything can be blocked - the status bar and on-screen navigation buttons are still accessable!\n\nTo disable the automatic screen lock, simply click on the apps notification.");
            startService(new Intent(this, Lockservice.class));
        }
    }
}
