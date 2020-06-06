package testing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args) throws Exception {
        List<String> categories = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\ivan\\IdeaProjects\\kalinin\\parsed.txt"));
        String line;
        while ((line = reader.readLine()) != null) {
            categories.add(line);
        }
        int total_titles = 0;
        int total_texts = 0;
        for (int i = 0; i < categories.size(); i++) {
            if (i % 2 == 0) {
                total_titles += categories.get(i).length();
            } else {
                total_texts += categories.get(i).length();
            }
        }
        System.out.println(total_titles);
        System.out.println(total_texts);
        int a = 5 / 2;
    }
}
