package com.chenx.reactor;

import io.reactivex.rxjava3.core.*;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/*
Mono和Flux都实现了Publisher，Mono表示0或1个元素，Flux表示一系列元素.
对于例如计算、数据库请求或请求外部服务的情况下，如果期望最多一个结果，应该使用Mono，如果期望多个结果，应该使用Flux。
Mono像Optional，Flux像List。
 */
public class MonoAndFlux {
    public static void main(String[] args) {
//        intro();
//        mono();
//        flux();
        fluxWithInterval();
    }

    private static void fluxWithInterval() {
        System.out.println("=====>>> fluxWithInterval");
        Scheduler scheduler = Schedulers.newSingle("print-thread");
        Flux.interval(Duration.ofMillis(0), Duration.ofMillis(500), scheduler)
            .take(10)
            .doOnComplete(scheduler::dispose)
//            .subscribeOn(Schedulers.newSingle("print-thread"))    //无效，被interval内部的强制parallel调度覆盖。
            .subscribe(num -> System.out.println("[" + Thread.currentThread().getName() + "]Received: " + num));
    }

    private static void flux() {
        Flux<String> flux = Flux.just("hello", "mr.");
        StepVerifier.create(flux)
            .expectNext("hello")
            .expectNextMatches(s -> s.startsWith("mr"))
            .verifyComplete();
        Flux<String> fluxWithError = Flux.just("hello", "perfect", "error")
            .map(s -> {
                if (s.equals("error")) {
                    throw new RuntimeException("got error message");
                }
                return s;
            });
        StepVerifier.create(fluxWithError)
            .expectNext("hello")
            .expectNext("perfect")
            .expectError()
            .verify();
    }

    private static void mono() {
        Mono<String> mono = Mono.just("hello");
        StepVerifier.create(mono)
            .expectNext("hello")
            .expectComplete()
            .verify();
    }

    private static void intro() {
        Mono.just(1).log().subscribe(System.out::println);
        Flux.just(1, 2).log().subscribe(System.out::println);
    }
}
