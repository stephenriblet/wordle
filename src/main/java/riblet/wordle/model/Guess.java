package riblet.wordle.model;

import java.util.Collections;
import java.util.List;

public record Guess(String word, List<Validity> validities) {

    public Guess {
        validities = Collections.unmodifiableList(validities);
    }

}
