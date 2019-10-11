package com.example.kopi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class HomepageActivity extends AppCompatActivity{
    EditText editText_clip_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        set_views();
        update_clip_content();

        Intent intent = new Intent(this, ClipboardListenerService.class);
        startService(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        set_views();
        update_clip_content();
    }

    private void set_views(){
        editText_clip_content = (EditText) findViewById(R.id.editText_clip_content);
    }

    private void update_clip_content(){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip_content = clipboardManager.getPrimaryClip();
        editText_clip_content.setText(clip_content.getItemAt(0).getText());
    }
}
