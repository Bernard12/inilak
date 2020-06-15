package lab.domain.category;

import lombok.Data;

import java.util.List;

@Data
public class CategoryQuery {
    private List<Category> categorymembers;
}
