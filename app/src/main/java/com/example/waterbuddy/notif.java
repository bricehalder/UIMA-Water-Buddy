package com.example.waterbuddy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class notif extends AppCompatActivity {
    private CheckBox emailPref;
    private EditText emailAdr;
    private Spinner emailNotifTime;
    private Spinner notifFreq;
    private Spinner notifStart;
    private Spinner notifPct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        /** Get shared-preference-related elements by id now so we don't have to repeat this code */
        emailPref = (CheckBox) findViewById(R.id.emailPref);
        emailAdr = (EditText) findViewById(R.id.emailIn);
        emailNotifTime = (Spinner) findViewById(R.id.spEmailNotifTime);
        notifFreq = (Spinner) findViewById(R.id.spNotifFreq);
        notifStart = (Spinner) findViewById(R.id.spNotifStart);
        notifPct = (Spinner) findViewById(R.id.spNotifPct);
    }

    @Override
    public void onResume() {
        super.onResume();

        /** Display the shared preferences info. */
        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        emailPref.setChecked(sp.getBoolean("emailPref", false));
        emailAdr.setText(sp.getString("emailAdr", ""));
        emailNotifTime.setSelection(sp.getInt("emailNotifTime", 0));
        notifFreq.setSelection(sp.getInt("notifFreq", 0));
        notifStart.setSelection(sp.getInt("notifStart", 0));
        notifPct.setSelection(sp.getInt("notifPct", 0));
    }

    /** No need to commit shared preferences if user clicks cancel. */
    public void cancelNotifs(View view) {
        finish();
    }

    /** If user clicks save, commit the shared preferences. */
    public void saveNotifs(View view) {
        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();

        spe.putBoolean("emailPref", emailPref.isChecked());
        spe.putString("emailAdr", emailAdr.getText().toString());
        spe.putInt("emailNotifTime", emailNotifTime.getSelectedItemPosition());
        spe.putInt("notifFreq", notifFreq.getSelectedItemPosition());
        spe.putInt("notifStart", notifStart.getSelectedItemPosition());
        spe.putInt("notifPct", notifPct.getSelectedItemPosition());

        spe.commit();
        finish();
    }
}