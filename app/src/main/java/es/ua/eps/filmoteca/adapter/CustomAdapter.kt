package es.ua.eps.filmoteca.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import coil.load
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

        val name: TextView      = view.findViewById(R.id.itemName)
        val director: TextView  = view.findViewById(R.id.itemDirector)
        val icon: ImageView     = view.findViewById(R.id.itemPoster)

        getItem(position)?.let {
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

        return view
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
    }

}