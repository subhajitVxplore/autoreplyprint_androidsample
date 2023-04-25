package com.caysn.autoreplyprint.sample;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class StartActivity extends Activity {

    private static final int RequestCode_RequestAllPermissions = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableBluetooth();
        enableWiFi();
        enableLocation();

        if (hasAllPermissions()) {
            onPermissionGranted();
            finish();
        } else {
            requestAllPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RequestCode_RequestAllPermissions:
                if (hasAllPermissions()) {
                    onPermissionGranted();
                } else {
                    onPermissionGranted();
                }
                finish();
                break;
        }
    }

    private boolean hasAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasLocationPermission = (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            boolean hasCameraPermission = (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
            boolean hasStoragePermission = (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            return hasLocationPermission && hasCameraPermission && hasStoragePermission;
        }
        return true;
    }

    private void requestAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permissions[] = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
            };
            requestPermissions(permissions, RequestCode_RequestAllPermissions);
        }
    }

    private void onPermissionGranted() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void enableBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (null != adapter) {
            if (!adapter.isEnabled()) {
                if (!adapter.enable()) {
                    Toast.makeText(this, "Failed to enable bluetooth adapter", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void enableWiFi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (null != wifiManager) {
            if (!wifiManager.isWifiEnabled()) {
                if (!wifiManager.setWifiEnabled(true)) {
                    Toast.makeText(this, "Failed to enable wifi", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void enableLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        if (null != locationManager) {
            boolean gpsLocation = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkLocation = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!gpsLocation && !networkLocation) {
                Toast.makeText(this, "Please enable location else will not search ble printer", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
