package es.ua.eps.filmoteca

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.ua.eps.filmoteca.databinding.ActivityFilmEditBinding

class FilmEditActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFilmEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_edit)

        binding.filmSave.setOnClickListener{
            finish()
        }

        binding.filmCancel.setOnClickListener{
            finish()
        }
    }
}