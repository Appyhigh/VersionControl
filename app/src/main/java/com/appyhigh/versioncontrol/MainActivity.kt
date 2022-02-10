package com.appyhigh.versioncontrol

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.appyhigh.appupdatemanager.VersionControlConstants
import com.appyhigh.appupdatemanager.VersionControlListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var mFirebaseRemoteConfig: FirebaseRemoteConfig
    private val TAG = MainActivity::class.java.canonicalName
    private var currentVersion = ""
    private var criticalVersion = ""
    private var versionControlListener: VersionControlListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpFirebase()
    }

    private fun setUpFirebase() {
        versionControlListener = object : VersionControlListener {
            override fun onUpdateDetectionSuccess(updateType: VersionControlConstants.UpdateType) {
                Log.d("updateType", updateType.toString())
            }
        }
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val versionControl =
                        mFirebaseRemoteConfig.getString(VersionControlConstants.VERSION_CONTROL)
                    val versionControlJson = JSONObject(versionControl)
                    currentVersion =
                        versionControlJson.getString(VersionControlConstants.CURRENT_VERSION)
                    criticalVersion =
                        versionControlJson.getString(VersionControlConstants.CRITICAL_VERSION)
                    com.appyhigh.appupdatemanager.VersionControlSdk.initializeSdk(
                        this,
                        findViewById(R.id.tvhello),
                        currentVersion,
                        criticalVersion,
                        BuildConfig.VERSION_CODE,
                        versionControlListener
                    )
                } else {
                    Log.e(TAG, "Config params fetch error")
                }
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == com.appyhigh.appupdatemanager.VersionControlSdk.MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                com.appyhigh.appupdatemanager.VersionControlSdk.initializeSdk(
                    this,
                    findViewById(R.id.tvhello),
                    currentVersion,
                    criticalVersion,
                    BuildConfig.VERSION_CODE,
                    versionControlListener
                )
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }
}