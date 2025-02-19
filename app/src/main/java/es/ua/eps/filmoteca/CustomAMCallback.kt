package es.ua.eps.filmoteca

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import es.ua.eps.filmoteca.adapter.RecycledAdapter

class CustomAMCallback : ActionMode.Callback {

    var actionMode: ActionMode? = null

    private var adapter : RecycledAdapter? = null

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        val inflater = mode?.menuInflater
        inflater?.inflate(R.menu.contextual_menu, menu)

        actionMode?.title = "1 item selected"

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
                actionMode?.title = "0 items selected"
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

    fun actionItemClicked(position : Int, context: Context){
        if(position < FilmDataSource.films.size) {
            val film: Film = FilmDataSource.films[position]
            film.selected = !film.selected
            setTitle(FilmDataSource.getSelectedFilmsCount(), context)
            adapter?.notifyItemChanged(position)
        }
    }

    private fun setTitle(count : Int, context: Context){
        val title : String = count.toString() + " " + context.getString(R.string.films_selected)
        actionMode?.title = title
    }

}