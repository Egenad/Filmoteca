package es.ua.eps.filmoteca.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import es.ua.eps.filmoteca.Film
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.activity.FilmEditActivity
import es.ua.eps.filmoteca.activity.MainActivity
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding

const val EXTRA_FILM_POSITION = "EXTRA_FILM_POSITION"

class FilmDataFragment : Fragment() {

    private lateinit var binding : ActivityFilmDataBinding
    private val MOVIE_RESULT = 1

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            onActivityResult(MOVIE_RESULT, result.resultCode, result.data)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ActivityFilmDataBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.filmEdit.setOnClickListener {

            val editIntent = Intent(activity, FilmEditActivity::class.java)
                .putExtra(EXTRA_FILM_POSITION, activity?.intent?.getIntExtra(EXTRA_FILM_POSITION, 0))

            if(Build.VERSION.SDK_INT >= 30)
                startForResult.launch(editIntent)
            else
                @Suppress("DEPRECATION")
                startActivityForResult(editIntent, MOVIE_RESULT)
        }

        binding.filmReturn?.setOnClickListener { returnButton() }

        updateInterfaceByPositionId(arguments?.getInt(EXTRA_FILM_POSITION, 0)!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MOVIE_RESULT)
            if (resultCode == Activity.RESULT_OK) {
                // Update interface with new values
                updateInterface(
                    FilmDataSource.films[activity?.intent?.getIntExtra(
                        EXTRA_FILM_POSITION,
                        0
                    )!!]
                )
                Toast.makeText(activity, R.string.film_edit_result_ok, Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(activity, R.string.film_edit_result_cancelled, Toast.LENGTH_SHORT).show()
    }

    fun updateInterfaceByPositionId(position: Int){
        if(FilmDataSource.films.size > position)
            updateInterface(FilmDataSource.films[position])
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
            if (imdbPageIntent.resolveActivity(activity?.packageManager!!) != null) {
                startActivity(imdbPageIntent)
            }
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
        startActivity(Intent(activity, MainActivity::class.java)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }

}