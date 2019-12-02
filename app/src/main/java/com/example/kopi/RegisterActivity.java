package com.example.kopi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    Button login_here_button;
    Button register_button;

    EditText email_id;
    EditText password;

    SharedPreferences sharedPreferences;
    String pref_name = "kopi_pref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            this.getSupportActionBar().hide();
        }
        catch (NullPointerException e){}
        setContentView(R.layout.activity_register);


        login_here_button = (Button) findViewById(R.id.button_login_2);
        register_button = (Button) findViewById(R.id.button_register_2);

        email_id = (EditText) findViewById(R.id.editText_email_2);
        password = (EditText) findViewById(R.id.editText_password_2);

        login_here_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> postData = new HashMap<>();
                postData.put("email_id", email_id.getText().toString());
                postData.put("password", password.getText().toString());
                try {
                    new HttpPostAsyncTask(postData).execute(PropertiesUtil.getProperty("registration_url", getApplicationContext()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public class HttpPostAsyncTask extends AsyncTask<String, Void, String> {
        // This is the JSON body of the post
        JSONObject postData;
        // This is a constructor that allows you to pass in the JSON body
        public HttpPostAsyncTask(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
        @Override
        protected String doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "someAuthString");

                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = br.readLine()) != null){
                        sb.append(line);
                    }
                    System.out.println("response");
                    System.out.println(sb);
                    return sb.toString();
                } else {
                    Toast.makeText(getBaseContext(), "error trying to signup", Toast.LENGTH_LONG).show();
                    return "invalid";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if("{status:success}".equals(s)){
                sharedPreferences = getSharedPreferences(pref_name, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    editor.putBoolean("logged_in",true);
                    editor.putString("email_id", postData.getString("email_id"));
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(RegisterActivity.this, HomepageActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getBaseContext(), "An account with the above credentials already exist...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
