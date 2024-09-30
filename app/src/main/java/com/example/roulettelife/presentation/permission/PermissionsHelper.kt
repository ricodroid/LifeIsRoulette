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
 * パーミッションを要求するためのヘルパー関数
 */
object PermissionsHelper {

    const val BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE = 1
    private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 2
    private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 3
    private const val FOREGROUND_SERVICE_LOCATION_PERMISSION_REQUEST_CODE = 4
    private const val GPS_ENABLE_REQUEST_CODE = 5
    const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 6


    /**
     * すべての必要なパーミッションをリクエストする
     *
     * @param context リクエストを行うコンテキスト
     * @param launcher パーミッションリクエストを処理するランチャー
     */
    fun requestAllPermissionsWithLauncher(activity: Activity, launcher: ActivityResultLauncher<Array<String>>) {
        val permissions = getRequiredBluetoothPermissions().toTypedArray()
        launcher.launch(permissions)
    }

    /**
     * 現在の[Context]が指定された[Manifest.permission]を持っているかどうかを判定する
     *
     * @param permissionType チェックするパーミッションの種類
     * @return パーミッションが付与されている場合はtrue そうでなければfalse
     */
    private fun Context.hasPermission(permissionType: String): Boolean {
        Log.d("hasPermission",
            "${permissionType}==${
                ContextCompat.checkSelfPermission(this, permissionType) ==
                    PackageManager.PERMISSION_GRANTED}")
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    /**
     * 現在の[Context]がAndroidバージョンに応じてBluetooth操作を行うために必要な
     * パーミッションを持っているかどうかを判定する
     *
     * @return パーミッションが付与されている場合はtrue そうでなければfalse
     */
    fun Context.hasRequiredBluetoothPermissions(): Boolean {
        return getRequiredBluetoothPermissions().all { hasPermission(it) }
    }

    /**
     * Android 14（API レベル UPSIDE_DOWN_CAKE）以降のデバイスで
     * フォアグラウンドサービス位置情報のパーミッションを持っているかどうかを判定する
     *
     * Android 14未満のデバイスでは、このパーミッションは不要なので常にtrueを返す
     *
     * @return パーミッションが付与されている場合はtrue そうでなければfalse
     */
    private fun Context.hasForegroundServiceLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            hasPermission(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        } else {
            true
        }
    }

    /**
     * Android 10（API レベル Q）以降のデバイスでバックグラウンド位置情報のパーミッションを持っているかどうかを判定する
     *
     * Android 10未満のデバイスでは、このパーミッションは不要なので常にtrueを返す
     *
     * @return パーミッションが付与されている場合はtrue そうでなければfalse
     */
    private fun Context.hasBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            hasPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            true
        }
    }

    /**
     * Bluetooth操作関連に必要なパーミッションのリストを取得する
     *
     * @return 必要なパーミッションのリスト
     */
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


    /**
     * すべての必要なパーミッションをリクエストする
     *
     * @param activity リクエストを行うアクティビティ
     */
    fun requestAllPermissions(activity: Activity) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                requestNearbyDevicesPermissions(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE)
                requestNotificationPermission(activity, NOTIFICATION_PERMISSION_REQUEST_CODE)
                requestForegroundServiceLocationPermission(activity, FOREGROUND_SERVICE_LOCATION_PERMISSION_REQUEST_CODE)
                requestLocationPermissions(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE) // 初回リクエスト
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                requestNearbyDevicesPermissions(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE)
                requestNotificationPermission(activity, NOTIFICATION_PERMISSION_REQUEST_CODE)
                requestLocationPermissions(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE) // 初回リクエスト
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                requestNearbyDevicesPermissions(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE)
                requestLocationPermissions(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE) // 初回リクエスト
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                requestLocationPermissions(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE) // 初回リクエスト
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                requestLocationPermission(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE)
            }
            else -> {
                requestLocationPermission(activity, BLUETOOTH_LOCATION_PERMISSION_REQUEST_CODE)
            }
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
     * GPSがONかどうかを判定
     */
    fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    /**
     * 位置情報アクセスのパーミッションをユーザーにリクエストする
     *
     * @param activity リクエストを行うアクティビティ
     * @param requestCode リクエストを識別するためのコード
     */
    private fun requestLocationPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            requestCode
        )
    }

    /**
     * Android10以降のデバイスで位置情報アクセスのパーミッションをリクエストする
     *
     * @param activity リクエストを行うアクティビティ
     * @param requestCode リクエストを識別するためのコード
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestLocationPermissions(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            requestCode
        )
    }

    /**
     * Android11以降のデバイスでバックグラウンド位置情報のパーミッションをリクエストする
     *
     * @param activity リクエストを行うアクティビティ
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun requestBackgroundLocationPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    /**
     * Android 12（API レベル S）以降のデバイスでBluetoothスキャンと接続、位置情報アクセスのパーミッションをリクエストする
     *
     * @param activity リクエストを行うアクティビティ
     * @param requestCode リクエストを識別するためのコード
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestNearbyDevicesPermissions(activity: Activity, requestCode: Int) {
        val permissions = getRequiredBluetoothPermissions().toTypedArray()
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
    }

    /**
     * Android 14（API レベル UPSIDE_DOWN_CAKE）以降のデバイスでフォアグラウンドサービス位置情報の
     * パーミッションをリクエストする
     *
     * `FOREGROUND_SERVICE_LOCATION` パーミッションをユーザーに要求する
     *
     * @param activity リクエストを行うアクティビティ
     * @param requestCode リクエストを識別するためのコード
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun requestForegroundServiceLocationPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.FOREGROUND_SERVICE_LOCATION),
            requestCode
        )
    }

    /**
     * Android 13（API レベル TIRAMISU）以降のデバイスでフォアグラウンドサービス通知のパーミッションをリクエストする
     *
     * `POST_NOTIFICATIONS`パーミッションをユーザーに要求する
     *
     * @param activity リクエストを行うアクティビティ
     * @param requestCode リクエストを識別するためのコード
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestNotificationPermission(activity: Activity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            requestCode
        )
    }

    /**
     * Bluetooth操作を行うために必要なパーミッションとフォアグラウンドサービス位置情報のパーミッションを持っているかどうかを判定する
     */
    fun hasAllPermissions(context: Context): Boolean {
        return context.hasRequiredBluetoothPermissions() && context.hasForegroundServiceLocationPermission() && context.hasBackgroundLocationPermission()
    }

    /**
     * 全てのパーミッションリクエストが許可されたことを確認する
     */
    fun allPermissionsGranted(grantResults: IntArray): Boolean {
        return grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
    }
}