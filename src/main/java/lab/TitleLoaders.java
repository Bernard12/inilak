package lab;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import lab.domain.title.TitlePage;
import lab.domain.title.TitleResult;
import lab.wikiapi.WikiTitlesService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.stream.Collectors;

public class TitleLoaders {
    private final static Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl("https://ru.wikipedia.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private final static WikiTitlesService service = retrofit.create(WikiTitlesService.class);

    Flowable<String> loadTitles(String category) throws Exception {
        try {
            TitleResult result = service
                    .getTitles(category, null)
                    .execute()
                    .body();

            List<String> titles =
                    result.getQuery()
                            .getCategorymembers()
                            .stream()
                            .map(TitlePage::getTitle)
                            .filter(x -> !x.contains(":"))
                            .collect(Collectors.toList());

            if (result.getContinueOptional() == null) {
                return Observable.fromIterable(titles).toFlowable(BackpressureStrategy.BUFFER);
            }

            String next = result.getContinueOptional().getCmcontinue();
            boolean goon = true;
            while (goon) {
                TitleResult next_result = service.getTitles(category, next).execute().body();
                if (next_result.getContinueOptional() == null) {
                    goon = false;
                } else {
                    next = next_result.getContinueOptional().getCmcontinue();
                }
                titles.addAll(
                        next_result
                                .getQuery()
                                .getCategorymembers()
                                .stream()
                                .map(TitlePage::getTitle)
                                .filter(x -> !x.contains(":"))
                                .collect(Collectors.toList())
                );
            }

            return Observable.fromIterable(titles).toFlowable(BackpressureStrategy.BUFFER);
        } catch (Exception exception) {
            return this.loadTitles(category);
        }
    }
}
