package lab.domain.title;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TitleResult {
    @JsonProperty(value = "continue")
    private Optional<TitleContinue> continueOptional = Optional.empty();
    private TitleQuery query;
}
