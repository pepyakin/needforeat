package me.pepyakin.needforeat.bot;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class SpeechTest {
    @Test
    public void retrieveFirstWord() throws Exception {
        String[] vocabulary = new String[] { "hello world" };

        MockRng mockRng = new MockRng();
        Speech speech = new Speech(vocabulary, mockRng);

        mockRng.setNextRandom(0);
        String firstWord = speech.pickNextWord();

        assertThat(firstWord, equalTo(vocabulary[0]));
    }
}
