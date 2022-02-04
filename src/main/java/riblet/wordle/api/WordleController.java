package riblet.wordle.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import riblet.wordle.model.Guess;
import riblet.wordle.model.WordleResponse;
import riblet.wordle.service.InvalidWordleException;
import riblet.wordle.service.WordleService;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
public class WordleController {

    private final WordleService service;

    public WordleController(WordleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<WordleResponse> currentGame(final HttpServletRequest request) {
        WordleResponse wordle = getSessionWordle(request);
        return new ResponseEntity<>(wordle, HttpStatus.OK);
    }

    @GetMapping("/new")
    public ResponseEntity<WordleResponse> newGame(final HttpServletRequest request) {
        WordleResponse wordle = resetWordle(request);
        return new ResponseEntity<>(wordle, HttpStatus.OK);
    }

    @GetMapping("/new/{seed}")
    public ResponseEntity<WordleResponse> fromSeed(final HttpServletRequest request, final UUID seed) {
        WordleResponse wordle = WordleResponse.fromSeed(seed);
        setWordle(request, wordle);

        return new ResponseEntity<>(wordle, HttpStatus.OK);
    }

    @PostMapping("guess/{word}")
    public ResponseEntity<WordleResponse> guess(@PathVariable final String word, final HttpServletRequest request) {
        WordleResponse wordle = getSessionWordle(request);

        Guess guess = this.service.evaluate(wordle.seed(), wordle.guesses(), word);
        wordle.guesses().add(guess);

        setWordle(request, wordle);

        return new ResponseEntity<>(wordle, HttpStatus.OK);
    }

    // TODO error response
    @ExceptionHandler(InvalidWordleException.class)
    public ResponseEntity<WordleResponse> handleInvalidGuess(final HttpServletRequest request, final Exception ex) {
        WordleResponse response = getSessionWordle(request);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // session methods

    private WordleResponse getSessionWordle(final HttpServletRequest request) {
        WordleResponse wordle = getWordle(request);
        if (wordle == null) {
            wordle = WordleResponse.empty();
        }

        setWordle(request, wordle);

        return wordle;
    }

    private WordleResponse resetWordle(final HttpServletRequest request) {
        WordleResponse wordle = WordleResponse.empty();
        setWordle(request, wordle);

        return wordle;
    }

    private static WordleResponse getWordle(final HttpServletRequest request) {
        return (WordleResponse) request.getSession().getAttribute("wordle");
    }

    private static void setWordle(final HttpServletRequest request, final WordleResponse wordle) {
        request.getSession().setAttribute("wordle", wordle);
    }

}
