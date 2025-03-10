package es.ua.eps.filmoteca

class Film {
    var imageResId = 0
    var title: String? = null
    var director: String? = null
    var year = 0
    var genre = 0
    var format = 0
    var imdbUrl: String? = null
    var comments: String? = null
    var selected: Boolean = false
    var imgUrl: String? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    var geoEnabled: Boolean = false

    override fun toString(): String {
        return title?:"<Sin titulo>"
    }

    companion object {
        const val FORMAT_DVD = 0
        const val FORMAT_DIGITAL = 1
        const val FORMAT_BLURAY = 2

        const val GENRE_ACTION = 0
        const val GENRE_COMEDY = 1
        const val GENRE_DRAMA = 2
        const val GENRE_SCIFI = 3
        const val GENRE_HORROR = 4
        const val GENRE_FANTASY = 5
    }
}