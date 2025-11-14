package org.movie.constant;

public class MovieAPIConstant {
    public static final String DOMAIN = "https://api.themoviedb.org/3";
    public static final String DISCOVERY_MOVIE = "/discover/movie";
    public static final String PARAM_INCLUDE_ADULT = "include_adult";
    public static final String PARAM_INCLUDE_VIDEO = "include_video";
    public static final String PARAM_LANGUAGE = "language";
    public static final String PARAM_PAGE = "page";
    public static final String PARAM_SORT_BY = "sort_by";
    public static final String PARAM_WITH_KEYWORDS = "with_keywords";

    // Default values
    public static final String VALUE_INCLUDE_ADULT = "false";
    public static final String VALUE_INCLUDE_VIDEO = "false";
    public static final String VALUE_LANGUAGE = "en-US";
    public static final String VALUE_PAGE_DEFAULT = "1";
    public static final String VALUE_SORT_BY_POPULARITY = "popularity.desc";
}
