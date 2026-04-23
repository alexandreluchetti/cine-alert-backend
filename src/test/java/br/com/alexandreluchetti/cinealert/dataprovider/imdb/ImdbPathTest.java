package br.com.alexandreluchetti.cinealert.dataprovider.imdb;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImdbPathTest {

    @Test
    void values_containsExpectedPaths() {
        assertThat(ImdbPath.FIND_Q.getValue()).isEqualTo("/title/find?q=");
        assertThat(ImdbPath.GET_OVERVIEW_DETAILS_TCONST.getValue()).isEqualTo("/title/get-overview-details?tconst={?1}&currentCountry=BR");
        assertThat(ImdbPath.GET_TOP_RATED_MOVIES.getValue()).isEqualTo("/title/get-top-rated-movies");
        assertThat(ImdbPath.GET_MOST_POPULAR_MOVIES.getValue()).isEqualTo("/title/get-most-popular-movies");
    }
}
