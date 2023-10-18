package es.ua.eps.filmoteca.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import es.ua.eps.filmoteca.FilmDataSource
import es.ua.eps.filmoteca.R
import es.ua.eps.filmoteca.databinding.ActivityMainBinding
import es.ua.eps.filmoteca.fragment.EXTRA_FILM_POSITION
import es.ua.eps.filmoteca.fragment.FilmDataFragment
import es.ua.eps.filmoteca.fragment.FilmListFragment

class MainActivity : AppCompatActivity(),
            FilmListFragment.OnItemSelectedListener,
            FilmListFragment.OnDeleteItemListener,
            FilmDataFragment.OnDataEditListener,
            FilmDataFragment.OnReturnListener{

    companion object {
        const val ID_ADD_FILM = Menu.FIRST
        const val ID_ABOUT = Menu.FIRST + 1
        const val ID_GROUP = Menu.FIRST
    }

    private lateinit var mainBinding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setSupportActionBar(mainBinding.includeAppbar.toolbar)

        if(mainBinding.fragmentContainer != null)
            if(savedInstanceState == null)
                initDynamicFragment()
            else
                toListFragment()
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

                val listFragmentStatic = supportFragmentManager.findFragmentById(R.id.list_fragment) as? FilmListFragment
                val listFragmentDynamic = supportFragmentManager.findFragmentById(R.id.fragment_container) as? FilmListFragment

                listFragmentStatic?.notifyItemInserted()
                listFragmentDynamic?.notifyItemInserted()
            }
            android.R.id.home -> toListFragment()
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

        if (findViewById<FrameLayout>(R.id.fragment_container) == null) {

            supportActionBar?.setHomeButtonEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            dataFragment?.updateInterfaceByPositionId(position)

        } else {

            supportActionBar?.setHomeButtonEnabled(true)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    override fun onReturn() {
        toListFragment()
    }

    override fun onDeleteItem() {
        val dataFragment = supportFragmentManager.findFragmentById(R.id.data_fragment) as? FilmDataFragment
        dataFragment?.updateInterface(FilmDataSource.getFilmByTitle(dataFragment.getActualFilmTitle()))
    }

    override fun onDataEdit(position: Int) {
        val listFragment = supportFragmentManager.findFragmentById(R.id.list_fragment) as? FilmListFragment
        listFragment?.notifyItemModified(position)
    }

    private fun toListFragment(){

        if(findViewById<FrameLayout>(R.id.fragment_container) != null) {

            supportActionBar?.setHomeButtonEnabled(false)
            supportActionBar?.setDisplayHomeAsUpEnabled(false)

            supportFragmentManager.popBackStack()

            val listFragment = FilmListFragment()
            val t = supportFragmentManager.beginTransaction()
            t.replace(R.id.fragment_container, listFragment)
            t.commit()
        }
    }

}