package com.sambishopp.securenotes.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sambishopp.securenotes.R
import com.sambishopp.securenotes.fragments.PreferenceFragment

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorCardView)
        setContentView(R.layout.activity_preference)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_preference, PreferenceFragment())
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}