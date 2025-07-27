package com.chenx.rxjava;

import io.reactivex.rxjava3.core.Observable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RxJavaErrorHandling {
    public static void main(String[] args) {
        doOnError();
        retryError();
        conditionRetry();
        retryWhen();
    }


    private static void doOnError() {
        AtomicBoolean state = new AtomicBoolean(false);
        RuntimeException unknownError = new RuntimeException("UNKNOWN_ERROR");
        Observable.error(unknownError)
            .doOnError(t -> state.set(true))
            .subscribe(
                i -> {
                },
                System.err::println // 吞掉异常
            );
        assertTrue(state.get());
    }


    /*
    retry会让Observable重新执行创建逻辑，然后从第一个元素开始发送。retry后获取的数据可能和上次不同
     */
    private static void retryError() {
        /*
        onNext: A
        onNext: A
        onNext: A
        onError: java.lang.RuntimeException: UNKNOWN_ERROR
         */
        Observable.create(emitter -> {
                emitter.onNext("A");
                emitter.onError(new RuntimeException("UNKNOWN_ERROR")); // 第一次失败
            })
            .retry(2) // 最多重试 2 次（共 3 次尝试）
            .subscribe(
                item -> System.out.println("onNext: " + item),
                error -> System.out.println("onError: " + error)
            );
    }


    private static void conditionRetry() {
        Observable.create(emitter -> {
                emitter.onNext("A");
                emitter.onError(new RuntimeException("UNKNOWN_ERROR"));
            })
            .doOnError(e -> {
            })
            .retry((limit, throwable) ->
                limit < 4
            )
            .subscribe(i -> {
            }, e -> System.out.println("retry failed: " + e.getMessage()));
    }

    // 看不懂
    private static void retryWhen() {
        Observable.create(s -> {
            System.out.println("subscribing");
            s.onError(new RuntimeException("always fails"));
        }).retryWhen(attempts -> attempts.zipWith(Observable.range(1, 3), (n, i) -> i).flatMap(i -> {
            System.out.println("delay retry by " + i + " second(s)");
            return Observable.timer(i, TimeUnit.SECONDS);
        })).blockingForEach(System.out::println);
    }
}
