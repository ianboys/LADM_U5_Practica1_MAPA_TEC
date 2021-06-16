package mx.tecnm.tepic.ladm_u5_practica1_mapa_tec

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    //private lateinit var mMap2: GoogleMap
    var nombreEdif = ""
    var pos1lat = 0.0
    var pos1lon = 0.0

    var latitudAct = 0.0
    var longitudAct = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        var extra = intent.extras

        nombreEdif = extra!!.getString("edifNombre")!!
        pos1lat = extra!!.getDouble("edifPos1lat")!!
        pos1lon = extra!!.getDouble("edifPos1lon")!!

        latitudAct = extra!!.getDouble("actualLat")!!
        longitudAct = extra!!.getDouble("actualLon")!!

        println("-------------------------------------------------------------------------------------------")
        //println(nombreEdif)
        //println(pos1lat)
        //println(pos1lon)
        //println(pos1lon)

        println(latitudAct)
        println(longitudAct)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Edificio buscado and move the camera
        val edficioBuscado = LatLng(pos1lat, pos1lon)
        mMap.addMarker(MarkerOptions().position(edficioBuscado).title("${nombreEdif}"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edficioBuscado,17.0f))
        mMap.uiSettings.isZoomControlsEnabled = true

        val posicionActual = LatLng(latitudAct, longitudAct)
        mMap.addMarker(MarkerOptions().position(posicionActual).title("Posicion Actual"))
    }
}