package com.lokate.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.lokate.demo.di.initKoin
import com.lokate.kmmsdk.LokateSDK

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin(LokateSDK.BeaconScannerType.IBeacon) //beacon scanner type
        setContent {
            App()
        }
    }
}
