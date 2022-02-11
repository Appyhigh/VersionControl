package com.appyhigh.versioncontrol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appyhigh.appupdatemanager.VersionControlConstants
import com.appyhigh.appupdatemanager.VersionControlListener
import com.appyhigh.appupdatemanager.VersionControlSdk

class MainActivity : AppCompatActivity() {
    private var versionControlListener: VersionControlListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        versionControlListener = object : VersionControlListener {
            override fun onUpdateDetectionSuccess(updateType: VersionControlConstants.UpdateType) {
                when (updateType) {
                    VersionControlConstants.UpdateType.SOFT_UPDATE -> {
                    }
                    VersionControlConstants.UpdateType.HARD_UPDATE -> {
                    }
                    else -> {
                    }
                }
            }
        }
        VersionControlSdk.initializeSdk(
            this,
            findViewById(R.id.tvhello),
            BuildConfig.VERSION_CODE,
            versionControlListener
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VersionControlSdk.MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                VersionControlSdk.initializeSdk(
                    this,
                    findViewById(R.id.tvhello),
                    BuildConfig.VERSION_CODE,
                    versionControlListener
                )
            }
        }
    }
}