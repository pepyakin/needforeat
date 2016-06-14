package me.pepyakin.her.bot;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static me.pepyakin.her.bot.TestSubscriberMatchers.valueCount;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ImpulseProviderTest {
    @Test
    public void testTimings() throws Exception {
        MockRng staticRng = new MockRng();

        TestScheduler testScheduler = Schedulers.test();
        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        ImpulseProvider impulseProvider = new ImpulseProvider(
                testScheduler, staticRng);

        // First iteration, 2000 + 500
        staticRng.setNextRandom(500);
        impulseProvider.impulse().subscribe(testSubscriber);

        // There should be no value emitted,
        assertThat(testSubscriber, valueCount(equalTo(0)));

        staticRng.setNextRandom(100);
        testScheduler.advanceTimeBy(2500, TimeUnit.MILLISECONDS);

        assertThat(testSubscriber, valueCount(equalTo(1)));

        testScheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        assertThat(testSubscriber, valueCount(equalTo(1)));

        testScheduler.advanceTimeBy(2100, TimeUnit.MILLISECONDS);
        assertThat(testSubscriber, valueCount(equalTo(2)));
    }

    private static class MockRng implements Rng {
        private int nextRandom;

        void setNextRandom(int nextRandom) {
            this.nextRandom = nextRandom;
        }

        @Override
        public int nextInt(int n) {
            return nextRandom;
        }
    }
}
