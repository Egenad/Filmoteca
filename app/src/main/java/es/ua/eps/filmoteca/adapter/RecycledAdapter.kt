package es.ua.eps.filmoteca.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
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
        private val view = v

        fun bind(it: Film) {
            name.text = it.title
            director.text = it.director

            if(it.imgUrl != null){
                try{
                    icon.load(it.imgUrl)
                }catch (ex: Exception){
                    ex.printStackTrace()
                }
            }else{
                icon.setImageResource(it.imageResId)
            }

            if(it.selected)
                view.setBackgroundColor(Color.GRAY)
            else
                view.setBackgroundColor(Color.WHITE)
        }

        init {
            director = view.findViewById(R.id.itemDirector)
            name = view.findViewById(R.id.itemName)
            icon = view.findViewById(R.id.itemPoster)
        }
    }

    fun setOnItemClickListener(listener: (filmPosition: Int) -> Unit) {
        this.listener = listener
    }

    fun setOnLongItemClickListener(listener: (filmPosition: Int) -> Boolean) {
        this.listenerLong = listener
    }

}