package es.ua.eps.filmoteca

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import es.ua.eps.filmoteca.adapter.RecycledAdapter
import es.ua.eps.filmoteca.java.ContextBuilder

class CustomAMCallback : ActionMode.Callback {

    var actionMode: ActionMode? = null

    private val resources = ContextBuilder.getContext().resources
    private var adapter : RecycledAdapter? = null

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater = mode?.menuInflater
        inflater?.inflate(R.menu.contextual_menu, menu)

        setTitle(1)

        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.multiple_delete -> {
                // Delete selected films
                FilmDataSource.deleteSelectedFilms()
                adapter?.notifyDataSetChanged()
                setTitle(0)
                true
            }
            else -> false
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        FilmDataSource.selectAllItems(false)
        adapter?.notifyDataSetChanged()
        actionMode = null
    }

    fun startSupportActionMode(context : AppCompatActivity, newAdapter : RecycledAdapter){
        actionMode = context.startSupportActionMode(this)
        adapter = newAdapter
    }

    fun actionItemClicked(position : Int){
        if(position < FilmDataSource.films.size) {
            val film: Film = FilmDataSource.films[position]
            film.selected = !film.selected
            setTitle(FilmDataSource.getSelectedFilmsCount())
            adapter?.notifyItemChanged(position)
        }
    }

    private fun setTitle(count : Int){
        val title : String = count.toString() + " " + resources.getString(R.string.films_selected)
        actionMode?.title = title
    }

}