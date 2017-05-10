package com.nuevoshorizontes.nhstream.Cards;

import com.google.gson.annotations.SerializedName;


public class SeriesCard {

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
    private SeriesCard.Type mType;
    @SerializedName("categoria")
    private String mCategoria = "";
    @SerializedName("img")
    private String mThumbnail = "";
    @SerializedName("stream")
    private String mStream = "";
    @SerializedName("width")
    private int mWidth;
    @SerializedName("height")
    private int mHeight;
    @SerializedName("titulo") private String mTitle = "";
    @SerializedName("logo") private String mLogo = "";
    @SerializedName("portada") private String mPortada = "";
    @SerializedName("numero_temporadas") private int mNumeroTemporadas = 0;
    @SerializedName("temporadas") private SeriesSeasonCard[] mTemporadas = null;

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

    public Type getmType() {
        return mType;
    }

    public void setmType(Type mType) {
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

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }


    public String getmLogo() {
        return mLogo;
    }

    public void setmLogo(String mLogo) {
        this.mLogo = mLogo;
    }

    public String getmPortada() {
        return mPortada;
    }

    public void setmPortada(String mPortada) {
        this.mPortada = mPortada;
    }

    public int getmNumeroTemporadas() {
        return mNumeroTemporadas;
    }

    public void setmNumeroTemporadas(int mNumeroTemporadas) {
        this.mNumeroTemporadas = mNumeroTemporadas;
    }

    public SeriesSeasonCard[] getmTemporadas() {
        return mTemporadas;
    }

    public void setmTemporadas(SeriesSeasonCard[] mTemporadas) {
        this.mTemporadas = mTemporadas;
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
