package es.ua.eps.filmoteca.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.adapter.CustomAdapter
import es.ua.eps.filmoteca.adapter.RecycledAdapter
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListRecycledBinding

class FilmListActivity : Activity() {

    private lateinit var bindingRecycled : ActivityFilmListRecycledBinding
    private lateinit var binding : ActivityFilmListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(resources.getBoolean(R.bool.use_recycled_view)) {
            createRecycledView()
        }else {
            createListView()
        }
    }

    private fun createRecycledView(){
        bindingRecycled = ActivityFilmListRecycledBinding.inflate(layoutInflater)
        setContentView(bindingRecycled.root)

        bindingRecycled.list.layoutManager = LinearLayoutManager(this)
        bindingRecycled.list.itemAnimator = DefaultItemAnimator()
        val recyclerAdapter = RecycledAdapter(FilmDataSource.films)

        recyclerAdapter.setOnItemClickListener {
            filmPosition ->  startActivity(Intent(this@FilmListActivity, FilmDataActivity::class.java)
            .putExtra(EXTRA_FILM_POSITION, filmPosition))
        }

        bindingRecycled.list.adapter = recyclerAdapter
    }

    private fun createListView(){
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