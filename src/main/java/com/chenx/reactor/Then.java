package com.chenx.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Then {
    public static void main(String[] args) {
        Flux.just(1, 2, 3)
            .doOnNext(System.out::println)
            // 等待上一个流的complete/error信号，然后执行
            .thenMany(Flux.just(4, 5, 6))
            .doOnNext(System.out::println)
            .thenMany(Mono.justOrEmpty(7))
            .doOnNext(System.out::println)
            .subscribe();
    }
}
