package lab.domain.page;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Page {
    private Long pageid;
    private String title;
    private String extract;

    @Override
    public String toString() {
        return "Page{" +
                "pageid=" + pageid +
                ", title='" + title + '\'' +
                ", extract='" + extract + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Page page = (Page) o;
        return Objects.equals(title, page.title) &&
                Objects.equals(extract, page.extract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, extract);
    }
}
