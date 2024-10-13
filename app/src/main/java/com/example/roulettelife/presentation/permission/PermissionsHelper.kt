package com.example.roulettelife.presentation.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.roulettelife.R

/**
 * パーミッション関連
 */
object PermissionsHelper {

    private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 1
    private const val GPS_ENABLE_REQUEST_CODE = 2
    private const val CAMERA_PERMISSION_REQUEST_CODE = 3

    // パーミッションの許可状態をログに出力する
    fun logPermissionStatus(context: Context) {
        val requiredPermissions = getAllRequiredPermissions()

        requiredPermissions.forEach { permission ->
            val isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            Log.d("PermissionStatus", "$permission granted: $isGranted")
        }
        // Bluetoothの有効状態もログに出力
        val bluetoothEnabled = isBluetoothEnabled()
        Log.d("PermissionStatus", "Bluetooth enabled: $bluetoothEnabled")
        val gpsEnabled = isGPSEnabled(context)
        Log.d("PermissionStatus", "GPS enabled: $gpsEnabled")
        // Android 10以上の場合、ACCESS_BACKGROUND_LOCATIONの許可状態をログに出力
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val backgroundLocationGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
            Log.d("PermissionStatus", "ACCESS_BACKGROUND_LOCATION : $backgroundLocationGranted")
        }
        // Android 13以上の場合、POST_NOTIFICATIONSの許可状態をログに出力
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermissionGranted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            Log.d("PermissionStatus", "POST_NOTIFICATIONS : $notificationPermissionGranted")
        }
        // カメラパーミッションの状態をログに出力
        val cameraPermissionGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        Log.d("PermissionStatus", "CAMERA : $cameraPermissionGranted")
    }

    private fun getRequiredBluetoothPermissions(): List<String> {
        val permissions = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        return permissions
    }

    fun getAllRequiredPermissions(): List<String> {
        val permissions = mutableListOf<String>()

        // Android 12以上の場合、Bluetooth関連のパーミッション
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
//            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
//            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
//        }

//        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
//        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        // Android 13以上の場合、通知関連のパーミッション
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Android 14以上の場合、FOREGROUND_SERVICE_CONNECTED_DEVICEパーミッション
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
//            permissions.add(Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE)
//        }

        // カメラパーミッションを追加
        permissions.add(Manifest.permission.CAMERA)

        return permissions
    }

    // 背景位置情報のパーミッションチェックとダイアログ表示
    @RequiresApi(Build.VERSION_CODES.Q)
    fun checkAndRequestBackgroundLocation(activity: Activity, launcher: ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            // パーミッションが許可されていない場合にダイアログを表示
            showBackgroundLocationDialog(activity, launcher)
        }
    }

    // Bluetoothに必要なすべてのパーミッションが許可されているかを確認
    fun Context.hasRequiredBluetoothPermissions(): Boolean {
        val requiredPermissions = getRequiredBluetoothPermissions()
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * BluetoothがONかどうかを判定
     */
    fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    /**
     * BluetoothをONにする
     */
    fun enableBluetooth(activity: Activity) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            arrayOf(Manifest.permission.BLUETOOTH_ADMIN)
        }

        if (permissions.all { ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED }) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, BLUETOOTH_PERMISSION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(activity, permissions, BLUETOOTH_PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * GPSがONかどうかを判定
     */
    fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * GPSをONにする
     */
    fun enableGPS(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.gps_dialog_title)
            .setMessage(R.string.gps_dialog_message)
            .setPositiveButton(R.string.gps_dialog_positive_button) { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivityForResult(intent, GPS_ENABLE_REQUEST_CODE)
            }
            .setNegativeButton(R.string.gps_dialog_negative_button) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun allPermissionsGranted(context: Context): Boolean {
        return getAllRequiredPermissions().all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 背景位置情報パーミッションをリクエストするダイアログ
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun showBackgroundLocationDialog(activity: Activity, launcher: ActivityResultLauncher<String>) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.request_background_dialog)
            .setMessage(R.string.request_background_dialog_message)
            .setPositiveButton(R.string.gps_dialog_positive_button) { _, _ ->
                launcher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            .setNegativeButton(R.string.gps_dialog_negative_button) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}