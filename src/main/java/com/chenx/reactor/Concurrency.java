package com.chenx.reactor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Concurrency {
    private static final Logger log = LoggerFactory.getLogger(Concurrency.class);
    public static void main(String[] args) throws InterruptedException {
        intro();
    }

    private static void intro() throws InterruptedException {
        ArrayList<Integer> els = new ArrayList<>();
        Flux.just(1, 2, 3, 4)
            .log()
            .map(i -> i * 2)
            .subscribeOn(Schedulers.parallel())
            .subscribe(els::add);
        log.info("{}", els); // 可能为空
        TimeUnit.SECONDS.sleep(1);
        log.info("{}", els);
    }
}
