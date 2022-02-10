package com.appyhigh.appupdatemanager

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

@SuppressLint("StaticFieldLeak")
object VersionControlSdk {


    /**
     * If BuildVersion < currentVersion && BuildVersion >= criticalVersion -> SOFT_UPDATE (Eg: BuildVersion:101, CurrentVersion:102, CriticalVersion:100)
     * if BuildVersion < currentVersion && BuildVersion < CriticalVersion -> HARD_UPDATE
     * else NO_UPDATE
     */
    private var appUpdateManager: AppUpdateManager? = null
    var MY_REQUEST_CODE = 0x121212
    lateinit var view: View
    var firstRequest = true
    fun initializeSdk(
        context: Activity,
        view: View,
        currentVersion: String,
        criticalVersion: String,
        buildVersion: Int,
        versionControlListener: VersionControlListener?
    ) {
        appUpdateManager = AppUpdateManagerFactory.create(context)
        this.view = view
        if (buildVersion < currentVersion.toInt()) {
            when {
                buildVersion >= criticalVersion.toInt() -> {
                    Log.d("initializeSdk", "SOFT_UPDATE")
                    if (firstRequest) {
                        checkUpdate(context, FLEXIBLE, versionControlListener)
                        firstRequest = false
                    }
                }
                buildVersion < criticalVersion.toInt() -> {
                    Log.d("initializeSdk", "HARD_UPDATE")
                    checkUpdate(context, IMMEDIATE, versionControlListener)
                }
                else -> {
                    Log.d("initializeSdk", "NO_UPDATE")
                    versionControlListener?.onUpdateDetectionSuccess(VersionControlConstants.UpdateType.NO_UPDATE)

                }
            }
        } else {
            Log.d("initializeSdk", "NO_UPDATE")
            versionControlListener?.onUpdateDetectionSuccess(VersionControlConstants.UpdateType.NO_UPDATE)
        }
    }

    private fun checkUpdate(
        context: Activity,
        updateType: Int,
        versionControlListener: VersionControlListener?
    ) {
        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo
        // Checks that the platform will allow the specified type of update.
        Log.d("checkUpdate", "Checking for updates")
        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo ->
            Log.d("checkUpdate", appUpdateInfo.updateAvailability().toString())
            Log.d("checkUpdate", updateType.toString())
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(updateType)
            ) {
                // Request the update.
                if (updateType == IMMEDIATE) {
                    versionControlListener?.onUpdateDetectionSuccess(VersionControlConstants.UpdateType.HARD_UPDATE)
                } else {
                    versionControlListener?.onUpdateDetectionSuccess(VersionControlConstants.UpdateType.SOFT_UPDATE)
                }
                appUpdateManager!!.registerListener(listener!!)
                appUpdateManager!!.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    appUpdateInfo,
                    // The current activity making the update request.
                    context,
                    // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                    // flexible updates.
                    AppUpdateOptions.newBuilder(updateType)
                        .setAllowAssetPackDeletion(true)
                        .build(),
                    // Include a request code to later monitor this update request.
                    MY_REQUEST_CODE
                )
            } else {
                Log.d("checkUpdate", "No Update available")
                versionControlListener?.onUpdateDetectionSuccess(VersionControlConstants.UpdateType.NO_UPDATE)
            }
        }
    }

    private val listener: InstallStateUpdatedListener? =
        InstallStateUpdatedListener { installState ->
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                // After the update is downloaded, show a notification
                // and request user confirmation to restart the app.
                showSnackBarForCompleteUpdate()
            }
        }

    private fun showSnackBarForCompleteUpdate() {
        Log.d("listener", "An update has been downloaded")
        appUpdateManager!!.unregisterListener(listener!!)
        Snackbar.make(
            view,
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("RESTART") { appUpdateManager!!.completeUpdate() }
            setActionTextColor(Color.WHITE)
            show()
        }
    }
}