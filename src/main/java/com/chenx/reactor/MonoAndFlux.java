package com.chenx.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/*
Mono和Flux都实现了Publisher，Mono表示0或1个元素，Flux表示一系列元素.
对于例如计算、数据库请求或请求外部服务的情况下，如果期望最多一个结果，应该使用Mono，如果期望多个结果，应该使用Flux。
Mono像Optional，Flux像List。
 */
public class MonoAndFlux {
    public static void main(String[] args) {
//        intro();
//        mono();
        flux();
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
