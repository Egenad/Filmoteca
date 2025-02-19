package es.ua.eps.filmoteca.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.ListFragment
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
import java.lang.Exception

class FilmListFragment : ListFragment() {

    private lateinit var bindingRecycled : ActivityFilmListRecycledBinding
    private lateinit var binding : ActivityFilmListBinding

    private var customCallback : CustomAMCallback = CustomAMCallback()

    private var callback: OnItemSelectedListener? = null
    private var callbackDelete: OnDeleteItemListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if(resources.getBoolean(R.bool.use_recycled_view))
            return createRecycledView(container)

        return createListView(container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        try {
            super.onViewCreated(view, savedInstanceState)
        }catch (ex : Exception){
            Log.w(this.javaClass.toString(), ex.message.toString()) // Not a ListView
        }

        if(resources.getBoolean(R.bool.use_recycled_view))
            createRecycledViewListeners()
        else
            createListViewListeners()
    }

    private fun createRecycledView(container: ViewGroup?) : View{

        bindingRecycled = ActivityFilmListRecycledBinding.inflate(layoutInflater, container, false)

        registerForContextMenu(bindingRecycled.list)

        bindingRecycled.list.layoutManager = LinearLayoutManager(activity as AppCompatActivity?)
        bindingRecycled.list.itemAnimator = DefaultItemAnimator()
        val recyclerAdapter = RecycledAdapter(FilmDataSource.films)
        bindingRecycled.list.adapter = recyclerAdapter

        return bindingRecycled.list
    }

    private fun createListView(container: ViewGroup?) : View{

        binding = ActivityFilmListBinding.inflate(layoutInflater, container, false)

        registerForContextMenu(binding.list)

        binding.list.adapter = CustomAdapter(activity, R.layout.film_item, FilmDataSource.films)

        listAdapter =  binding.list.adapter

        return binding.root
    }

    private fun createRecycledViewListeners(){

        val recycledList = bindingRecycled.list
        val recyclerAdapter = recycledList.adapter as RecycledAdapter

        recyclerAdapter.setOnItemClickListener {
                filmPosition ->
            run {
                if (customCallback.actionMode != null)
                    customCallback.actionItemClicked(filmPosition, requireContext()) // MULTIPLE SELECTION
                else
                    callback?.onItemSelected(filmPosition)
            }
        }

        recyclerAdapter.setOnLongItemClickListener {
            if (customCallback.actionMode != null) {
                false
            } else {
                customCallback.startSupportActionMode((activity as AppCompatActivity?)!!, recyclerAdapter)
                customCallback.actionItemClicked(it, requireContext())
                true
            }
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }

    interface OnDeleteItemListener {
        fun onDeleteItem()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = try {
            context as OnItemSelectedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnItemSelectedListener")
        }

        callbackDelete = try {
            context as OnDeleteItemListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnDeleteItemListener")
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        callback?.onItemSelected(position)
    }

    private fun createListViewListeners(){

        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL

        listView.setMultiChoiceModeListener(
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
                            callbackDelete?.onDeleteItem()
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

    fun notifyItemInserted(){
        if(resources.getBoolean(R.bool.use_recycled_view))
            bindingRecycled.list.adapter?.notifyItemInserted(FilmDataSource.films.size - 1)
        else
            (listAdapter as CustomAdapter).notifyDataSetChanged()
    }

    fun notifyItemModified(position: Int){
        if(resources.getBoolean(R.bool.use_recycled_view))
            bindingRecycled.list.adapter?.notifyItemChanged(position)
        else
            (listAdapter as CustomAdapter).notifyDataSetChanged()
    }

}