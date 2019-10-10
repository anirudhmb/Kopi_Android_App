package com.example.kopi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.EditText;

public class HomepageActivity extends AppCompatActivity {
    EditText editText_clip_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        editText_clip_content = (EditText) findViewById(R.id.editText_clip_content);

        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip_content = clipboardManager.getPrimaryClip();
        editText_clip_content.setText(clip_content.getItemAt(0).getText());

        Intent intent = new Intent(this, ClipboardListenerService.class);
        startService(intent);

    }
}
