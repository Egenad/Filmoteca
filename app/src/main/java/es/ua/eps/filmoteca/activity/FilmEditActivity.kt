package es.ua.eps.filmoteca.activity

import android.R.attr
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import es.ua.eps.filmoteca.Film
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding


class FilmEditActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFilmEditBinding

    val REQUEST_IMAGE_CAPTURE   = 1
    val REQUEST_MEDIA_FILE      = 2
    var ACTUAL_REQUEST_CODE     = 0

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {result: ActivityResult ->
            onActivityResult(ACTUAL_REQUEST_CODE, result.resultCode, result.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedFilm = FilmDataSource.films[intent.getIntExtra(EXTRA_FILM_POSITION, 0)]

        binding = ActivityFilmEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.filmSave.setOnClickListener{

            // Retrieve fields data and save them to the selectedFilm
            selectedFilm.title = binding.filmEditTitle?.text.toString()
            selectedFilm.director = binding.filmEditDirector?.text.toString()
            selectedFilm.imdbUrl = binding.filmEditImdb?.text.toString()
            selectedFilm.comments = binding.filmEditComments?.text.toString()

            try {
                selectedFilm.year = Integer.parseInt(binding.filmEditYear?.text.toString())
            }catch(ex : NumberFormatException){
                Log.e(this.javaClass.toString(), ex.stackTraceToString())
            }

            selectedFilm.genre = binding.filmEditGenre?.selectedItemPosition?: 0
            selectedFilm.format = binding.filmEditFormat?.selectedItemPosition?: 0

            // Return OK result and finish activity
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.filmCancel.setOnClickListener{
            setResult(Activity.RESULT_CANCELED, null)
            finish()
        }

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

        updateEditHud(selectedFilm)
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
            val bitmap = data?.extras?.get("data") as Bitmap
            binding.imageView.setImageBitmap(bitmap)
        }
    }

    private fun updateEditHud(selectedFilm: Film){
        binding.imageView.setImageResource(selectedFilm.imageResId)
        binding.filmEditTitle?.setText(selectedFilm.title?: "")
        binding.filmEditDirector?.setText(selectedFilm.director?: "")
        binding.filmEditYear?.setText(selectedFilm.year.toString())
        binding.filmEditImdb?.setText(selectedFilm.imdbUrl?: "")
        binding.filmEditComments?.setText(selectedFilm.comments?: "")
        binding.filmEditGenre?.setSelection(selectedFilm.genre)
        binding.filmEditFormat?.setSelection(selectedFilm.format)
    }
}