package es.ua.eps.filmoteca.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityMapsBinding
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_DIRECTOR
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_LATITUDE
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_LONGITUDE
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_TITLE
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_YEAR

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.includeAppbar.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latitude    = intent.getDoubleExtra(EXTRA_FILM_LATITUDE, 0.0)
        val longitude   = intent.getDoubleExtra(EXTRA_FILM_LONGITUDE, 0.0)
        val title       = intent.getStringExtra(EXTRA_FILM_TITLE)
        val director    = intent.getStringExtra(EXTRA_FILM_DIRECTOR)
        val year        = intent.getIntExtra(EXTRA_FILM_YEAR, 0)

        val place = LatLng(latitude, longitude)
        val marker = mMap.addMarker(MarkerOptions()
            .position(place)
            .title(title)
            .snippet("$director, $year")
        )

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 16f))
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        marker?.showInfoWindow()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId){
            android.R.id.home -> finish()
        }

        return false
    }
}