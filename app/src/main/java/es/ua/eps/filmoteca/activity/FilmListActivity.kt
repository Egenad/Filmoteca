package es.ua.eps.filmoteca.activity

import android.app.ListActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding

@Suppress("DEPRECATION")
class FilmListActivity : ListActivity() {

    private lateinit var binding : ActivityFilmListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.adapter = ArrayAdapter(
            this,
            R.layout.film_item,
            R.id.itemName,
            FilmDataSource.films)

        /*binding.filmA.setOnClickListener {
            startActivity(Intent(this@FilmListActivity, FilmDataActivity::class.java)
                .putExtra(EXTRA_FILM_TITLE, resources.getString(R.string.film_a_name))
            )
        }

        binding.filmB.setOnClickListener {
            startActivity(Intent(this@FilmListActivity, FilmDataActivity::class.java)
                .putExtra(EXTRA_FILM_TITLE, resources.getString(R.string.film_b_name))
            )
        }

        binding.about.setOnClickListener {
            startActivity(Intent(this@FilmListActivity, AboutActivity::class.java))
        }*/
    }
}