package lab;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import lab.services.categories.CategoriesDownloadService;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class Application {
    public static void main(String[] args) throws Exception {
//        final String outputPath = "/home/ivan/projects/inilak/categories.txt";
//        downloadAllCategories(outputPath);
        // ignore
        String BASE = "/home/ivan/projects/inilak";

        String categoriesPath = String.format("%s/categories.txt", BASE);
        String titlesPath = String.format("%s/titles.txt", BASE);
        String parsedPath = String.format("%s/parsed.txt", BASE);
        String metaPath = String.format("%s/meta.txt", BASE);

//        Disposable allItems = downloadAllItems(categoriesPath, parsedPath, metaPath);
//        Disposable d = downloadAllTitles(categoriesPath, titlesPath);
//        Scanner sc = new Scanner(System.in);
//        sc.nextLine();
//        d.dispose();
    }

//    private static Disposable downloadAllItems(String categoriesPath, String parsedOutput, String metaOutputPath) throws Exception {
//
//        PagesLoader pagesLoader = new PagesLoader();
//
//        Function<Page, Page> formatter = (page) -> {
//            String text = page
//                    .getExtract()
//                    .replaceAll("[#*]", "")
//                    .replaceAll("\n", " ");
//            page.setExtract(text);
//            return page;
//        };
//
//        PageWriter pageWriter = new PageWriter(parsedOutput, metaOutputPath);
//
//        @NonNull Disposable disposable =
//                Flowable.fromIterable(categories)
//                        .take(1)
//                        .filter(x -> x.replaceAll(":", "").length() == x.length() - 1)
//                        .doAfterNext(System.out::println)
//                        .parallel(8)
//                        .runOn(Schedulers.from(ForkJoinPool.commonPool()))
//                        .flatMap(titleLoaders::loadTitles)
//                        .flatMap(pagesLoader::loadTexts)
//                        .map(formatter::apply)
//                        .sequential()
//                        .distinct()
//                        .subscribe(x -> {
//                            pageWriter.write(x);
//                            pageWriter.writeMetadata(x);
//                        }, Throwable::printStackTrace, pageWriter::close);
//        return disposable;
//    }

    private static Disposable downloadAllTitles(String categoriesPath, String titlesOutput) throws Exception {
        List<String> categories = List.empty();
        BufferedReader reader = new BufferedReader(new FileReader(categoriesPath));
        String line;
        while ((line = reader.readLine()) != null) {
            categories = categories.prepend(line);
        }
        reader.close();

        TitleLoaders titleLoaders = new TitleLoaders();
        Observable<String> categories$ = Observable.fromIterable(categories);
        System.setOut(new PrintStream(new FileOutputStream(titlesOutput)));
        return categories$
                .flatMap(titleLoaders::loadTitles2)
                .subscribe(System.out::println, Throwable::printStackTrace);
    }

    private static void downloadAllCategories(@NonNull String outputPath) throws Exception {
        System.setOut(new PrintStream(new FileOutputStream(outputPath)));
        CategoriesDownloadService service = new CategoriesDownloadService();
        Disposable subscribe = service.getAllCategories("Category:Cinema_of_the_United_States")
                .subscribe(System.out::println, Throwable::printStackTrace, () -> System.err.println("Bye"));
    }

    private static void filterDuplicates(String inPath, String outPath) throws Exception {
        Set<String> linesSet = HashSet.empty();
        BufferedReader reader = new BufferedReader(new FileReader(inPath));
        String line;
        while ((line = reader.readLine()) != null) {
            linesSet = linesSet.add(line);
        }
        reader.close();
        System.out.println("HASH SET SIZE: " + linesSet.size());

        BufferedWriter writer = new BufferedWriter(new FileWriter(outPath));
        for (String s : linesSet) {
            writer.write(s);
            writer.write('\n');
        }
        writer.close();
    }
}
