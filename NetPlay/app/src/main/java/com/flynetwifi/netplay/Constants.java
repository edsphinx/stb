package com.flynetwifi.netplay;


public interface Constants {

    static String server = "http://10.115.0.3:8080";
    //static String server = "http://190.92.91.218:8080";

    static String authorization = "/oauth/token";
    static String profiles = "/stb/perfiles/";
    static String account = "/stb/cuenta/";

    static String live = "/stb/live";
    static String live_favorites = Constants.live + "/favoritos/";
    static String programation = "/stb/live/programacion/";

    static String movies = "/stb/peliculas/";
    static String movies_details = "/stb/peliculas/detalles/";

    static String series = "/stb/series/";
    static String details = Constants.series + "detalles/";

    static String music = "/stb/musica/videos/";
    static String music_playlist = Constants.music + "playlist/user/";
    static String music_singers = Constants.music + "cantantes/";
    static String music_genders = Constants.music + "generos/";
    static String songs_playlist = Constants.music + "/playlist/";
    static String songs_genders = Constants.music + "/generos/";
    static String songs_singers = Constants.music + "/cantantes/";
    static String songs_songs = Constants.music + "/cancion/";

    static String music_song_query = Constants.music + "/search/";


    static String cuenta_confirmacion = "/stb/cuenta/confirmacion/";

    static String messages = "/stb/mensajes/";

}
