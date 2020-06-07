package lab.wikiapi;

import lab.domain.page.Result;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WikiPageLoader {
    @GET("/w/api.php?action=query&format=json&prop=extracts&utf8=1&formatversion=2&explaintext=1&exsectionformat=plain")
    Call<Result> getPage(@Query("titles") String title);
}
