package com.chenx.reactive;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.chenx.reactive.SubjectImpl.subjectMethod;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


public class RxJavaIntro {
    private static final String[] letters = new String[]{"a", "b", "c", "d", "e", "f"};

    public static void main(String[] args) throws InterruptedException {
        observable();
        onNext();
        map();
        scan();
        groupBy();
        filter();
        conditionOperators();
        takeWhile();
        connectableObservables();
        single();
        resourceManagement();
        subjectMethod();
    }


    /*
    Observable类似stream source
     */
    private static void observable() {
        System.out.println("=====>>> Observable");
        ArrayList<String> list = new ArrayList<>();
        Observable<String> observable = Observable.just("silent");
        observable.subscribe(list::add);
        assertEquals("silent", list.getFirst());
    }


    /*
    关于onNext，onError和onCompleted回调
     */
    private static void onNext() {
        System.out.println("=====>>> onNext");
        StringBuilder sb = new StringBuilder();
        Observable<String> observable = Observable.fromArray(letters);
        observable.subscribe(
            sb::append, // onNext
            Throwable::printStackTrace, // onError
            () -> sb.append("_Completed") // onCompleted
        );
        assertEquals("abcdef_Completed", sb.toString());
    }


    private static void map() {
        System.out.println("=====>>> map");
        Observable<String> observable = Observable.fromArray(letters);
        StringBuilder sb = new StringBuilder();
        observable.map(String::toUpperCase)
            .subscribe(sb::append);
        assertEquals("ABCDEF", sb.toString());
    }


    /*
    scan会对每个元素执行scan，并在初始值和处理每个元素之后都发出一次结果
     */
    private static void scan() {
        System.out.println("=====>>>  scan");
        StringBuilder result = new StringBuilder();
        Observable.fromArray(letters)
            .scan(new StringBuilder(), StringBuilder::append)
            .subscribe(result::append);
        assertEquals("aababcabcdabcdeabcdef", result.toString());
    }

    /*
    分组函数
     */
    private static void groupBy() {
        System.out.println("=====>>> groupBy");
        StringBuilder even = new StringBuilder();
        StringBuilder odd = new StringBuilder();
        Observable.fromStream(IntStream.rangeClosed(1, 10).boxed())
            .groupBy(i -> i % 2 == 0 ? "even" : "odd")
            .subscribe(group -> group.subscribe(number -> {
                if ("even".equals(group.getKey())) {
                    even.append(number);
                } else {
                    odd.append(number);
                }
            }));
        assertEquals("246810", even.toString());
        assertEquals("13579", odd.toString());
    }

    private static void filter() {
        System.out.println("=====>>> filter");
        StringBuilder result = new StringBuilder();
        Observable.fromStream(IntStream.rangeClosed(1, 10).boxed())
            .filter(i -> i % 2 == 0)
            .subscribe(result::append);
        assertEquals("246810", result.toString());
    }


    private static void conditionOperators() {
        StringBuilder result = new StringBuilder();
        Observable.empty()
            .defaultIfEmpty("Observable is empty")
            .first("empty cause default first")
            .subscribe(result::append);
        assertEquals("Observable is empty", result.toString());
    }

    /*
     * 截断流，条件limit
     */
    private static void takeWhile() {
        System.out.println("=====>>> takeWhile");
        StringBuilder result = new StringBuilder();
        Observable.fromStream(IntStream.rangeClosed(1, 10).boxed())
            .takeWhile(i -> i < 5)
            .subscribe(result::append);
        assertEquals("1234", result.toString());
    }


    private static void connectableObservables() throws InterruptedException {
        System.out.println("=====>>> connectableObservables");
        String[] result = {""};
        // connect后每200ms产出一个值
        ConnectableObservable<Long> connectableObservable = Observable.interval(200, TimeUnit.MILLISECONDS).publish();
        connectableObservable.subscribe(i -> result[0] += i);
        assertNotEquals("01", result[0]);
        connectableObservable.connect();
        TimeUnit.MILLISECONDS.sleep(500); // 500ms会产出两个，即0和1
        assertEquals("01", result[0]);
    }

    /*
    单元素
     */
    private static void single() {
        System.out.println("=====>>> single");
        String[] result = {""};
        Single<String> single = Observable.just("hello")
            .singleOrError()
            .doOnSuccess(i -> result[0] += i)
            .doOnError(e -> {
                throw new RuntimeException(e.getMessage());
            });
        single.subscribe();
        assertEquals("hello", result[0]);
    }

    /*
    使用using发送
     */
    private static void resourceManagement() {
        System.out.println("=====>>> resourceManagement");
        StringBuilder sb = new StringBuilder();
        Observable<Object> values = Observable.using(
            () -> "MyResource",
            r -> Observable.create(o -> {
                for (Character c : r.toCharArray()) {
                    o.onNext(c);
                }
                o.onComplete();
            }),
            r -> System.out.println("Disposed: " + r)   // 输出resourceSupplier的内容
        );
        values.subscribe(
            sb::append,
            sb::append
        );
        assertEquals("MyResource", sb.toString());
    }
}
class SubjectImpl {

    static Integer subscriber1 = 0;
    static Integer subscriber2 = 0;

    public static Integer subjectMethod() {
        System.out.println("=====>>> subjectMethod");
        PublishSubject<Integer> subject = PublishSubject.create();

        subject.subscribe(getFirstObserver());

        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);

        subject.subscribe(getSecondObserver());

        subject.onNext(4);
        subject.onComplete();
        return subscriber1 + subscriber2;
    }


    static Observer<Integer> getFirstObserver() {
        return new Observer<Integer>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                subscriber1 += value;
                System.out.println("Subscriber1: " + value);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("error");
            }

            @Override
            public void onComplete() {
                System.out.println("Subscriber1 completed");
            }
        };
    }

    static Observer<Integer> getSecondObserver() {
        return new Observer<Integer>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(Integer value) {
                subscriber2 += value;
                System.out.println("Subscriber2: " + value);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("error");
            }

            @Override
            public void onComplete() {
                System.out.println("Subscriber2 completed");
            }
        };
    }
}
