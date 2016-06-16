package me.pepyakin.needforeat.bot;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

import static me.pepyakin.needforeat.bot.TestSubscriberMatchers.emittedValueCount;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class ImpulseProviderTest {

    private MockRng staticRng;
    private TestScheduler testScheduler;
    private TestSubscriber<Void> testSubscriber;
    private ImpulseProvider impulseProvider;

    @Before
    public void before() {
        staticRng = new MockRng();
        staticRng.setNextRandom(0);
        testScheduler = Schedulers.test();
        testSubscriber = new TestSubscriber<>();
        impulseProvider = new ImpulseProvider(testScheduler, staticRng);
    }

    @Test
    public void initialNoEmit() {
        impulseProvider.impulse().subscribe(testSubscriber);
        assertThat(testSubscriber, emittedValueCount(equalTo(0)));
    }

    @Test
    public void noEmitBeforeThreshold() {
        staticRng.setNextRandom(500);
        impulseProvider.impulse().subscribe(testSubscriber);
        testScheduler.advanceTimeBy(2000, TimeUnit.MILLISECONDS);
        assertThat(testSubscriber, emittedValueCount(equalTo(0)));
    }

    @Test
    public void emitAfterThreshold() {
        staticRng.setNextRandom(500);
        impulseProvider.impulse().subscribe(testSubscriber);
        testScheduler.advanceTimeBy(2500, TimeUnit.MILLISECONDS);
        assertThat(testSubscriber, emittedValueCount(equalTo(1)));
    }

    @Test
    public void emitSeq() {
        staticRng.setNextRandom(500);
        impulseProvider.impulse().subscribe(testSubscriber);

        staticRng.setNextRandom(100);
        testScheduler.advanceTimeBy(2500, TimeUnit.MILLISECONDS);

        assertThat(testSubscriber, emittedValueCount(equalTo(1)));
        testScheduler.advanceTimeBy(2500, TimeUnit.MILLISECONDS);
        assertThat(testSubscriber, emittedValueCount(equalTo(2)));
    }
}
