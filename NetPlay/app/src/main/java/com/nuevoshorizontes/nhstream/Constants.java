package com.nuevoshorizontes.nhstream;


public interface Constants {

    static String server = "http://10.115.0.3:8080";
    static String stream_server = "http://10.115.0.3:1935";
    static String server_epg = "http://epg.panoramalife.com";
    //static String server = "http://190.92.91.218:8080";


    static String version = "1";
    static String version_url = server + "/stb/version";
    static String apk = stream_server + "/apps/app.apk";

    static String authorization = "/oauth/token";
    static String profiles = "/stb/perfiles/";
    static String account = "/stb/cuenta/";

    static String live = "/stb/live";
    static String live_favorites = Constants.live + "/favoritos/";
    static String programation = "/stb/live/programacion/";

    //EPG Custom Programation
    static String programation_epg = "/api/channel/";

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
