package lab;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lab.domain.page.Page;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Application {
    public static void main(String[] args) throws Exception {
        List<String> categories = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\ivan\\IdeaProjects\\kalinin\\cats.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            categories.add(line);
        }

        TitleLoaders titleLoaders = new TitleLoaders();
        PagesLoader pagesLoader = new PagesLoader();

        Function<Page, Page> formatter = (page) -> {
            String text = page
                    .getExtract()
                    .replaceAll("[#*]", "")
                    .replaceAll("\\n", " ");
            page.setExtract(text);
            return page;
        };
        // Observable.fromArray("Категория:Москва")

        PageWriter pageWriter = new PageWriter("C:\\Users\\ivan\\IdeaProjects\\kalinin\\parsed.txt");
        @NonNull Disposable disposable =
                Flowable.fromIterable(categories)
//                        .take(10)
                        .parallel(15)
                        .runOn(Schedulers.from(ForkJoinPool.commonPool()))
                        .flatMap(titleLoaders::loadTitles, false, 10000)
                        .flatMap(pagesLoader::loadTexts, false, 10000)
                        .map(formatter::apply)
//                        .doAfterNext(System.out::println)
                        .sequential()
                        .subscribe(pageWriter::write, Throwable::printStackTrace, pageWriter::close);

        // ignore
        Scanner sc = new Scanner(System.in);
        sc.next();
    }
}
