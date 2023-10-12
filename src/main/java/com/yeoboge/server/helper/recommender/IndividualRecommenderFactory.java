package com.yeoboge.server.helper.recommender;

import com.yeoboge.server.domain.entity.Genre;
import com.yeoboge.server.enums.RecommendTypes;
import com.yeoboge.server.repository.RecommendRepository;
import lombok.Builder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Builder
public class IndividualRecommenderFactory implements RecommenderFactory {
    private RecommendRepository repository;
    private List<Genre> genres;
    private long userId, numRating;

    public List<Recommender> getRecommenders(WebClient webClient) {
        List<Recommender> recommenders = getSQLRecommenders();
        recommenders.addAll(getGenreRecommenders(webClient));
        return recommenders;
    }

    /**
     * 사용자가 선호하는 장르에 해당하는 추천 보드게임 목록을 생성할 {@link Recommender} 리스트를 반환함.
     *
     * @return 해당 장르 별 추천 목록을 생성할 {@link Recommender} 리스트
     */
    private List<Recommender> getGenreRecommenders(WebClient webClient) {
        List<Recommender> genreRecommenders = new ArrayList<>();
        for (Genre genre : genres) {
            Recommender recommender = numRating >= 10
                    ? AIIndividualRecommender.builder()
                    .repository(repository)
                    .type(RecommendTypes.FAVORITE_GENRE)
                    .client(webClient)
                    .userId(userId)
                    .genreId(genre.getId())
                    .genreName(genre.getName())
                    .build()
                    : GenreIndividualRecommender.builder()
                    .repository(repository)
                    .type(RecommendTypes.FAVORITE_GENRE)
                    .genreId(genre.getId())
                    .genreName(genre.getName())
                    .build();
            genreRecommenders.add(recommender);
        }

        return genreRecommenders;
    }

    /**
     * AI 생성 관련 로직이 아닌 DB 조회 로직에만 해당하는 카테고리들의 {@link Recommender} 리스트를 반환함.
     *
     * @return DB 조회 로직과 관련한 {@link Recommender} 리스트
     */
    private List<Recommender> getSQLRecommenders() {
        List<Recommender> recommenderList = new ArrayList<>();

        recommenderList.add(BookmarkIndividualRecommender.builder()
                .repository(repository)
                .type(RecommendTypes.MY_BOOKMARK)
                .userId(userId)
                .build());
        recommenderList.add(FriendIndividualRecommender.builder()
                .repository(repository)
                .type(RecommendTypes.FRIENDS_FAVORITE)
                .userId(userId)
                .build());
        recommenderList.add(Top10IndividualRecommender.builder()
                .repository(repository)
                .type(RecommendTypes.TOP_10)
                .build());

        return recommenderList;
    }
}
