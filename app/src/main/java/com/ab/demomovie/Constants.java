package com.ab.demomovie;

public class Constants {
    public static final String TAG = "MovieApp";
    private static final String IP_ADDRESS = "http://192.168.5.5/movieapp/";
    public static final String URL_CREATE = IP_ADDRESS + "create.php";
    public static final String URL_READ = IP_ADDRESS + "index.php";
    public static final String URL_READ_DATA = IP_ADDRESS + "movie_show.php?mid=";
    public static final String URL_UPDATE = IP_ADDRESS + "update.php";
    public static final String URL_DELETE = IP_ADDRESS + "delete.php";
    public static final String URL_CATEGORY = IP_ADDRESS + "category.php";
    public static final String URL_IMAGES = IP_ADDRESS + "images/";

    // KEY's
    public static final String mTitle = "title";
    public static final String mDesc = "desc";
    public static final String mYear = "year";
    public static final String mDuration = "duration";
    public static final String mDirector = "directors";
    public static final String mCategory = "category";
    public static final String mImage = "image";
}
