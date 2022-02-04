package riblet.wordle.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record WordleResponse(UUID seed, List<Guess> guesses) {

    public static WordleResponse empty() {
        return new WordleResponse(UUID.randomUUID(), new ArrayList<>(6));
    }

    public static WordleResponse fromSeed(final UUID seed) {
        return new WordleResponse(seed, new ArrayList<>(6));
    }

}
