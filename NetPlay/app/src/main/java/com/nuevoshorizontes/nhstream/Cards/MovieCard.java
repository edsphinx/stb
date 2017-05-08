package com.nuevoshorizontes.nhstream.Cards;

import com.google.gson.annotations.SerializedName;

public class MovieCard {
    @SerializedName("id")
    private int mId;
    @SerializedName("nombre")
    private String mNombre = "";
    @SerializedName("descripcion")
    private String mDescripcion = "";
    @SerializedName("clasificacion")
    private String mClasificacion = "";
    @SerializedName("idioma")
    private String mIdioma = "";
    @SerializedName("type")
    private MovieCard.Type mType;
    @SerializedName("categoria")
    private String mCategoria = "";
    @SerializedName("logo")
    private String mThumbnail = "";
    @SerializedName("background")
    private String mBackground = "";
    @SerializedName("stream")
    private String mStream = "";
    @SerializedName("width")
    private int mWidth;
    @SerializedName("height")
    private int mHeight;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmNombre() {
        return mNombre;
    }

    public void setmNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getmDescripcion() {
        return mDescripcion;
    }

    public void setmDescripcion(String mDescripcion) {
        this.mDescripcion = mDescripcion;
    }

    public String getmClasificacion() {
        return mClasificacion;
    }

    public void setmClasificacion(String mClasificacion) {
        this.mClasificacion = mClasificacion;
    }

    public String getmIdioma() {
        return mIdioma;
    }

    public void setmIdioma(String mIdioma) {
        this.mIdioma = mIdioma;
    }

    public MovieCard.Type getmType() {
        return mType;
    }

    public void setmType(MovieCard.Type mType) {
        this.mType = mType;
    }

    public String getmCategoria() {
        return mCategoria;
    }

    public void setmCategoria(String mCategoria) {
        this.mCategoria = mCategoria;
    }

    public String getmThumbnail() {
        return mThumbnail;
    }

    public void setmThumbnail(String mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public String getmBackground() {
        return mBackground;
    }

    public void setmBackground(String mBackground) {
        this.mBackground = mBackground;
    }

    public String getmStream() {
        return mStream;
    }

    public void setmStream(String mStream) {
        this.mStream = mStream;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    public enum Type {
        APP,
        MOVIE_COMPLETE,
        MOVIE,
        MOVIE_BASE,
        ICON,
        SQUARE_BIG,
        SINGLE_LINE,
        GAME,
        SQUARE_SMALL,
        DEFAULT,
        SIDE_INFO,
        SIDE_INFO_TEST_1,
        TEXT,
        CHARACTER,
        GRID_SQUARE,
        VIDEO_GRID

    }
}
