package lab.wikiapi;

import lab.domain.title.TitleResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface WikiTitlesService {
    @GET("/w/api.php?action=query&format=json&list=categorymembers&cmlimit=max")
    Call<TitleResult> getTitles(
            @Query("cmtitle") String category,
            @Query("cmcontinue") String next
    );
}
