package es.ua.eps.filmoteca.service

data class FilmData (
    var title: String? = null,
    var director: String? = null,
    var year: String? = null,
    var genre: String? = null,
    var format: String? = null,
    var imageUrl: String? = null,
    var imdbUrl: String? = null,
    var comments: String? = null
)