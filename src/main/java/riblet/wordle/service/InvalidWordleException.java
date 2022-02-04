package riblet.wordle.service;

import java.io.Serial;

public class InvalidWordleException extends IllegalArgumentException {

    @Serial
    private static final long serialVersionId = -1L;

    public InvalidWordleException(final String reason) {
        super(reason);
    }

}
