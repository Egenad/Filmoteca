package es.ua.eps.filmoteca.activity

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.adapter.CustomAdapter
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding

@Suppress("DEPRECATION")
class FilmListActivity : ListActivity() {

    private lateinit var binding : ActivityFilmListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFilmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.list.adapter = CustomAdapter(this, R.layout.film_item, FilmDataSource.films)

        binding.list.setOnItemClickListener { parent: AdapterView<*>, view: View,
                                              position: Int, id: Long ->
            val intent = Intent(this@FilmListActivity, FilmDataActivity::class.java)
            intent.putExtra(EXTRA_FILM_POSITION, position)
            startActivity(intent)
        }
    }
}