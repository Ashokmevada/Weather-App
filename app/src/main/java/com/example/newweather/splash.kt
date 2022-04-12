package com.example.newweather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

class splash : AppCompatActivity() {


    lateinit var mfusedlocation: FusedLocationProviderClient
    private var mycode=1010

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        window.statusBarColor= Color.parseColor("#6a1b9a")
        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)

        getLastlocation()


    }

    @SuppressLint("MissingPermission")
    private fun getLastlocation() {


        if(checkpermission()){

            if(LocationEnable()){

                mfusedlocation.lastLocation.addOnCompleteListener{
                        task->
                    var location: Location? = task.result
                    if(location==null){

                        newlocation()

                    }else{

                        Handler(Looper.getMainLooper()).postDelayed({

                            var intent = Intent(this,MainActivity::class.java)
                            intent.putExtra("lat",location.latitude.toString())
                            intent.putExtra("lon",location.longitude.toString())
                            startActivity(intent)
                            finish()
                        },1500)


                    }

                }

            }else{
                Toast.makeText(this,"Please Turn On The GPS", Toast.LENGTH_LONG).show()
            }

        }else{

            requestlocation()

        }



    }

    @SuppressLint("MissingPermission")
    private fun newlocation() {

        var locationrequest = LocationRequest()
        locationrequest.priority= LocationRequest.PRIORITY_HIGH_ACCURACY
        locationrequest.fastestInterval=0
        locationrequest.numUpdates=1
        locationrequest.interval=0
        mfusedlocation = LocationServices.getFusedLocationProviderClient(this)
        mfusedlocation.requestLocationUpdates(locationrequest,locationcallback, Looper.myLooper())

    }


    private val locationcallback=object : LocationCallback() {

        override fun onLocationResult(p0: LocationResult) {
            var lastlocation: Location =p0.lastLocation
        }

    }





    private fun requestlocation() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),
            mycode)

    }

    private fun checkpermission(): Boolean {

        if(
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ||  ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            return true
        }

        return false

    }

    private fun LocationEnable(): Boolean {

        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)|| locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode==mycode){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastlocation()
            }
        }
    }



}