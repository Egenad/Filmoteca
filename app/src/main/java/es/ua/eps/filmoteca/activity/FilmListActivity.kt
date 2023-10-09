package es.ua.eps.filmoteca.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import es.ua.eps.filmoteca.CustomAMCallback
import es.ua.eps.filmoteca.Film
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

    private var customCallback : CustomAMCallback = CustomAMCallback()

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
        registerForContextMenu(bindingRecycled.list)

        bindingRecycled.list.layoutManager = LinearLayoutManager(this)
        bindingRecycled.list.itemAnimator = DefaultItemAnimator()
        val recyclerAdapter = RecycledAdapter(FilmDataSource.films)
        bindingRecycled.list.adapter = recyclerAdapter

        createRecycledViewListeners()
    }

    private fun createListView(){
        binding = ActivityFilmListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.includeAppbar.toolbar)
        registerForContextMenu(binding.list)

        binding.list.adapter = CustomAdapter(this, R.layout.film_item, FilmDataSource.films)

        createListViewListeners()

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

    private fun createRecycledViewListeners(){

        val recycledList = bindingRecycled.list
        val recyclerAdapter = recycledList.adapter as RecycledAdapter

        recyclerAdapter.setOnItemClickListener {
            filmPosition ->
            run {
                if (customCallback.actionMode != null) {
                    // MULTIPLE SELECTION
                    customCallback.actionItemClicked(filmPosition)

                } else {
                    startActivity(
                        Intent(this@FilmListActivity, FilmDataActivity::class.java)
                            .putExtra(EXTRA_FILM_POSITION, filmPosition)
                    )
                }
            }
        }

        recyclerAdapter.setOnLongItemClickListener {
            if (customCallback.actionMode != null) {
                false
            } else {
                customCallback.startSupportActionMode(this, recyclerAdapter)
                customCallback.actionItemClicked(it)
                true
            }
        }
    }

    private fun createListViewListeners(){

        binding.list.setOnItemClickListener { parent: AdapterView<*>, view: View,
                                              position: Int, id: Long ->
            val intent = Intent(this@FilmListActivity, FilmDataActivity::class.java)
            intent.putExtra(EXTRA_FILM_POSITION, position)
            startActivity(intent)
        }

        binding.list.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL

        binding.list.setMultiChoiceModeListener(
            object : AbsListView.MultiChoiceModeListener {
                override fun onCreateActionMode(p0: android.view.ActionMode?, p1: Menu?): Boolean {
                    val inflater = p0?.menuInflater
                    inflater?.inflate(R.menu.contextual_menu, p1)
                    setTitle(p0, 1)
                    return true
                }

                override fun onPrepareActionMode(p0: android.view.ActionMode?, p1: Menu?): Boolean {
                    return false
                }

                override fun onActionItemClicked(
                    p0: android.view.ActionMode?,
                    p1: MenuItem?
                ): Boolean {
                    return when (p1?.itemId) {
                        R.id.multiple_delete -> {
                            // Delete selected films
                            FilmDataSource.deleteSelectedFilms()
                            val customAdapter = binding.list.adapter as CustomAdapter
                            customAdapter.notifyDataSetChanged()
                            setTitle(p0, 0)
                            true
                        }
                        else -> false
                    }
                }

                override fun onDestroyActionMode(p0: android.view.ActionMode?) {
                    FilmDataSource.selectAllItems(false)
                }

                override fun onItemCheckedStateChanged(
                    p0: android.view.ActionMode?,
                    p1: Int,
                    p2: Long,
                    p3: Boolean
                ) {
                    (binding.list.getItemAtPosition(p1) as Film).selected = p3
                    val customAdapter = binding.list.adapter as CustomAdapter

                    setTitle(p0, FilmDataSource.getSelectedFilmsCount())

                    customAdapter.notifyDataSetChanged()
                }

                private fun setTitle(p0: android.view.ActionMode?, count : Int){
                    val title : String = count.toString() + " " + resources.getString(R.string.films_selected)
                    p0?.title = title
                }
            }
        )
    }
}