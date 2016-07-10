package com.example.akimasa.chinachudownloader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by akimasa on 16/07/10.
 */
public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        EditText editText;
        editText = (EditText)findViewById(R.id.user);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        editText = (EditText)findViewById(R.id.user);
        editText.setText(sp.getString("user", null), TextView.BufferType.NORMAL);

        editText = (EditText)findViewById(R.id.password);
        editText.setText(sp.getString("password", null), TextView.BufferType.NORMAL);

        editText = (EditText)findViewById(R.id.host);
        editText.setText(sp.getString("host", null), TextView.BufferType.NORMAL);

        editText = (EditText)findViewById(R.id.port);
        editText.setText(sp.getString("port", null), TextView.BufferType.NORMAL);

        Button sendButton = (Button) findViewById(R.id.settings_back);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button saveButton = (Button) findViewById(R.id.settings_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText;
                editText = (EditText)findViewById(R.id.host);
                sp.edit().putString("host", editText.getText().toString()).commit();

                editText = (EditText)findViewById(R.id.port);
                sp.edit().putString("port", editText.getText().toString()).commit();

                editText = (EditText)findViewById(R.id.user);
                sp.edit().putString("user", editText.getText().toString()).commit();

                editText = (EditText)findViewById(R.id.password);
                sp.edit().putString("password", editText.getText().toString()).commit();

                finish();
            }
        });
    }
}
