package es.ua.eps.filmoteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.ua.eps.filmoteca.databinding.ActivityFilmDataBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding

class FilmDataActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFilmDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_data)

        binding = ActivityFilmDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.filmRelated.setOnClickListener {
            startActivity(Intent(this@FilmDataActivity, FilmDataActivity::class.java))
        }

        binding.filmEdit.setOnClickListener {
            startActivity(Intent(this@FilmDataActivity, FilmEditActivity::class.java))
        }

        binding.filmReturn.setOnClickListener {
            startActivity(Intent(this@FilmDataActivity, FilmListActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }
}