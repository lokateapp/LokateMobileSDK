package com.lokate.android.demo

import AndroidPermissionHandler
import PermissionHandler
import market.MarketApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    // private lateinit var beaconScanner: AndroidBeaconScanner
    private lateinit var permissionHandler: PermissionHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionHandler = AndroidPermissionHandler(this)
        // beaconScanner = AndroidBeaconScanner()

        setContent {
            MarketApp(permissionHandler)
        }
    }
}

// @Preview
// @Composable
// fun AppAndroidPreview() {
//     MarketApp()
// }