package es.ua.eps.filmoteca.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import es.ua.eps.filmoteca.Film
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding

const val EXTRA_FILM_POSITION = "EXTRA_FILM_POSITION"
class FilmDataActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFilmDataBinding
    private val MOVIE_RESULT = 1

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            onActivityResult(MOVIE_RESULT, result.resultCode, result.data)
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilmDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.includeAppbar.toolbar)

        binding.filmEdit.setOnClickListener {

            val editIntent = Intent(this@FilmDataActivity, FilmEditActivity::class.java)
                .putExtra(EXTRA_FILM_POSITION, intent.getIntExtra(EXTRA_FILM_POSITION, 0))

            if(Build.VERSION.SDK_INT >= 30)
                startForResult.launch(editIntent)
            else
                @Suppress("DEPRECATION")
                startActivityForResult(editIntent, MOVIE_RESULT)
        }

        binding.filmReturn.setOnClickListener {
            startActivity(Intent(this@FilmDataActivity, FilmListActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }

        updateInterface(FilmDataSource.films[intent.getIntExtra(EXTRA_FILM_POSITION, 0)])

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == MOVIE_RESULT)
            if(resultCode == Activity.RESULT_OK){
                // Update interface with new values
                updateInterface(FilmDataSource.films[intent.getIntExtra(EXTRA_FILM_POSITION, 0)])
                Toast.makeText(this, R.string.film_edit_result_ok, Toast.LENGTH_SHORT).show()
            }else if(resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, R.string.film_edit_result_cancelled, Toast.LENGTH_SHORT).show()
    }

    private fun updateInterface(selectedFilm: Film){

        val nullValue = resources.getString(R.string.nullValue)

        binding.filmData.text = selectedFilm.title?: nullValue
        binding.filmDirectorValue.text = selectedFilm.director?: nullValue
        binding.filmYearValue.text = selectedFilm.year.toString()
        binding.imageView.setImageResource(selectedFilm.imageResId)

        var genre = nullValue
        var format = nullValue

        if(resources.getStringArray(R.array.genre_options).size > selectedFilm.genre)
            genre = resources.getStringArray(R.array.genre_options)[selectedFilm.genre] ?: nullValue

        if(resources.getStringArray(R.array.format_options).size > selectedFilm.format)
            format = resources.getStringArray(R.array.format_options)[selectedFilm.format]?: nullValue

        val genreType = "$format, $genre"

        binding.filmTypeGenere.text = genreType

        binding.filmImdb.setOnClickListener{
            val imdbPageIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse(selectedFilm.imdbUrl))
            if (imdbPageIntent.resolveActivity(packageManager) != null) {
                startActivity(imdbPageIntent)
            }
        }
    }
}