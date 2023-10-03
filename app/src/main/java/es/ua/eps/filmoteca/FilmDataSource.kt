package es.ua.eps.filmoteca

object FilmDataSource {

    val films: MutableList<Film> = mutableListOf<Film>()

    init {
        var f = Film()
        f.title = "Regreso al futuro"
        f.director = "Robert Zemeckis"
        f.imageResId = R.drawable.back_to_future
        f.comments = ""
        f.format = Film.FORMAT_DIGITAL
        f.genre = Film.GENRE_SCIFI
        f.imdbUrl = "http://www.imdb.com/title/tt0088763"
        f.year = 1985
        films.add(f)

        f = Film()
        f.title = "Harry Potter y la piedra filosofal"
        f.director = "Chris Columbus"
        f.imageResId = R.drawable.harry_potter_philosopher_stone
        f.comments = ""
        f.format = Film.FORMAT_DIGITAL
        f.genre = Film.GENRE_FANTASY
        f.imdbUrl = "https://www.imdb.com/title/tt0241527"
        f.year = 2001
        films.add(f)

        f = Film()
        f.title = "Harry Potter y la cámara secreta"
        f.director = "Chris Columbus"
        f.imageResId = R.drawable.harry_potter_chamber_secrets
        f.comments = ""
        f.format = Film.FORMAT_DIGITAL
        f.genre = Film.GENRE_FANTASY
        f.imdbUrl = "http://www.imdb.com/title/tt0295297"
        f.year = 2002
        films.add(f)

        f = Film()
        f.title = "Indiana Jones: En busca del Arca Perdida"
        f.director = "Steven Spielberg"
        f.imageResId = R.drawable.indiana_lost_ark
        f.comments = ""
        f.format = Film.FORMAT_DIGITAL
        f.genre = Film.GENRE_ACTION
        f.imdbUrl = "https://www.imdb.com/title/tt0082971"
        f.year = 1981
        films.add(f)

    }
}