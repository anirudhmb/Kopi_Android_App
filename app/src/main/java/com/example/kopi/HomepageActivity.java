package com.example.kopi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class HomepageActivity extends AppCompatActivity{
    EditText editText_clip_content;
    Button button_logout;

    SharedPreferences sharedPreferences;
    String pref_name = "kopi_pref";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        set_views();
        update_clip_content();

        sharedPreferences = getSharedPreferences(pref_name, 0);
        final Intent intent = new Intent(this, ClipboardListenerService.class);
        intent.putExtra("email_id", sharedPreferences.getString("email_id", null));
        startService(intent);

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("logged_in");
                editor.remove("email_id");
                editor.commit();
                Intent logout_intent = new Intent(HomepageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        set_views();
        update_clip_content();
    }

    private void set_views(){
        editText_clip_content = (EditText) findViewById(R.id.editText_clip_content);
        button_logout = (Button) findViewById(R.id.button_logout);
    }

    private void update_clip_content(){
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip_content = clipboardManager.getPrimaryClip();
        if(clip_content != null)
            editText_clip_content.setText(clip_content.getItemAt(0).getText());
        else
            editText_clip_content.setText("");
    }
}
