package com.example.kopi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class ClipboardListenerService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            Log.d("changed", "onPrimaryClipChanged");
            ClipData clip = mClipboardManager.getPrimaryClip();
            Toast.makeText(ClipboardListenerService.this, clip.getItemAt(0).getText().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    Notification notification;

    public ClipboardListenerService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        super.onCreate();
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(clipChangedListener);

        Intent notificationIntent = new Intent(this, HomepageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,0);

        createNotificationChannel();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(getText(R.string.notification_message))
                    .setSmallIcon(R.drawable.kopi_logo)
                    .setContentIntent(pendingIntent)
                    .setTicker(getText(R.string.ticker_text))
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"Kopi Listener",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

