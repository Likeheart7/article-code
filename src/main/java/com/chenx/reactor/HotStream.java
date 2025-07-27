package com.chenx.reactor;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class HotStream {
    public static void main(String[] args) {
//       connectableFlux();
//        throttling();
    }


    private static void connectableFlux() {
        ConnectableFlux<Object> publish = Flux.create(fluxSink -> {
                while (true) {
                    fluxSink.next(System.currentTimeMillis());
                }
            })
            .publish();
        // 每个元素会输出两次
        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);
        publish.connect();
    }


    private static void throttling() {
        ConnectableFlux<Object> publish = Flux.create(sink -> {
                while (true) {
                    sink.next(System.currentTimeMillis());
                }
            })
            .sample(Duration.ofSeconds(2)) // 指定时间间隔背压
            .publish();

        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);
        publish.subscribe(System.out::println);
        publish.connect();
    }
}
