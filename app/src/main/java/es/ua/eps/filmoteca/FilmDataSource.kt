package es.ua.eps.filmoteca

import android.content.Context
import es.ua.eps.filmoteca.service.FilmData

object FilmDataSource {

    val films: MutableList<Film> = mutableListOf()

    private lateinit var appContext: Context

    fun init(context: Context) {

        appContext = context.applicationContext

        val resources = appContext.resources

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

        val resources = appContext.resources

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

    private fun filmExistsByTitle(title: String): Boolean {
        return films.any { it.title == title }
    }

    fun addFireBaseMovie(movieData: FilmData){
        // Check if it exists already
        if(filmExistsByTitle(movieData.title ?: "")){
            val film = getFilmByTitle(movieData.title ?: "")

            if(movieData.director != null) film.director = movieData.director
            if(movieData.comments != null) film.comments = movieData.comments
            if(movieData.imdbUrl != null) film.imdbUrl = movieData.imdbUrl
            if(movieData.imageUrl != null) film.imgUrl = movieData.imageUrl
            if(movieData.year != null) film.year = Integer.valueOf(movieData.year ?: "0")
            if(movieData.genre != null) film.genre = Integer.valueOf(movieData.genre ?: "0")
            if(movieData.format != null) film.format = Integer.valueOf(movieData.format ?: "0")

        }else{ // Add to data source
            try {
                val f = Film()
                f.title = movieData.title
                f.director = movieData.director
                f.imageResId = R.string.film_default_title
                f.imgUrl = movieData.imageUrl
                f.comments = movieData.comments
                f.format = Integer.valueOf(movieData.format ?: "0")
                f.genre = Integer.valueOf(movieData.genre ?: "0")
                f.imdbUrl = movieData.imdbUrl
                f.year = Integer.valueOf(movieData.year ?: "0")
                films.add(f)
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }
    }

    fun deleteFireBaseMovie(movieName: String){
        getFilmByTitle(movieName).let {
            films.remove(it)
        }
    }
}