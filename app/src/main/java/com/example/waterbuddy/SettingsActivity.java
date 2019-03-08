package com.example.waterbuddy;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

public class SettingsActivity extends AppCompatActivity {
    private EditText goal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor spe = sp.edit();

        setContentView(R.layout.activity_settings);

        goal = findViewById(R.id.goal_input);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        final EditText chooseTime = findViewById(R.id.daily_reset_input);

        chooseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(SettingsActivity.this, R.style.TimePickerTheme,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                                chooseTime.setText(String.format("%02d:%02d", hourOfDay, minutes));
                                spe.putString("time", chooseTime.getText().toString());
                                spe.commit();
                            }
                        }, 0, 0, false);

                timePickerDialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        ((EditText) findViewById(R.id.daily_reset_input)).setText(sp.getString("time", "00:00"));
        goal.setText(Integer.toString((sp.getInt("goal", 64))));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.back:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void applyGoal(View view) {
        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt("goal", Integer.parseInt(goal.getText().toString()));
        spe.commit();
    }

    public void togUnits(View view) {
        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
    }

    public void togNotifs(View view) {
        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
    }
}
