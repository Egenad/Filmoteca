package es.ua.eps.filmoteca

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding

class FilmListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFilmListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film_list)

        binding = ActivityFilmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.filmA.setOnClickListener {
            startActivity(Intent(this@FilmListActivity, FilmDataActivity::class.java))
        }

        binding.filmB.setOnClickListener {
            startActivity(Intent(this@FilmListActivity, FilmDataActivity::class.java))
        }

        binding.about.setOnClickListener {
            startActivity(Intent(this@FilmListActivity, AboutActivity::class.java))
        }
    }
}