package riblet.wordle.service.impl;

import org.springframework.stereotype.Service;
import riblet.wordle.model.Guess;
import riblet.wordle.model.Validity;
import riblet.wordle.service.InvalidWordleException;
import riblet.wordle.service.WordleService;

import java.util.*;

@Service
public class WordleServiceImpl implements WordleService {

    private final riblet.wordle.service.impl.Dictionary dictionary;

    WordleServiceImpl(final Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public Guess evaluate(final UUID seed, final List<Guess> guesses, final String guess) {
        validate(guesses, guess); // TODO decide on what layer to trim.

        String answer = this.dictionary.fromSeed(seed);

        return compareGuess(guess, answer);
    }

    private void validate(final List<Guess> guesses, final String guess) {
        if (guess.length() != 5) {
            throw new InvalidWordleException("Must contain exactly 5 letters.");
        }

        if (!this.dictionary.isValidWord(guess)) {
            throw new InvalidWordleException("%s is not in our dictionary.".formatted(guess));
        }

        if (guesses.stream().map(Guess::word).anyMatch(w -> w.equals(guess))) {
            throw new InvalidWordleException("%s has already been guessed!".formatted(guess));
        }

    }

    private Guess compareGuess(final String guess, final String answer) {
        Map<String, Long> occurences = getAnswerOccurences(answer);

        String[] letters = guess.split("");
        List<Validity> validities = new ArrayList<>(6);
        for (int i = 0; i < guess.length(); i++) {
            String letter = letters[i];
            if (answer.substring(i, i + 1).equals(letter)) {
                validities.add(Validity.EXACT);

                int numOccurs = occurences.get(letter).intValue();
                occurences.put(letter, Long.valueOf(numOccurs - 1));
                continue;
            }

            boolean containsLetter = occurences.getOrDefault(letter, 0L).intValue() > 0;
            if (containsLetter) {
                validities.add(Validity.PRESENT);
                int numOccurs = occurences.get(letter).intValue();
                occurences.put(letter, Long.valueOf(numOccurs - 1));
                continue;
            }

            validities.add(Validity.WRONG);
        }

        return new Guess(guess, validities);
    }

    // java please be smarter.
    private Map<String, Long> getAnswerOccurences(final String answer) {
        Map<String, Long> occurences = new HashMap<>();
        for (String letter : answer.split("")) {
            Long count = occurences.get(letter);
            if (count == null) {
                occurences.put(letter, Long.valueOf(1));
                continue;
            }

            occurences.put(letter, Long.valueOf(count.longValue() + 1));
        }

        return occurences;
    }

}
