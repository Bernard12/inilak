package lab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import lab.domain.title.TitlePage;
import lab.domain.title.TitleResult;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class TitleLoaders {
    private final static HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module());

    Flowable<String> loadTitles(String category) throws Exception {
        try {

            String URL = String.format(
                    "https://ru.wikipedia.org/w/api.php?action=query&format=json&list=categorymembers&cmtitle=%s&cmlimit=max",
                    URLEncoder.encode(category, StandardCharsets.UTF_8.toString())
            );

//            System.out.println(String.format("[Debug]: (%s) URL %s", category, URL));
            HttpRequest request = HttpRequest.newBuilder(new URI(URL)).build();
            // todo get ALL title with continue
            TitleResult result = sendRequest(request);

            List<String> titles =
                    result.getQuery()
                            .getCategorymembers()
                            .stream()
                            .map(TitlePage::getTitle)
                            .filter(x -> !x.contains(":"))
                            .collect(Collectors.toList());

            if (result.getContinueOptional().isEmpty()) {
                return Observable.fromIterable(titles).toFlowable(BackpressureStrategy.BUFFER);
            }

            String next = result.getContinueOptional().get().getCmcontinue();
            boolean goon = true;
            while (goon) {
                String NEXT_URL = String.format("%s&cmcontinue=%s", URL, URLEncoder.encode(next, StandardCharsets.UTF_8.toString()));
                HttpRequest next_request = HttpRequest.newBuilder(new URI(NEXT_URL)).build();
                TitleResult next_result = sendRequest(next_request);
                if (next_result.getContinueOptional().isEmpty()) {
                    goon = false;
                } else {
                    next = next_result.getContinueOptional().get().getCmcontinue();
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
        } catch (Exception e) {
            return this.loadTitles(category);
        }
    }

    private TitleResult sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return parse(response);
    }

    private TitleResult parse(HttpResponse<String> response) throws Exception {
        return mapper.readValue(response.body(), TitleResult.class);
    }
}
