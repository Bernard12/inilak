package lab.domain.title;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TitleResult {
    @SerializedName(value = "continue")
    private TitleContinue continueOptional;
    private TitleQuery query;
}
