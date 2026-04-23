package br.com.alexandreluchetti.cinealert.dataprovider.imdb;

import lombok.Getter;

@Getter
public enum ImdbPath {

    FIND_Q("/title/find?q="),
    GET_OVERVIEW_DETAILS_TCONST("/title/get-overview-details?tconst={?1}&currentCountry=BR"),
    GET_TOP_RATED_MOVIES("/title/get-top-rated-movies"),
    GET_MOST_POPULAR_MOVIES("/title/get-most-popular-movies");

    private final String value;

    ImdbPath(String value) {
        this.value = value;
    }
}
