package lab;

import io.reactivex.rxjava3.core.*;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lab.domain.title.TitleContinue;
import lab.domain.title.TitlePage;
import lab.domain.title.TitleQuery;
import lab.domain.title.TitleResult;
import lab.wikiapi.WikiTitlesService;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class TitleLoaders {
    private final static OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(100, 1, TimeUnit.SECONDS))
            .build();

    private final static Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl("https://en.wikipedia.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();
    private final static WikiTitlesService service = retrofit.create(WikiTitlesService.class);

    public Observable<String> loadTitles2(String category) {
        return Observable.create(obs -> this.loadAllTitles(obs, category));
    }


    private void loadAllTitles(ObservableEmitter<String> obs, String category) {
        Try<TitleResult> getTitlesTry = this.retrieveTitles(category, Option.none());

        TitleResult titleResult = getTitlesTry.get();
        Option<String> optionNext = Option.of(titleResult.getContinueOptional())
                .map(TitleContinue::getCmcontinue);
        this.extractTitles(titleResult).forEach(obs::onNext);

        while (optionNext.isDefined()) {
            Try<TitleResult> nextResultsTry = this.retrieveTitles(category, optionNext);

            this.extractTitles(nextResultsTry.get()).forEach(obs::onNext);

            optionNext = Option.of(nextResultsTry)
                    .map(Try::get).filter(Objects::nonNull)
                    .map(TitleResult::getContinueOptional).filter(Objects::nonNull)
                    .map(TitleContinue::getCmcontinue).filter(Objects::nonNull);
        }
    }

    private List<String> extractTitles(TitleResult result) {
        Option<List<String>> optionalTitles = Option.of(result)
                .map(TitleResult::getQuery)
                .map(TitleQuery::getCategorymembers)
                .map(List::ofAll)
                .map(list -> list.map(TitlePage::getTitle));

        if (optionalTitles.isEmpty()) {
            return List.empty();
        }

        Predicate<String> isNotCategory = x -> !x.contains("Category:");
        return optionalTitles.get().filter(isNotCategory);
    }

    private Try<TitleResult> retrieveTitles(String category, Option<String> next) {
        return Try.of(() -> service.getTitles(category, next.getOrNull()).execute().body())
                .recover(x -> {
                    System.err.println("[TITLE] Sad but wiki fucked up :(");
                    return this.retrieveTitles(category, next).get();
                });
    }
}
