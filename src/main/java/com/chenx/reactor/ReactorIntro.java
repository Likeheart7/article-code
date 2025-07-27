package com.chenx.reactor;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class ReactorIntro {
    private static final Logger log = LoggerFactory.getLogger(ReactorIntro.class);

    public static void main(String[] args) {
//        fluxAndMono();
//        subscribe();
//        behindSubscribe();
//        backpressure();
//        map();
//        combine();
    }


    private static void fluxAndMono() {
        Flux<Integer> just = Flux.just(1, 2, 3, 4, 5);
        Mono<Integer> mono = Mono.just(1);  //单个
    }

    private static void subscribe() {
        ArrayList<Integer> els = new ArrayList<>();
        Flux.range(1, 10)
            .log()
            .subscribe(els::add);
        assertIterableEquals(IntStream.rangeClosed(1, 10).boxed().toList(), els);
    }


    private static void behindSubscribe() {
        ArrayList<Integer> els = new ArrayList<>();
        Flux.range(1, 5)
            .log()
            .subscribe(new Subscriber<Integer>() {
                @Override
                public void onSubscribe(Subscription s) {
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(Integer integer) {
                    els.add(integer);
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {

                }
            });
        assertIterableEquals(IntStream.rangeClosed(1, 5).boxed().toList(), els);
    }


    private static void backpressure() {
        ArrayList<Integer> els = new ArrayList<>();
        Flux.range(1, 5)
            .log()
            .subscribe(new Subscriber<Integer>() {
                private Subscription s;
                int onNextAmount;

                @Override
                public void onSubscribe(Subscription s) {
                    this.s = s;
                    s.request(2);
                }

                @Override
                public void onNext(Integer integer) {
                    els.add(integer);
                    onNextAmount++;
                    // 每取两个处理好后再请求两个
                    if (onNextAmount % 2 == 0) {
                        s.request(2);
                    }
                }

                @Override
                public void onError(Throwable t) {

                }

                @Override
                public void onComplete() {

                }
            });
    }


    private static void map() {
        ArrayList<Integer> els = new ArrayList<>();
        Flux.just(1, 2, 3, 4)
            .log()
            .map(i -> {
                log.info("{}:{}", i, Thread.currentThread());
                return i * 2;
            })
            .subscribe(els::add);
    }

    public static void combine() {
        ArrayList<String> els = new ArrayList<>();
        Flux.just(1, 2, 3, 4)
            .log()
            .map(i -> i * 2)
            .zipWith(Flux.range(0, Integer.MAX_VALUE), // zipWith将两个流合并，参数为另一个流，元素合并逻辑
                (one, two) -> String.format("First Flux: %d, Second Flux: %d", one, two))
            .subscribe(els::add);
        assertIterableEquals(List.of("First Flux: 2, Second Flux: 0",
            "First Flux: 4, Second Flux: 1",
            "First Flux: 6, Second Flux: 2",
            "First Flux: 8, Second Flux: 3"), els);
        // [First Flux: 2, Second Flux: 0, First Flux: 4, Second Flux: 1, First Flux: 6, Second Flux: 2, First Flux: 8, Second Flux: 3]
        System.out.println(els);
    }
}
