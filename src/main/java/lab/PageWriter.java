package lab;

import lab.domain.page.Page;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PageWriter {

    private Writer writer;
    private Writer writerMeta;

    public PageWriter(String path, String metaPath) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
        writerMeta = new BufferedWriter(new FileWriter(metaPath));
    }

    public void write(Page page) throws IOException {
        writer.write(page.getTitle());
        writer.write("\n");
        writer.write(page.getExtract());
        writer.write("\n");
    }

    public void writeMetadata(Page page) throws Exception {
        writerMeta.write(page.getTitle());
        writerMeta.write("\n");
        String wikiApiURL = String.format(
                "https://ru.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&utf8=1&formatversion=2&explaintext=1&exsectionformat=plain&titles=%s",
                URLEncoder.encode(page.getTitle(), StandardCharsets.UTF_8.toString())
        );
        writerMeta.write(wikiApiURL);
        writerMeta.write("\n");
    }

    public void close() throws IOException {
        System.out.println("CLOSE");
        this.writer.close();
        this.writerMeta.close();
    }
}
