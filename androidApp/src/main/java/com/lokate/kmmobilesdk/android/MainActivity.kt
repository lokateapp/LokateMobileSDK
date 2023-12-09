package com.lokate.kmmobilesdk.android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.lokate.kmmobilesdk.Greeting
import com.lokate.kmmsdk.AndroidBeaconScanner
import com.lokate.kmmsdk.utils.extension.emptyString

class MainActivity : ComponentActivity() {

    private lateinit var bluetoothScanner: AndroidBeaconScanner

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getPermissionsS(afterGranted: () -> Unit = {}) {
        val requiredPermissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH
        )
        val permissions = mutableListOf(false, false, false, false)

        while (!permissions.all { it }) {
            requiredPermissions.forEachIndexed { index, permission ->
                permissions[index] =
                    checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }

            permissions.filter { !it }.forEachIndexed { index, b ->

                requestPermissions(arrayOf(requiredPermissions[index]), 0)

            }

        }
        afterGranted()

    }

    private fun getPermissions(afterGranted: () -> Unit = {}) {
        //check API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return getPermissionsS(afterGranted)
        }
        val requiredPermissions = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.BLUETOOTH
        )
        val permissions = mutableListOf(false, false)
        while (!permissions.all { it }) {
            requiredPermissions.forEachIndexed { index, permission ->
                permissions[index] =
                    checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED
            }

            permissions.filter { !it }.forEachIndexed { index, b ->
                requestPermissions(arrayOf(requiredPermissions[index]), 0)
            }

        }
        afterGranted()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //very very bad practice, we should move it to a viewmodel
        val textState = mutableStateOf("")
        bluetoothScanner = AndroidBeaconScanner()
        fun a() = run {
            bluetoothScanner.observeResults().watch {
                textState.value = it.toString()
            }
        }
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    GreetingView(Greeting().greet(), ::getPermissions, {
                        bluetoothScanner.start("123")
                        a()
                    }, textState)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bluetoothScanner.stop()
    }
}

@Composable
fun GreetingView(
    text: String,
    permissionCheck: (() -> Unit) -> Unit,
    startScan: () -> Unit,
    textState: State<String> = mutableStateOf(emptyString())
) {

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Text(text = text)

        }
        item {
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                permissionCheck(startScan)
            }) {

            }
        }
        item {
            Text(text = textState.value)
        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView("Hello, Android!", {}, {})
    }
}
