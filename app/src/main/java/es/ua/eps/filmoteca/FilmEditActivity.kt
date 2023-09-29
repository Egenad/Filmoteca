package es.ua.eps.filmoteca

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding

class FilmEditActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFilmEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_edit)

        binding = ActivityFilmEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.filmSave.setOnClickListener{
            setResult(Activity.RESULT_OK)
            finish()
        }

        binding.filmCancel.setOnClickListener{
            setResult(Activity.RESULT_CANCELED, null)
            finish()
        }
    }
}