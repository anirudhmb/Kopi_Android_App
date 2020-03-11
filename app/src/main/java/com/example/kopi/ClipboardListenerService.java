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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ClipboardListenerService extends Service {

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    String TAG = "ClipboardListenerService";
    Socket mSocket;
    private String email_id = null;

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ClipData clipData = ClipData.newPlainText("kopi_text", "");
            Log.d(TAG, "call: "+((JSONObject)args[0]).toString());
        }
    };

    {
        try {
            mSocket = IO.socket("https://kopi-socket-server.herokuapp.com/");
            mSocket.on("updated_clip_content", onNewMessage);
            mSocket.connect();
            JSONObject join_chat_room = new JSONObject();
            join_chat_room.put("email", email_id);
            mSocket.emit("privatechatroom", join_chat_room);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private ClipboardManager mClipboardManager;
    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            Log.d("changed", "onPrimaryClipChanged");
            ClipData clip = mClipboardManager.getPrimaryClip();
            JSONObject send_message = new JSONObject();
            try {
                send_message.put("email",email_id);
                send_message.put("clip_content", clip.getItemAt(0).getText().toString());
                mSocket.emit("new_private_message", send_message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        email_id = intent.getStringExtra("email_id");
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.putExtra("email_id", email_id);
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID,"Kopi Listener",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

