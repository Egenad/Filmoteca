package es.ua.eps.filmoteca.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.adapter.CustomAdapter
import es.ua.eps.filmoteca.adapter.RecycledAdapter
import es.ua.eps.filmoteca.databinding.ActivityFilmListBinding
import es.ua.eps.filmoteca.databinding.ActivityFilmListRecycledBinding

class FilmListActivity : AppCompatActivity() {

    companion object {
        private val ID_ADD_FILM = Menu.FIRST
        private val ID_ABOUT = Menu.FIRST + 1
        private val ID_GROUP = Menu.FIRST
    }

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

        setSupportActionBar(bindingRecycled.includeAppbar.toolbar)

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

        setSupportActionBar(binding.includeAppbar.toolbar)

        binding.list.adapter = CustomAdapter(this, R.layout.film_item, FilmDataSource.films)

        binding.list.setOnItemClickListener { parent: AdapterView<*>, view: View,
                                              position: Int, id: Long ->
            val intent = Intent(this@FilmListActivity, FilmDataActivity::class.java)
            intent.putExtra(EXTRA_FILM_POSITION, position)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menu?.add(ID_GROUP, ID_ADD_FILM, Menu.NONE, resources.getString(R.string.menu_add_film))
        menu?.add(ID_GROUP, ID_ABOUT, Menu.NONE, resources.getString(R.string.menu_about))

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)

        when(item.itemId){
            ID_ABOUT -> startActivity(Intent(this@FilmListActivity, AboutActivity::class.java))
            ID_ADD_FILM ->{
                FilmDataSource.addDefaultFilm()

                if(resources.getBoolean(R.bool.use_recycled_view)) {
                    bindingRecycled.list.adapter?.notifyItemInserted(FilmDataSource.films.size - 1)
                }else{
                    val customAdapter = binding.list.adapter as CustomAdapter
                    customAdapter.notifyDataSetChanged()
                }
            }
        }

        return false
    }
}