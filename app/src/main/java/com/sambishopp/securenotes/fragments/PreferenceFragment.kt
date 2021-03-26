package com.sambishopp.securenotes.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.sambishopp.securenotes.R

class PreferenceFragment : PreferenceFragmentCompat() {

    companion object
    {
        const val lockTimeKey = "lockTimerLengthChoice" //Preferences key for lock time.
        const val lockTimeDefault: Long = 600000
    }

    //Shared Preferences
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor

    //private val notificationOption = sharedPreferences.getBoolean("notificationOnOff", true)

    private val lockTimerOnOff: SwitchPreferenceCompat? = findPreference("lockTimerOnOff")
    private val notificationOnOff: SwitchPreferenceCompat? = findPreference("notificationOnOff")

    private val lockTimerLengthPreference: ListPreference? = findPreference("lockTimerLengthChoice")
    //private val lockTimeCurrentValue = lockTimerLengthPreference?.value

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)


    }

    @SuppressLint("CommitPrefEdits")
    override fun onAttach(context: Context) {
        super.onAttach(context)

        sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE) //PreferenceManager.getDefaultSharedPreferences(activity) //requireActivity().getPreferences(Context.MODE_PRIVATE)
        prefsEditor = sharedPreferences.edit()
    }

    override fun onResume()
    {
        super.onResume()

        //Lock timer on/off
        lockTimerOnOff?.summaryOff = "This is off"
        lockTimerOnOff?.summaryOn = "This is on"
        //var lockTimerIsChecked = sharedPreferences.getBoolean("lockTimerOnOff", true)

        //lockTimerChanged()
    }

    /*private fun lockTimerChanged()
    {
        when {
            lockTimeCurrentValue.equals("1") //5 Minutes
            -> {
                prefsEditor.putLong(lockTimeKey, 300000)
                prefsEditor.apply()
                Log.e("Preference1", sharedPreferences.getLong(lockTimeKey, lockTimeDefault).toString())
            }
            lockTimeCurrentValue.equals("2") //10 Minutes
            -> {
                prefsEditor.putLong(lockTimeKey, 600000)
                prefsEditor.apply()
                Log.e("Preference1", sharedPreferences.getLong(lockTimeKey, lockTimeDefault).toString())
            }
            lockTimeCurrentValue.equals("3") //20 Minutes
            -> {
                prefsEditor.putLong(lockTimeKey, 1200000)
                prefsEditor.apply()
                Log.e("Preference1", sharedPreferences.getLong(lockTimeKey, lockTimeDefault).toString())
            }
            lockTimeCurrentValue.equals("4") //30 Minutes
            -> {
                prefsEditor.putLong(lockTimeKey, 1800000)
                prefsEditor.apply()
                Log.e("Preference1", sharedPreferences.getLong(lockTimeKey, lockTimeDefault).toString())
            }
            lockTimeCurrentValue.equals("5") //1 Hour
            -> {
                prefsEditor.putLong(lockTimeKey, 3600000)
                prefsEditor.apply()
                Log.e("Preference1", sharedPreferences.getLong(lockTimeKey, lockTimeDefault).toString())
            }
        }
    }*/
}