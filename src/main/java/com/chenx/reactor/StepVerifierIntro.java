package com.chenx.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class StepVerifierIntro {
    public static void main(String[] args) {
//        simpleVerifier();
//        errorVerifier();
//        delayedVerifier();
//        postExecutionVerifier();
    }


    private static void postExecutionVerifier() {
        Flux<Integer> source = Flux.<Integer>create(emitter -> {
            emitter.next(1);
            emitter.next(2);
            emitter.next(3);
            emitter.complete();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            emitter.next(4);
        }).filter(number -> number % 2 == 0);

        StepVerifier
            .create(source)
            .expectNext(2)
            .expectComplete()
            .verifyThenAssertThat()
            .hasDropped(4)
            .tookLessThan(Duration.ofMillis(1050));
    }

    private static void delayedVerifier() {
        StepVerifier
            .withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1)).take(2))
            .expectSubscription()
            .expectNoEvent(Duration.ofSeconds(1))
            .expectNext(0L)
            .thenAwait(Duration.ofSeconds(1))
            .expectNext(1L)
            .verifyComplete();
    }

    private static void errorVerifier() {
        Flux<Integer> flux = Flux.range(1, 5);
        Flux<Integer> source = flux.concatWith(Mono.error(new IllegalArgumentException("UNKNOWN ERROR")));
        StepVerifier.create(source)
            .expectNextCount(5)
            .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException && throwable.getMessage().startsWith("UNK"))
            .verify();
    }

    private static void simpleVerifier() {
        Flux<String> source = Flux.just("Like", "Heart", "John", "Wick", "Max", "Sony")
            .filter(name -> name.length() == 4)
            .map(String::toUpperCase);
        StepVerifier
            .create(source)
            .expectNext("LIKE")
            .expectNextMatches(name -> name.startsWith("JO"))
            .expectNext("WICK", "SONY") // 传入多个会调用多次next
            .expectComplete()
            .verify();
    }
}
