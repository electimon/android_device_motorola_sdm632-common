/*
* Copyright (C) 2016 The OmniROM Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package com.moto.actions;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import android.app.ActionBar;
import com.moto.actions.util.SeekBarPreference;
import com.moto.actions.R;

public class VibrationCalibration extends PreferenceActivity implements
        OnPreferenceChangeListener {

    public static final String KEY_VIBRATION_ENABLED = "vibration_enabled";
    public static final String KEY_VIBRATION_AUTOCAL_ONE = "vibration_autocal_one";
    public static final String KEY_VIBRATION_AUTOCAL_TWO = "vibration_autocal_two";
    public static final String KEY_VIBRATION_AUTOCAL_THREE = "vibration_autocal_three";

    private SeekBarPreference mVibrationAutocalOne;
    private SeekBarPreference mVibrationAutocalTwo;
    private SeekBarPreference mVibrationAutocalThree;
    private SwitchPreference mVibrationEnabled;
    private SharedPreferences mPrefs;
    private boolean mEnabled;

    private String mOne;
    private String mTwo;
    private String mThree;

    private static final String VIBRATION_AUTOCAL_FILE = "/sys/class/leds/vibrator/device/autocal";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.vibration_cal);

        addPreferencesFromResource(R.xml.vibration_calibration);

        mVibrationAutocalOne = (SeekBarPreference) findPreference(KEY_VIBRATION_AUTOCAL_ONE);
        mVibrationAutocalOne.setInitValue(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_ONE, mVibrationAutocalOne.def));
        mVibrationAutocalOne.setOnPreferenceChangeListener(this);

        mVibrationAutocalTwo = (SeekBarPreference) findPreference(KEY_VIBRATION_AUTOCAL_TWO);
        mVibrationAutocalTwo.setInitValue(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_TWO, mVibrationAutocalTwo.def));
        mVibrationAutocalTwo.setOnPreferenceChangeListener(this);

        mVibrationAutocalThree = (SeekBarPreference) findPreference(KEY_VIBRATION_AUTOCAL_THREE);
        mVibrationAutocalThree.setInitValue(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_THREE, mVibrationAutocalThree.def));
        mVibrationAutocalThree.setOnPreferenceChangeListener(this);

        mOne = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_ONE, mVibrationAutocalOne.def));
        mTwo = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_TWO, mVibrationAutocalTwo.def));
        mThree = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_THREE, mVibrationAutocalThree.def));

    }

    private boolean isSupported(String file) {
        return UtilsKCAL.fileWritable(file);
    }

    public static void restore(Context context) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.vibration_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_reset:
                reset();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void reset() {
        int one = mVibrationAutocalOne.reset();
        int two = mVibrationAutocalTwo.reset();
        int three = mVibrationAutocalThree.reset();

        mPrefs.edit().putInt(KEY_VIBRATION_AUTOCAL_ONE, one).commit();
        mPrefs.edit().putInt(KEY_VIBRATION_AUTOCAL_TWO, two).commit();
        mPrefs.edit().putInt(KEY_VIBRATION_AUTOCAL_THREE, three).commit();

        String storedValue = Integer.toString(one) + " " + Integer.toString(two) + " " +  Integer.toString(three);

        UtilsKCAL.writeValue(VIBRATION_AUTOCAL_FILE, storedValue);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mVibrationEnabled) {
            Boolean enabled = (Boolean) newValue;
            mPrefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).commit();
            mOne = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_ONE, 10));
            mTwo = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_TWO, 127));
            mThree = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_THREE, 10));
            String storedValue = ((String) String.valueOf(mOne)
                   + " " + String.valueOf(mTwo) + " " +  String.valueOf(mThree));
            UtilsKCAL.writeValue(VIBRATION_AUTOCAL_FILE, storedValue);
            return true;
        } else if (preference == mVibrationAutocalOne) {
            float val = Float.parseFloat((String) newValue);
            mPrefs.edit().putInt(KEY_VIBRATION_AUTOCAL_ONE, (int) val).commit();
            mTwo = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_TWO, 127));
            mThree = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_THREE, 10));
            String strVal = ((String) newValue + " " + mTwo + " " +mThree);
            UtilsKCAL.writeValue(VIBRATION_AUTOCAL_FILE, strVal);
            return true;
        } else if (preference == mVibrationAutocalTwo) {
            float val = Float.parseFloat((String) newValue);
            mPrefs.edit().putInt(KEY_VIBRATION_AUTOCAL_TWO, (int) val).commit();
            mOne = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_ONE, 10));
            mThree = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_THREE, 10));
            String strVal = ((String) mOne + " " + newValue + " " +mThree);
            UtilsKCAL.writeValue(VIBRATION_AUTOCAL_FILE, strVal);
            return true;
        } else if (preference == mVibrationAutocalThree) {
            float val = Float.parseFloat((String) newValue);
            mPrefs.edit().putInt(KEY_VIBRATION_AUTOCAL_THREE, (int) val).commit();
            mOne = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_ONE, 10));
            mTwo = String.valueOf(mPrefs.getInt(KEY_VIBRATION_AUTOCAL_TWO, 127));
            String strVal = ((String) mOne + " " + mTwo + " " +newValue);
            UtilsKCAL.writeValue(VIBRATION_AUTOCAL_FILE, strVal);
            return true;
        }
        return false;
    }
}

