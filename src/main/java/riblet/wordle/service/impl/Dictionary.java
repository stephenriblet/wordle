package riblet.wordle.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
class Dictionary {

    private final List<String> values = new ArrayList<>();
    private final BigInteger size;

    Dictionary(@Value("classpath:wordle.dict") final Resource path) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(path.getFile()))) {
            String word = br.readLine();
            while (word != null) {
                this.values.add(word);
                word = br.readLine();
            }
        }

        Collections.sort(this.values);

        this.size = BigInteger.valueOf(this.values.size());
    }

    boolean isValidWord(final String word) {
        return this.find(word, 0, this.values.size() - 1);
    }

    private boolean find(final String word, final int low, final int high) {
        int middle = low + ((high - low) / 2);
        String dictWord = this.values.get(middle);

        int stringCompare = word.compareTo(dictWord);
        if (stringCompare == 0) {
            return true;
        }

        if (low > high) {
            return false;
        }

        // it comes alphabetically before
        if (stringCompare < 0) {
            return find(word, low, middle - 1);
        }

        // it comes alphabetically after
        return find(word, middle + 1, high);
    }

    String fromSeed(final UUID seed) {
        byte[] bytes = getBytesFromUUID(seed);

        BigInteger bigInteger = new BigInteger(bytes);
        int index = bigInteger.mod(this.size).intValue();

        return this.values.get(index);
    }


    private static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

}
