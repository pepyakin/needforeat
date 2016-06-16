package me.pepyakin.needforeat.bot;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import rx.observers.TestSubscriber;

final class TestSubscriberMatchers {
    private TestSubscriberMatchers() {
    }

    public static TypeSafeMatcher<TestSubscriber<?>> emittedValueCount(final Matcher<Integer> matcher) {
        return new TypeSafeMatcher<TestSubscriber<?>>() {
            @Override
            protected boolean matchesSafely(TestSubscriber<?> item) {
                return matcher.matches(item.getOnNextEvents().size());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("observable emitted value count of ");
                description.appendDescriptionOf(matcher);
            }
        };
    }
}
