package lab;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import lab.domain.page.Page;
import lab.domain.page.Result;
import lab.wikiapi.WikiPageLoader;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PagesLoader {
    private final static Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl("https://ru.wikipedia.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private final static WikiPageLoader service = retrofit.create(WikiPageLoader.class);

    Flowable<Page> loadTexts(String title) throws Exception {
        try {
            Result result = service.getPage(title).execute().body();
            return Observable.fromIterable(result.getQuery().getPages()).toFlowable(BackpressureStrategy.BUFFER);
        } catch (Exception e) {
            return this.loadTexts(title);
        }
    }
}
