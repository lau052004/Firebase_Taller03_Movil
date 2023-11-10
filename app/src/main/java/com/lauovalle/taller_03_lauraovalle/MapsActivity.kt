package com.lauovalle.taller_03_lauraovalle

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.User
import com.lauovalle.taller_03_lauraovalle.FirebaseModel.UsuariosActivos
import com.lauovalle.taller_03_lauraovalle.databinding.ActivityMapsBinding
import java.util.logging.Logger

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        val TAG: String = MapsActivity::class.java.name
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var latLngActual: LatLng
    private lateinit var latLngOtro: LatLng
    private var userLocationMarker: Marker? = null

    private val logger = Logger.getLogger(TAG)

    // Permission handler
    private val getSimplePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) {
        updateUI(it)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        dbRef = FirebaseDatabase.getInstance().getReference("Usuarios")

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        val key = intent.getStringExtra("nombre")
        binding.textView.text = key

        // Verificar permisos
        mMap.moveCamera(CameraUpdateFactory.zoomTo(20f))
        mMap.uiSettings.setAllGesturesEnabled(true)
        /// Add UI controls
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        verifyPermissions(this, android.Manifest.permission.ACCESS_FINE_LOCATION, "El permiso es requerido para poder mostrar tu ubicación en el mapa")
        obtenerUbicacion(mAuth.currentUser?.uid,key)

    }

    fun obtenerUbicacion(usuarioActual: String?, key: String?){
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Toast.makeText(this@MapsActivity, "datos an cambiado", Toast.LENGTH_SHORT).show()
                for (userSnapshot in dataSnapshot.children) {
                    val usuario = userSnapshot.getValue(User::class.java)
                    // Guardar latitud y longitud del otroUsuario
                    if (usuario != null && usuario.correo == key) {
                        val latitud = usuario.latitud
                        val longitud = usuario.longitud
                        latLngActual = LatLng(latitud.toDouble(), longitud.toDouble())
                        mMap.addMarker(
                            MarkerOptions().position(latLngActual)
                                .title("posición otro")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngActual, 15f))
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Maneja errores si es necesario
                Toast.makeText(this@MapsActivity, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun verifyPermissions(context: Context, permission: String, rationale: String) {
        when {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                Snackbar.make(binding.root, "Ya tengo los permisos 😜", Snackbar.LENGTH_LONG).show()
                updateUI(true)
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // We display a snackbar with the justification for the permission, and once it disappears, we request it again.
                val snackbar = Snackbar.make(binding.root, rationale, Snackbar.LENGTH_LONG)
                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar, event: Int) {
                        if (event == DISMISS_EVENT_TIMEOUT) {
                            getSimplePermission.launch(permission)
                        }
                    }
                })
                snackbar.show()
            }
            else -> {
                getSimplePermission.launch(permission)
            }
        }
    }
//comment
    @SuppressLint("MissingPermission")
    fun updateUI(permission: Boolean) {
        if (permission) {
            // granted
            logger.info("Permission granted")

            var fusedLocationClient: FusedLocationProviderClient
            var locationCallback: LocationCallback
            var polylineOptions = PolylineOptions()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val ubicacion = LatLng(location.latitude, location.longitude)
                    if (userLocationMarker == null) {
                        userLocationMarker = mMap.addMarker(
                            MarkerOptions().position(ubicacion)
                                .title("Marker in my actual position ${location.latitude} ${location.longitude}")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion))
                    } else {
                        // Actualiza la posición del marcador existente
                        userLocationMarker?.position = ubicacion
                    }
                }
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.locations.forEach { location ->
                        // Obtén la nueva ubicación
                        val latLng = LatLng(location.latitude, location.longitude)
                        if (userLocationMarker == null) {
                            userLocationMarker = mMap.addMarker(
                                MarkerOptions().position(latLng)
                                    .title("Marker in my actual position ${location.latitude} ${location.longitude}")
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            )
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        } else {
                            // Actualiza la posición del marcador existente
                            userLocationMarker?.position = latLng
                        }
                    }
                }
            }

            val locationRequest = LocationRequest.create().apply {
                interval = 10000 // Intervalo de actualización de ubicación en milisegundos
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            // Verifica la configuración de ubicación
            val client = LocationServices.getSettingsClient(this)
            val task = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener {
                // Configuración de ubicación aceptada, comienza la actualización
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            }

        } else {
            logger.warning("Permission denied")
        }
    }
}