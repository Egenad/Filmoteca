package es.ua.eps.filmoteca.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.adapter.CustomAdapter
import es.ua.eps.filmoteca.databinding.ActivityFilmListRecycledBinding
import es.ua.eps.filmoteca.databinding.ActivityMainBinding
import es.ua.eps.filmoteca.fragment.FilmListFragment

class MainActivity : AppCompatActivity() {

    companion object {
        private val ID_ADD_FILM = Menu.FIRST
        private val ID_ABOUT = Menu.FIRST + 1
        private val ID_GROUP = Menu.FIRST
    }

    private lateinit var mainBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.includeAppbar.toolbar)
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
            ID_ABOUT -> startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            ID_ADD_FILM ->{
                FilmDataSource.addDefaultFilm()

                val listFragment : FilmListFragment? = if(Build.VERSION.SDK_INT >= 28)
                    supportFragmentManager.findFragmentById(R.id.list_fragment) as FilmListFragment
                else
                    fragmentManager.findFragmentById(R.id.list_fragment) as FilmListFragment?

                listFragment?.notifyItemInserted()
            }
        }

        return false
    }

}