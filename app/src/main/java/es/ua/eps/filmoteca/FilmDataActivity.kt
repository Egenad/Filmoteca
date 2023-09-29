package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding

const val EXTRA_FILM_TITLE = "EXTRA_FILM_TITLE"

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
        setContentView(R.layout.activity_film_data)

        binding = ActivityFilmDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.filmRelated.setOnClickListener {
            startActivity(Intent(this@FilmDataActivity, FilmDataActivity::class.java)
                .putExtra(EXTRA_FILM_TITLE, resources.getString(R.string.film_related_name))
            )
        }

        binding.filmEdit.setOnClickListener {

            val editIntent = Intent(this@FilmDataActivity, FilmEditActivity::class.java)

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

        binding.filmData.text = intent.getStringExtra(EXTRA_FILM_TITLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == MOVIE_RESULT)
            if(resultCode == Activity.RESULT_OK){
                val toString = binding.filmData.text.toString() + " - " + resources.getString(R.string.film_edit_result)
                binding.filmData.text = toString
                Toast.makeText(this, R.string.film_edit_result_ok, Toast.LENGTH_SHORT).show()
            }else if(resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, R.string.film_edit_result_cancelled, Toast.LENGTH_SHORT).show()
    }
}