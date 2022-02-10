package com.appyhigh.appupdatemanager

interface VersionControlListener {
    fun onUpdateDetectionSuccess(updateType: VersionControlConstants.UpdateType)
}