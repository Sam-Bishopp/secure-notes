package com.sambishopp.securenotes.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sambishopp.securenotes.R

class PreferenceActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preference)

        if (savedInstanceState == null)
        {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_preference, MainPreference())
                .commit()
        }
        else
        {
            title = savedInstanceState.getCharSequence(TAG_TITLE)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0)
            {
                setTitle(R.string.settings_title)
            }
        }

        setUpToolbar()
    }

    private fun setUpToolbar() {
        val actionBar = supportActionBar

        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState.putCharSequence(TAG_TITLE, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if(supportFragmentManager.popBackStackImmediate())
        {
            return true
        }

        return super.onSupportNavigateUp()
    }

    class MainPreference : PreferenceFragmentCompat()
    {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.main_preferences, rootKey)
        }

    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat?, pref: Preference?): Boolean
    {
        //Initialise preferences fragment
        val args = pref?.extras

        val fragment = pref?.fragment?.let {
            supportFragmentManager.fragmentFactory.instantiate(classLoader, it).apply {
                arguments = args
                setTargetFragment(caller, PREFERENCE_FRAGMENT_REQUEST_CODE)
            }
        }

        fragment.let {
            if (it != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.content_preference, it)
                    .addToBackStack(null)
                    .commit()
            }
            title = pref?.title
            return true
        }
    }

    companion object
    {
        private const val TAG_TITLE = "PREFERENCE_ACTIVITY"
        private const val PREFERENCE_FRAGMENT_REQUEST_CODE = 0
    }
}