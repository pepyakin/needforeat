package me.pepyakin.her.bot;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class SpeechTest {

    @Test
    public void retrieveFirstWord() throws Exception {
        MockRng mockRng = new MockRng();
        Speech speech = new Speech(mockRng);

        mockRng.setNextRandom(0);
        String firstWord = speech.pickNextWord();

        assertThat(firstWord, equalTo(Speech.vocabulary[0]));
    }
}
