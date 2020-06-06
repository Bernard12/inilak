package lab;

import lab.domain.page.Page;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class PageWriter {

    private Writer writer;

    public PageWriter(String path) throws IOException {
        writer = new BufferedWriter(new FileWriter(path));
    }

    public void write(Page page) throws IOException {
        writer.write(page.getTitle());
        writer.write("\n");
        writer.write(page.getExtract());
        writer.write("\n");
    }

    public void close() throws IOException {
        System.out.println("CLOSE");
        this.writer.close();
    }
}
