package es.ua.eps.filmoteca.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import es.ua.eps.filmoteca.Film
import es.ua.eps.filmoteca.R

class CustomAdapter(
    context: Context?, resource: Int,
    objects: MutableList<Film>?
) : ArrayAdapter<Film>(context!!, resource, objects!!) {
    override fun getView(position: Int, convertView: View?,
                         parent: ViewGroup
    ): View {
        val view: View = convertView?: LayoutInflater.from(this.context)
            .inflate(R.layout.film_item, parent, false)

        val name = view.findViewById(R.id.itemName) as TextView
        val director = view.findViewById(R.id.itemDirector) as TextView
        val icon = view.findViewById(R.id.itemPoster) as ImageView

        getItem(position)?.let {
            name.text = it.title
            director.text = it.director
            icon.setImageResource(it.imageResId)
        }

        return view
    }

}