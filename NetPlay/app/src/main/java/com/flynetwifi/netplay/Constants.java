package com.flynetwifi.netplay;


public interface Constants {

    static String server = "http://10.115.0.3:8080";
    //static String server = "http://190.92.91.218:8080";

    static String bearer = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjRhYzdjYjc1MzU5NjAwNTk3ZTg5NzQzZjFlNzFkZjc3NmEzZjNmZTM4NGMxODllNWE5NWFlMWZiZDg5ZTY5MjYyNzRmYzAxYTU1MzEwOTVkIn0.eyJhdWQiOiI3IiwianRpIjoiNGFjN2NiNzUzNTk2MDA1OTdlODk3NDNmMWU3MWRmNzc2YTNmM2ZlMzg0YzE4OWU1YTk1YWUxZmJkODllNjkyNjI3NGZjMDFhNTUzMTA5NWQiLCJpYXQiOjE0ODg1ODA2MzQsIm5iZiI6MTQ4ODU4MDYzNCwiZXhwIjoxNTIwMTE2NjM0LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.b5JNXeJD1UgV42ygq6WyjsAwKckbpsQDq1U2o5ckeT4QguRvo0yXNJ5DGwXPUvxyhGMHrIs1zxVm2k8tvOzYKhL8l4ahHAMS_c4G1rAemPdOA-z_tlZc37LwGKRb6mJj0_XI0K-GCNWibeLmjmxKXvRIzhgOj0Im6mde4HUGTkMeGJb50IQIQlzZnRDGmOlFghQL62OqDCFyR6BxsU1E1xUyuxnh0rxK5jiOw56JACJ3ylcB0vO0Hs0g56gq0as_Ic_VetdaqWynGJMX5TZz-stHxl_r4b4McWcAg4Uj2klsjSrF86qb4GjoXRDaOmBGCpDHhArKKvu9QzNkr-s95SuG8JGwwOrFPkSuhM6JHZ6RRt-iEPqhH-OiTWTsB5Pn06ce7FURVJ_scJVgjwVGxXJ4k69qSBuLXQEj-AFHRKWyR_ept2kPiyb1lQ2jJEygn_y5n2mHeNFVMEad_V0QeZ1eqrm-Gs33iIKmgS8TlIKlPLExhs_-6IZrtH2-DWfXhpuqSXwOBwkpxAYNipJXDVwTOfNafSgqLxqNPpd5Mv6tmGuOZwzCiP1YRIOifjgnc0eW6LSHW3s312IgOyovIWiXdHix6lc_j3A3ttKiAT5KRhnWB1wWT20ldUe1QQ5RvY3w-CI-fJPZA31PO7RRCgvGedEFNqwCLcSnsV1QJVs";

    static String authorization = server + "/oauth/token";
    static String profiles = "/stb/perfiles/";
    static String account = "/stb/cuenta/";
    static String credentials = server + "/" + account + "login/";

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
