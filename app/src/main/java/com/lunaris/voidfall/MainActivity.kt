package com.lunaris.voidfall
import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
class MainActivity:Activity(){
 private lateinit var hub:StationHubView
 override fun onCreate(s:Bundle?){super.onCreate(s);window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);hub=StationHubView(this);setContentView(hub)}
 override fun onResume(){super.onResume();if(::hub.isInitialized)hub.resume()}
 override fun onPause(){if(::hub.isInitialized)hub.pause();super.onPause()}
}