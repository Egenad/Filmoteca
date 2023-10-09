package es.ua.eps.filmoteca.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.ua.eps.filmoteca.Film
import es.ua.eps.filmoteca.R

class RecycledAdapter(val filmList: List<Film>) :
    RecyclerView.Adapter<RecycledAdapter.ViewHolder?>() {

    private var listener: (filmPosition: Int) -> Unit = {}
    private var listenerLong: (filmPosition: Int) -> Boolean = {false}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.film_item, parent, false)

        val holder = ViewHolder(v)

        v.setOnClickListener {
            listener(holder.adapterPosition)
        }


        v.setOnLongClickListener {
            listenerLong(holder.adapterPosition)
        }

        return holder
    }
    override fun getItemCount(): Int {
        return filmList.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filmList[position])
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var name: TextView
        private var director: TextView
        private var icon: ImageView

        fun bind(it: Film) {
            name.text = it.title
            director.text = it.director
            icon.setImageResource(it.imageResId)
        }

        init {
            name = v.findViewById(R.id.itemName)
            director = v.findViewById(R.id.itemDirector)
            icon = v.findViewById(R.id.itemPoster)
        }
    }

    fun setOnItemClickListener(listener: (filmPosition: Int) -> Unit) {
        this.listener = listener
    }

    fun setOnLongItemClickListener(listener: (filmPosition: Int) -> Boolean) {
        this.listenerLong = listener
    }

}