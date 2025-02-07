package es.ua.eps.filmoteca

import es.ua.eps.filmoteca.java.ContextBuilder

object FilmDataSource {

    val films: MutableList<Film> = mutableListOf()

    init {

        val resources = ContextBuilder.getContext().resources

        // FILMS

        var f = Film()
        f.title = resources.getString(R.string.film_rf_name)
        f.director = resources.getString(R.string.film_rf_director)
        f.imageResId = R.drawable.back_to_future
        f.comments = ""
        f.format = Film.FORMAT_DIGITAL
        f.genre = Film.GENRE_SCIFI
        f.imdbUrl = "http://www.imdb.com/title/tt0088763"
        f.year = 1985
        films.add(f)

        f = Film()
        f.title = resources.getString(R.string.film_h1_name)
        f.director = resources.getString(R.string.film_h1_director)
        f.imageResId = R.drawable.harry_potter_philosopher_stone
        f.comments = ""
        f.format = Film.FORMAT_BLURAY
        f.genre = Film.GENRE_FANTASY
        f.imdbUrl = "https://www.imdb.com/title/tt0241527"
        f.year = 2001
        films.add(f)

        f = Film()
        f.title = resources.getString(R.string.film_h2_name)
        f.director = resources.getString(R.string.film_h1_director)
        f.imageResId = R.drawable.harry_potter_chamber_secrets
        f.comments = ""
        f.format = Film.FORMAT_BLURAY
        f.genre = Film.GENRE_FANTASY
        f.imdbUrl = "http://www.imdb.com/title/tt0295297"
        f.year = 2002
        films.add(f)

        f = Film()
        f.title = resources.getString(R.string.film_ij_name)
        f.director = resources.getString(R.string.film_ij_director)
        f.imageResId = R.drawable.indiana_lost_ark
        f.comments = ""
        f.format = Film.FORMAT_DVD
        f.genre = Film.GENRE_ACTION
        f.imdbUrl = "https://www.imdb.com/title/tt0082971"
        f.year = 1981
        films.add(f)

    }

    fun addDefaultFilm(){
        films.add(getDefaultFilm())
    }

    fun getDefaultFilm() : Film{

        val resources = ContextBuilder.getContext().resources

        val f = Film()
        f.title = resources.getString(R.string.film_default_title)
        f.director = resources.getString(R.string.film_default_director)
        f.imageResId = android.R.drawable.ic_menu_gallery
        f.comments = ""
        f.format = Film.FORMAT_DIGITAL
        f.genre = Film.GENRE_SCIFI
        f.imdbUrl = resources.getString(R.string.film_default_imdb)
        f.year = 2000
        return f
    }

    fun selectAllItems(selection : Boolean){
        for (film in films){
            film.selected = selection
        }
    }

    fun getSelectedFilmsCount() : Int {

        var count = 0

        for (film in films){
            if(film.selected)
                count++
        }

        return count
    }

    fun deleteSelectedFilms(){

        val newFilmList: MutableList<Film> = mutableListOf()

        for (film in films){
            if(film.selected)
                newFilmList.add(film)
        }

        films.removeAll(newFilmList)
    }

    fun getFilmByTitle(title: String) : Film{

        var result : Film = getDefaultFilm()

        if(films.isNotEmpty()) {
            var found = false;
            for (film in films) {
                if (film.title.equals(title)) {
                    result = film
                    found = true
                }
            }

            if(!found) result = films[0]
        }

        return result
    }
}