package es.ua.eps.filmoteca.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import coil.load
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import es.ua.eps.filmoteca.Film
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.GeofenceBroadcastReceiver
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_POSITION
import java.util.concurrent.TimeUnit

class FilmEditActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFilmEditBinding
    private lateinit var geofencingClient: GeofencingClient

    val REQUEST_IMAGE_CAPTURE   = 1
    val REQUEST_MEDIA_FILE      = 2
    var ACTUAL_REQUEST_CODE     = 0

    private val REQUEST_CODE_FOREGROUND = 1001
    private val REQUEST_CODE_BACKGROUND = 1002

    private val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = TimeUnit.MINUTES.toMillis(1)

    private var selectedFilm: Film? = null

    private val geofenceList: MutableList<Geofence> = mutableListOf()

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {result: ActivityResult ->
            onActivityResult(ACTUAL_REQUEST_CODE, result.resultCode, result.data)
        }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedFilm = FilmDataSource.films[intent.getIntExtra(EXTRA_FILM_POSITION, 0)]

        binding = ActivityFilmEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.includeAppbar.toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        geofencingClient = LocationServices.getGeofencingClient(this)

        this.selectedFilm = selectedFilm

        generateBindings()
    }

    private fun generateBindings(){
        binding.filmSave.setOnClickListener{

            if(selectedFilm != null) {
                // Retrieve fields data and save them to the selectedFilm
                selectedFilm?.title = binding.filmEditTitle.text.toString()
                selectedFilm?.director = binding.filmEditDirector.text.toString()
                selectedFilm?.imdbUrl = binding.filmEditImdb.text.toString()
                selectedFilm?.comments = binding.filmEditComments.text.toString()

                try {
                    selectedFilm?.year = Integer.parseInt(binding.filmEditYear.text.toString())
                } catch (ex: NumberFormatException) {
                    Log.e(this.javaClass.toString(), ex.stackTraceToString())
                }

                selectedFilm?.genre = binding.filmEditGenre.selectedItemPosition
                selectedFilm?.format = binding.filmEditFormat.selectedItemPosition

                // Return OK result and finish activity
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

        binding.filmCancel.setOnClickListener{ returnButton() }

        binding.filmPhoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                if(Build.VERSION.SDK_INT >= 30) {
                    ACTUAL_REQUEST_CODE = REQUEST_IMAGE_CAPTURE
                    startForResult.launch(takePictureIntent)
                }else
                    @Suppress("DEPRECATION")
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, R.string.error_open_camera, Toast.LENGTH_LONG).show()
                Log.e(this.javaClass.toString(), e.stackTraceToString())
            }
        }

        binding.filmImageSelect.setOnClickListener {

            val mediaFileIntent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            if (intent.resolveActivity(packageManager) != null) {
                if(Build.VERSION.SDK_INT >= 30) {
                    ACTUAL_REQUEST_CODE = REQUEST_MEDIA_FILE
                    startForResult.launch(mediaFileIntent)
                }
                else
                    @Suppress("DEPRECATION")
                    startActivityForResult(mediaFileIntent, REQUEST_MEDIA_FILE)
            }
        }

        binding.filmGeoEnable?.setOnClickListener {
            if(!selectedFilm!!.geoEnabled)
                addGeofenceForFilm()
            else
                removeGeofence()

            updateGeoEnabledButton()
        }

        updateEditHud()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE ){
            if(resultCode == RESULT_OK) {
                val bitmap = data?.extras?.get("data") as Bitmap
                binding.imageView.setImageBitmap(bitmap)
            }else{
                Toast.makeText(this, R.string.error_take_photo, Toast.LENGTH_LONG).show()
            }
        }else if(requestCode == REQUEST_MEDIA_FILE && resultCode == Activity.RESULT_OK){
            val selectedImageUri = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    private fun updateEditHud(){
        if(selectedFilm != null) {

            if (selectedFilm?.imgUrl != null) {
                binding.imageView.load(selectedFilm?.imgUrl)
            } else {
                binding.imageView.setImageResource(selectedFilm?.imageResId ?: R.drawable.back_to_future)
            }
            binding.filmEditTitle.setText(selectedFilm?.title ?: "")
            binding.filmEditDirector.setText(selectedFilm?.director ?: "")
            binding.filmEditYear.setText(selectedFilm?.year.toString())
            binding.filmEditImdb.setText(selectedFilm?.imdbUrl ?: "")
            binding.filmEditComments.setText(selectedFilm?.comments ?: "")
            binding.filmEditGenre.setSelection(selectedFilm!!.genre)
            binding.filmEditFormat.setSelection(selectedFilm!!.format)

            updateGeoEnabledButton()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId){
            android.R.id.home -> returnButton()
        }

        return false
    }

    private fun returnButton(){
        setResult(Activity.RESULT_CANCELED, null)
        finish()
    }

    private fun updateGeoEnabledButton(){
        if(selectedFilm!!.geoEnabled) {
            Toast.makeText(
                this,
                resources.getString(R.string.activated_geofence),
                Toast.LENGTH_SHORT
            ).show()
            binding.filmGeoEnable?.text = resources.getString(R.string.film_geoDisable)
        }
        else {
            binding.filmGeoEnable?.text = resources.getString(R.string.film_geoEnable)
        }
    }

    private fun addGeofenceForFilm() {
        geofenceList.add(Geofence.Builder()
            .setRequestId(selectedFilm?.title ?: "")
            .setCircularRegion(selectedFilm?.latitude ?: 0.0, selectedFilm?.longitude ?: 0.0, 50f)
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build())

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions()
            return
        }

        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
                        selectedFilm?.geoEnabled = true
                        updateGeoEnabledButton()
                    }
                    addOnFailureListener { e ->
                        Log.e("GeofenceError", "Error al añadir geocercado: ${e.message}", e)
                    }
                }
            }
        }
    }

    private fun removeGeofence() {
        geofencingClient.removeGeofences(geofencePendingIntent)
            .addOnSuccessListener {
                selectedFilm?.geoEnabled = false
                updateGeoEnabledButton()
                Toast.makeText(this, resources.getString(R.string.removed_geofence), Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, resources.getString(R.string.error_removing_geofence), Toast.LENGTH_SHORT).show()
            }
    }

    private fun requestLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE_FOREGROUND
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_FOREGROUND
            )
        }
    }

    private fun requestBackgroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AlertDialog.Builder(this)
                .setTitle("Permiso adicional requerido")
                .setMessage("Para que el geocercado funcione correctamente, la app necesita permiso de ubicación en segundo plano.")
                .setPositiveButton("Aceptar") { _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQUEST_CODE_BACKGROUND)
                }
                .setNegativeButton("Cancelar") { _, _ ->
                    Toast.makeText(this, "El geocercado no funcionará sin este permiso", Toast.LENGTH_LONG).show()
                }
                .show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_FOREGROUND -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("FilmEditActivity", "Permisos concedidos")
                    requestBackgroundPermission()
                } else {
                    Log.e("FilmEditActivity", "Permisos denegados")
                    Toast.makeText(
                        this,
                        "Es necesario otorgar permisos de ubicación",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            REQUEST_CODE_BACKGROUND -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de ubicación en segundo plano concedido", Toast.LENGTH_LONG).show()
                    addGeofenceForFilm()
                } else {
                    Toast.makeText(this, "El geocercado no funcionará sin este permiso", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}