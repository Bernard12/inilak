package lab;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import lab.domain.page.Page;
import lab.domain.page.Result;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class PagesLoader {
    private final static HttpClient client =
            HttpClient
                    .newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();
    private final static ObjectMapper mapper = new ObjectMapper();

    Flowable<Page> loadTexts(String title) throws Exception {
        String URL = String.format(
                "https://ru.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&titles=%s&utf8=1&formatversion=2&explaintext=1&exsectionformat=plain",
                URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
        );
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI(URL)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            Result result = mapper.readValue(body, Result.class);
            return Observable.fromIterable(result.getQuery().getPages()).toFlowable(BackpressureStrategy.BUFFER);
        } catch (Exception e) {
            return this.loadTexts(title);
        }
    }
}
