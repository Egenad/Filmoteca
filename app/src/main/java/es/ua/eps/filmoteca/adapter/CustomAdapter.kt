package es.ua.eps.filmoteca.adapter

import android.content.Context
import android.widget.ArrayAdapter

class CustomAdapter(
    context: Context?, resource: Int,
    objects: List<Language>?
) : ArrayAdapter<Language>(context!!, resource, objects!!) {
}