package com.febaisi.lnhelper

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import com.febaisi.lnhelper.databinding.ActivityMainBinding
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_main.*

const val PERMISSION_REQUEST_CODE = 1
const val PERMISSION_FULLY_GRANTED = 1
const val PERMISSION_FOREGROUND_GRANTED = 0
const val PERMISSION_DENIED = -1

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var permissionRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Adding Button Listeners
        buttonAskPermission.setOnClickListener {
            requestFullPermission()
        }
    }


    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionRequested = true
    }

    private fun permissionDeniedPopup() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_denied_title))
            .setMessage(getString(R.string.permission_denied_dont_ask))
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }

    private fun requestFullPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            PERMISSION_REQUEST_CODE)
    }

    private fun checkPermissions() {
        var currentPermission : Int

        val permissionAccessFineLocationApproved = ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (permissionAccessFineLocationApproved) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED

            if (backgroundLocationPermissionApproved) {
                binding.permissionStatus.text = getString(R.string.permission_all_the_time)
                currentPermission = PERMISSION_FULLY_GRANTED
            } else {
                binding.permissionStatus.text = getString(R.string.permission_while_in_foreground)
                currentPermission = PERMISSION_FOREGROUND_GRANTED
            }
        } else {
            binding.permissionStatus.text = getString(R.string.permission_not_granted)
            currentPermission = PERMISSION_DENIED
        }


        if (permissionRequested && !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) &&
            currentPermission != PERMISSION_FULLY_GRANTED) {
            permissionDeniedPopup()
            permissionRequested = false
        }
    }
}
