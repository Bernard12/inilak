package lab.wikiapi;

import lab.domain.category.CategoryQuery;
import lab.domain.category.CategoryResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface WikiCategoriesService {
    @GET("w/api.php?action=query&format=json&list=categorymembers&cmtype=subcat&cmlimit=max")
    Call<CategoryResult> getAllSubCategories(@Query("cmtitle") String baseCategory);
}
