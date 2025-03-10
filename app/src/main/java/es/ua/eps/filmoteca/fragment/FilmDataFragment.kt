package es.ua.eps.filmoteca.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import es.ua.eps.filmoteca.Film
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.activity.FilmEditActivity
import es.ua.eps.filmoteca.activity.LoginActivity
import es.ua.eps.filmoteca.activity.MapsActivity
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding

const val EXTRA_FILM_POSITION   = "EXTRA_FILM_POSITION"
const val EXTRA_FILM_LATITUDE   = "EXTRA_FILM_LATITUDE"
const val EXTRA_FILM_LONGITUDE  = "EXTRA_FILM_LONGITUDE"
const val EXTRA_FILM_TITLE      = "EXTRA_FILM_TITLE"
const val EXTRA_FILM_DIRECTOR   = "EXTRA_FILM_DIRECTOR"
const val EXTRA_FILM_YEAR       = "EXTRA_FILM_YEAR"

class FilmDataFragment : Fragment() {

    private lateinit var binding : ActivityFilmDataBinding
    private val MOVIE_RESULT = 1

    private var callback: OnReturnListener? = null
    private var callbackDataEdit: OnDataEditListener? = null

    private var selectedFilmPosition : Int? = null

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            onActivityResult(MOVIE_RESULT, result.resultCode, result.data)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ActivityFilmDataBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(selectedFilmPosition != null)
            outState.putInt(EXTRA_FILM_POSITION, selectedFilmPosition!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.filmEdit.setOnClickListener {

            val editIntent = Intent(activity, FilmEditActivity::class.java)
                .putExtra(
                    EXTRA_FILM_POSITION,
                    (arguments?.getInt(EXTRA_FILM_POSITION, 0) ?: selectedFilmPosition ?: 0)
                )

            if (Build.VERSION.SDK_INT >= 30)
                startForResult.launch(editIntent)
            else
                @Suppress("DEPRECATION")
                startActivityForResult(editIntent, MOVIE_RESULT)
        }

        binding.filmReturn?.setOnClickListener { returnButton() }

        selectedFilmPosition =
            savedInstanceState?.getInt(EXTRA_FILM_POSITION) ?: (arguments?.getInt(
                EXTRA_FILM_POSITION,
                0
            ) ?: 0)

        updateInterfaceByPositionId(selectedFilmPosition)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MOVIE_RESULT)
            if (resultCode == Activity.RESULT_OK) {
                // Update interface with new values
                if(arguments?.getInt(EXTRA_FILM_POSITION, 0) != null || selectedFilmPosition != null) {

                    val position : Int = arguments?.getInt(EXTRA_FILM_POSITION, 0)
                        ?: selectedFilmPosition ?: 0

                    updateInterface(FilmDataSource.films[position])
                    callbackDataEdit?.onDataEdit(position)
                }
                Toast.makeText(activity, R.string.film_edit_result_ok, Toast.LENGTH_SHORT).show()
            } else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(activity, R.string.film_edit_result_cancelled, Toast.LENGTH_SHORT).show()
    }

    fun updateInterfaceByPositionId(position: Int?){

        if(FilmDataSource.films.size > (position ?: 0)) {

            val selectedFilm = FilmDataSource.films[position ?: 0]

            updateInterface(selectedFilm)
            selectedFilmPosition = position

            binding.showMap?.setOnClickListener{
                val intent = Intent(activity, MapsActivity::class.java)
                    .putExtra(EXTRA_FILM_LATITUDE, selectedFilm.latitude)
                    .putExtra(EXTRA_FILM_LONGITUDE, selectedFilm.longitude)
                    .putExtra(EXTRA_FILM_TITLE, selectedFilm.title)
                    .putExtra(EXTRA_FILM_DIRECTOR, selectedFilm.director)
                    .putExtra(EXTRA_FILM_YEAR, selectedFilm.year)

                startActivity(intent)
            }
        }
    }

    fun updateInterface(selectedFilm: Film){

        val nullValue = resources.getString(R.string.nullValue)

        binding.filmData.text = selectedFilm.title?: nullValue
        binding.filmDirectorValue.text = selectedFilm.director?: nullValue
        binding.filmYearValue.text = selectedFilm.year.toString()
        binding.latitude?.text = getString(R.string.latitude, selectedFilm.latitude)
        binding.longitude?.text = getString(R.string.longitude, selectedFilm.longitude)

        var geoEnabledString = resources.getString(R.string.enabled)

        if(!selectedFilm.geoEnabled){
            geoEnabledString = resources.getString(R.string.disabled)
        }

        binding.geoEnabled?.text = getString(R.string.geoEnabled, geoEnabledString)

        if(selectedFilm.imgUrl != null){
            binding.imageView.load(selectedFilm.imgUrl)
        }else{
            binding.imageView.setImageResource(selectedFilm.imageResId)
        }

        var genre = nullValue
        var format = nullValue

        if(resources.getStringArray(R.array.genre_options).size > selectedFilm.genre)
            genre = resources.getStringArray(R.array.genre_options)[selectedFilm.genre] ?: nullValue

        if(resources.getStringArray(R.array.format_options).size > selectedFilm.format)
            format = resources.getStringArray(R.array.format_options)[selectedFilm.format]?: nullValue

        val genreType = "$format, $genre"

        binding.filmTypeGenre.text = genreType

        binding.filmImdb.setOnClickListener{
            val imdbPageIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse(selectedFilm.imdbUrl))
            if (imdbPageIntent.resolveActivity(activity?.packageManager!!) != null) {
                startActivity(imdbPageIntent)
            }
        }

        val enabled = FilmDataSource.films.isNotEmpty()
        binding.filmEdit.isEnabled = enabled
        binding.filmImdb.isEnabled = enabled
    }

    private fun returnButton(){
        callback?.onReturn()
    }

    interface OnReturnListener {
        fun onReturn()
    }

    interface OnDataEditListener {
        fun onDataEdit(position: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as OnReturnListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnReturnListener")
        }

        callbackDataEdit = try {
            context as OnDataEditListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnDataEditListener")
        }
    }

    fun getActualFilmTitle() : String{
        return binding.filmData.text.toString()
    }

}