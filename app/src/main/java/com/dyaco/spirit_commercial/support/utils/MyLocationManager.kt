package com.dyaco.spirit_commercial.support.utils



class MyLocationManager {

//    companion object {
//
//        @JvmStatic //讓java呼叫時 不用加上 companion (MyLocationManager.companion.bindLocationListenerIn)
//        fun bindLocationListenerIn(
//            lifecycleOwner: LifecycleOwner,
//            listener: LocationListener, context: Context
//        ) {
//            MyLocationListener(lifecycleOwner, listener, context)
//        }
//    }
//
//    @SuppressWarnings("MissingPermission")
//    internal class MyLocationListener(
//        lifecycleOwner: LifecycleOwner,
//        private val listener: LocationListener, private val context: Context
//    ) : LifecycleObserver {
//
//        private var locationManager: LocationManager? = null
//
//        init {
//            lifecycleOwner.lifecycle.addObserver(this)
//        }
//
//        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
//        fun addLocationListener() {
//            locationManager =
//                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//            locationManager?.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0f, listener )
//
//            println("Listener added")
//
//            //取得Location
//            val lastLocation = locationManager?.getLastKnownLocation(
//                LocationManager.GPS_PROVIDER
//            )
//
//            //通知location
//            if (lastLocation != null) {
//                listener.onLocationChanged(lastLocation)
//            }
//        }
//
//        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
//        fun removeLocationListener() {
//            if (locationManager == null) {
//                return
//            }
//            locationManager!!.removeUpdates(listener)
//            locationManager = null
//            println("Listener removed")
//        }
//
//    }
}