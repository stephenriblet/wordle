package riblet.wordle.service;

import riblet.wordle.model.Guess;

import java.util.List;
import java.util.UUID;

public interface WordleService {

    Guess evaluate(UUID seed, List<Guess> guesses, String guess);

}
