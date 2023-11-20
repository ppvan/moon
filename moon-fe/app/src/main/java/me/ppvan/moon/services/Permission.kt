package me.ppvan.moon.services

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import me.ppvan.moon.ui.activity.MainActivity
import me.ppvan.moon.utils.Eventer
import javax.inject.Inject
import javax.inject.Singleton

enum class PermissionEvents {
    MEDIA_PERMISSION_GRANTED
}

data class PermissionsState(
    val required: List<String>,
    val granted: List<String>,
    val denied: List<String>,
) {
    fun hasAll() = denied.isEmpty()
}

@Singleton
class PermissionsManager @Inject constructor() {
    val onUpdate = Eventer<PermissionEvents>()

    @RequiresApi(Build.VERSION_CODES.S)
    fun handle(activity: MainActivity) {
        val state = getState(activity)
        if (state.hasAll()) return

        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.count { it.value } > 0) {
                this.onUpdate.dispatch(PermissionEvents.MEDIA_PERMISSION_GRANTED)
                Log.i("INFO", "MEDIA_PERMISSION_GRANTED")
            }
        }.launch(state.denied.toTypedArray())
    }
    @RequiresApi(Build.VERSION_CODES.S)
    private fun getRequiredPermissions(): List<String> {
        val required = mutableListOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.MANAGE_MEDIA
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            required.add(Manifest.permission.READ_MEDIA_AUDIO)
            required.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            required.add(Manifest.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK)
        }

        return required
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getState(activity: MainActivity): PermissionsState {
        val required = getRequiredPermissions()
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()
        required.forEach {
            if (activity.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED) {
                granted.add(it)
            } else {
                denied.add(it)
            }
        }
        return PermissionsState(
            required = required,
            granted = granted,
            denied = denied,
        )
    }
}