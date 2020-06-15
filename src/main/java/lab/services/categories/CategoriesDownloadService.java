package lab.services.categories;

import io.reactivex.rxjava3.core.Observable;
import lab.domain.category.Category;
import lab.domain.category.CategoryResult;
import lab.wikiapi.WikiCategoriesService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CategoriesDownloadService {
    private final static Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl("https://ru.wikipedia.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private final static WikiCategoriesService service = retrofit.create(WikiCategoriesService.class);
    private final static Set<String> usedCategories = new HashSet<>();

    public Observable<String> getAllCategories(String category) {
        usedCategories.add(category);
        List<String> subs = getAllSubCategories(category);
        if (!subs.isEmpty()) {
            return Observable.merge(
                    Observable.just(category),
                    Observable.fromIterable(subs)
                            .filter(x -> !usedCategories.contains(x))
                            .flatMap(this::getAllCategories)
            );
        }
        return Observable.just(category);
    }

    private List<String> getAllSubCategories(String category) {
        try {
            CategoryResult result = service.getAllSubCategories(category).execute().body();
            assert result != null;
            return result.getQuery().getCategorymembers()
                    .stream()
                    .map(Category::getTitle)
                    .collect(Collectors.toList());
        } catch (Exception exp) {
            return this.getAllSubCategories(category);
        }
    }
}
