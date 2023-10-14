package es.ua.eps.filmoteca.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityMainBinding
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_POSITION
import es.ua.eps.filmoteca.fragment.FilmDataFragment
import es.ua.eps.filmoteca.fragment.FilmListFragment

class MainActivity : AppCompatActivity(),
            FilmListFragment.OnItemSelectedListener {

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

        if(mainBinding.fragmentContainer != null && savedInstanceState == null)
            initDynamicFragment()
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

                val listFragment = supportFragmentManager.findFragmentById(R.id.list_fragment) as FilmListFragment
                listFragment.notifyItemInserted()
            }
        }

        return false
    }

    private fun initDynamicFragment(){
        val listFragment = FilmListFragment()
        listFragment.arguments = intent.extras
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, listFragment).commit()
    }

    override fun onItemSelected(position: Int) {

        var dataFragment = supportFragmentManager.findFragmentById(R.id.data_fragment) as? FilmDataFragment

        if (dataFragment != null) {
            dataFragment.updateInterfaceByPositionId(position)
        } else {

            dataFragment = FilmDataFragment()
            val args = Bundle()
            args.putInt(EXTRA_FILM_POSITION, position)
            dataFragment.arguments = args

            val t = supportFragmentManager.beginTransaction()
            t.replace(R.id.fragment_container, dataFragment)
            t.addToBackStack(null)
            t.commit()
        }
    }

}