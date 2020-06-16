package lab;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lab.domain.page.Page;
import lab.services.categories.CategoriesDownloadService;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

public class Application {
    public static void main(String[] args) throws Exception {
        test2();
        // ignore
        Scanner sc = new Scanner(System.in);
        sc.next();
    }

    private static void test1() throws Exception {
        List<String> categories = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\ivan\\IdeaProjects\\kalinin\\kekus.txt"));
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
                    .replaceAll("\n", " ");
            page.setExtract(text);
            return page;
        };

        PageWriter pageWriter = new PageWriter(
                "C:\\Users\\ivan\\IdeaProjects\\kalinin\\parsed.txt",
                "C:\\Users\\ivan\\IdeaProjects\\kalinin\\parsed-meta.txt"
        );
        @NonNull Disposable disposable =
                Flowable.fromIterable(categories)
                        .filter(x -> x.replaceAll(":", "").length() == x.length() - 1)
                        .doAfterNext(System.out::println)
                        .parallel(50)
                        .runOn(Schedulers.from(ForkJoinPool.commonPool()))
                        .flatMap(titleLoaders::loadTitles)
                        .flatMap(pagesLoader::loadTexts)
                        .map(formatter::apply)
                        .sequential()
                        .distinct()
                        .subscribe(x -> {
                            pageWriter.write(x);
                            pageWriter.writeMetadata(x);
                        }, Throwable::printStackTrace, pageWriter::close);

    }

    private static void test2() throws Exception {
        System.setOut(new PrintStream(new FileOutputStream("C:\\Users\\ivan\\IdeaProjects\\kalinin\\kekus.txt")));
        CategoriesDownloadService service = new CategoriesDownloadService();
        Disposable subscribe = service.getAllCategories("Category:Cinema_of_the_United_States")
                .subscribe(System.out::println, Throwable::printStackTrace, () -> System.err.println("Bye"));
    }
}
