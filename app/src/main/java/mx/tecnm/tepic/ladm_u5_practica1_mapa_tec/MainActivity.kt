package mx.tecnm.tepic.ladm_u5_practica1_mapa_tec

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    lateinit var locacion : LocationManager
    var bandera = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }

        baseRemota.collection("tecnologico")
                .addSnapshotListener { value, error ->
                    if (error != null){
                        return@addSnapshotListener
                    }
                    posicion.clear()
                    for (document in value!!){
                        var data = Data()
                        data.nombre = document.getString("nombre").toString()
                        data.posicion1 = document.getGeoPoint("posicion1")!!
                        data.posicion2 = document.getGeoPoint("posicion2")!!

                        if (data.nombre.equals("Centro de Informacion") || data.nombre.equals("DIRECCION") ){
                            data.areas = document.get("Areas").toString()
                        }
                        posicion.add(data)
                    }
                }

        locacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        locacion.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 01f, oyente)

        btnBuscar.setOnClickListener {
            var edif = obtenerLocalizacion(txtEdificio.text.toString())

            var latitud = (edif.posicion1.latitude+edif.posicion2.latitude)/2
            var longitud = (edif.posicion1.longitude+edif.posicion2.longitude)/2

            if (bandera){
                var intent = Intent(this,MapsActivity::class.java)
                intent.putExtra("edifNombre",edif.nombre)
                intent.putExtra("edifPos1lat",latitud)
                intent.putExtra("edifPos1lon",longitud)

                intent.putExtra("actualLat",txtlatitud.text.toString().toDouble())
                intent.putExtra("actualLon",txtlongitud.text.toString().toDouble())

                startActivity(intent)
            }
        }
    }

    private fun obtenerLocalizacion(edificio: String) : Data {
        var edificioElegido = Data()

        var id = posicion.indexOfFirst { it.nombre == edificio }
        if (id == -1){
            mensaje("ERROR: Edificio no encontrado")
            bandera = false
            txtEdificio.setText("")
        }
        else{
            edificioElegido = posicion.get(id)
            bandera = true
        }
        return edificioElegido
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage(s)
                .setPositiveButton("OK"){ d,i-> }
                .show()
    }
}

class Oyente(puntero:MainActivity) : LocationListener {
    var p = puntero

    override fun onLocationChanged(location: Location) {
        p.txtlatitud.setText("${location.latitude}")
        p.txtlongitud.setText("${location.longitude}")
        p.textView3.setText("")
        var geoPosicionGPS = GeoPoint(location.latitude, location.longitude)

        for (item in p.posicion){
            if (item.estoyEn(geoPosicionGPS)){
                if (item.nombre.equals("Centro de Informacion") || item.nombre.equals("DIRECCION")){
                    p.textView3.setText("Estas en ${item.nombre}\nAreas:\n ${item.areas}")
                }else{
                    p.textView3.setText("Estas en ${item.nombre}")
                }

            }
        }
    }
}