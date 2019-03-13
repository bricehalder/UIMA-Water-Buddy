package com.example.waterbuddy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Date;

public class NavIntake extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    final static float OZ_ML_CONVERT = 29.574f;

    private int waterGoal;
    private int waterCur;
    private int waterPrev;
    private String units;
    /** True if units are ml, false if oz. Scales seekbar by OZ_ML_CONVERT if units are ml. */
    private boolean seekbarMl;

    private ImageView buddyView;
    private SeekBar seek;

    private String goalMsg, goal, today;
    private String dayOfYear, time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_intake);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /** Set basic views and values. */
        buddyView = (ImageView) findViewById(R.id.buddy);
        buddyView.setImageResource(R.drawable.wb0);
        seek = (SeekBar) findViewById(R.id.waterSelector);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int sbAmount;
                if (seekbarMl) {
                    sbAmount = Math.round(progress * OZ_ML_CONVERT);
                } else {
                    sbAmount = progress;
                }
                ((TextView) findViewById(R.id.dispSelect)).setText(Integer.toString(sbAmount) + units);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //nothing
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                //nothing
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        /** Display the settings stored in the sharedprefs as well as the current daily water consumption. */
        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        waterCur = sp.getInt("waterCur", 0);
        waterPrev = sp.getInt("waterPrev", 0);
        waterGoal = sp.getInt("goal", 64);
        if (sp.getBoolean("oz", true)) {
            units = " oz.";
            seekbarMl = false;
            ((TextView) findViewById(R.id.dispSelect)).setText(Integer.toString(seek.getProgress()) + units);
        } else {
            units = " ml.";
            seekbarMl = true;
            ((TextView) findViewById(R.id.dispSelect)).setText(Integer.toString(Math.round(OZ_ML_CONVERT * seek.getProgress())) + units);
        }

        String lastResetDay = sp.getString("date", "-1");

        dayOfYear = DateFormat.format("dd", new Date()).toString();
        time = DateFormat.format("HH:mm", new Date()).toString();

        if (!dayOfYear.equals(lastResetDay) && time.compareTo(sp.getString("time", "00:00")) >= 0) {
            resetProgress();
            lastResetDay = dayOfYear;
            spe.putString("date", lastResetDay.toString());
            spe.apply();
        }

        goalMsg = getString(R.string.goal_msg);
        goal = getString(R.string.goal) + units;
        today = getString(R.string.today) + units;

        ((TextView) findViewById(R.id.goal)).setText(goal.replace("XX", Integer.toString(waterGoal)));

        updateDisplay();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_intake, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(NavIntake.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_intake:
                // intentionally do nothing
                break;
            case R.id.nav_history:
                startActivity(new Intent(NavIntake.this, History.class));
                break;
            case R.id.nav_notif:
                startActivity(new Intent(NavIntake.this, notif.class));
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Called when the user taps the add button. */
    public void drinkWater(View view) {
        waterPrev = seek.getProgress();
        waterCur += seekbarMl ? Math.round(OZ_ML_CONVERT * waterPrev) : waterPrev;

        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt("waterCur", waterCur);
        spe.putInt("waterPrev", waterPrev);
        spe.commit();

        updateDisplay();
    }

    /** Called when the user taps the undo button. This works because waterPrev is always stored in oz. */
    public void unDrinkWater(View view) {
        waterCur -= seekbarMl ? Math.round(OZ_ML_CONVERT * waterPrev) : waterPrev;
        waterPrev = 0;

        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt("waterCur", waterCur);
        spe.putInt("waterPrev", waterPrev);
        spe.commit();

        updateDisplay();
    }

    /** Called when numbers change--shows the number change in strings and images. */
    private void updateDisplay() {
        /** Make sure we have the right water buddy image displaying */
        float pct_f = ((float)waterCur) / waterGoal;
        if (pct_f >= 1) {
            buddyView.setImageResource(R.drawable.wb4);
        } else if (pct_f >= 0.75){
            buddyView.setImageResource(R.drawable.wb3);
        } else if (pct_f >= 0.50){
            buddyView.setImageResource(R.drawable.wb2);
        } else if (pct_f >= 0.25){
            buddyView.setImageResource(R.drawable.wb1);
        } else {
            buddyView.setImageResource(R.drawable.wb0);
        }

        /** Update the text on the string. */
        int pct_i = (int) (100 * pct_f);
        ((TextView) findViewById(R.id.pctGoal)).setText(goalMsg.replace("XX", Integer.toString(pct_i)));
        ((TextView) findViewById(R.id.cur)).setText(today.replace("XX", Integer.toString(waterCur)));
    }

    /** Called when the user's day is over and progress must be reset. */
    private void resetProgress() {
        SharedPreferences sp = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spe = sp.edit();
        waterCur = 0;
        waterPrev = 0;
        spe.putInt("waterCur", 0);
        spe.putInt("waterPrev", 0);
        spe.commit();
    }
}