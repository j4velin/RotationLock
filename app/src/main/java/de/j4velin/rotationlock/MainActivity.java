package de.j4velin.rotationlock;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            askForPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView text = (TextView) findViewById(R.id.text);

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            text.setText(R.string.require_permission);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    askForPermission();
                }
            });
        } else {
            text.setText(R.string.intro);
            startService(new Intent(this, Lockservice.class));
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askForPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1);
    }
}
