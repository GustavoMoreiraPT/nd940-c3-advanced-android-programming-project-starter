package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    companion object {
        const val STATUS = "status"
        const val FILE_NAME = "file_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        if (intent.hasExtra(STATUS)) {
            if(intent.getBooleanExtra(STATUS, false)) {
                status.text = getString(R.string.status_success)
            } else {
                status.text = getString(R.string.status_fail)
            }
        }
        if (intent.hasExtra(FILE_NAME)) {
            name.text = intent.getStringExtra(FILE_NAME)
        }
    }

}
