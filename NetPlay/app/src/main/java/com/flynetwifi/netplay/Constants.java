package com.flynetwifi.netplay;


public interface Constants {

    public static String server = "http://10.115.0.3:8080";

    public static String authorization = "/oauth/token";
    public static String profiles = "/stb/perfiles/";
    public static String account = "/stb/account/";

    public final static String live = "/stb/live";
    public final static String programation = "/stb/live/programacion/";

    public final static String movies = "/stb/peliculas/";
    public final static String movies_details = "/stb/peliculas/detalles/";

    public final static String series = "/stb/series/";
    public final static String details = Constants.series + "detalles/";

    public final static String music = "/stb/musica/videos";
}
