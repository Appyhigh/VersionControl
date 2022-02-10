package com.appyhigh.appupdatemanager

object VersionControlConstants {
    const val VERSION_CONTROL ="version_control"
    const val CURRENT_VERSION ="current_version"
    const val CRITICAL_VERSION ="critical_version"

    enum class UpdateType{
        SOFT_UPDATE,
        HARD_UPDATE,
        NO_UPDATE
    }
}