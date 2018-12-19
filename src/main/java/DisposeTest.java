import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;

public class DisposeTest {

    @Test
    public void isWork() throws Throwable {
        final AtomicReference<Throwable> exceptionReference = new AtomicReference<>();

        Disposable disposable = Completable.complete()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeTest.run1");
                        String a = null;
                        for (int i = 0; i < 4000; i++) {
                            a += String.valueOf(i);
                        }
                        log("DisposeTest.run2");
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeTest.onComplete");
                        exceptionReference.set(new AssertionError("这不该执行"));

                    }
                });
        disposable.dispose();
        System.out.println("DisposeTest.test2");
        TimeUnit.SECONDS.sleep(3);

        if (exceptionReference.get() != null) {
            throw exceptionReference.get();
        }
    }

    @Test
    public void isWork2() throws Throwable {
        final CompositeDisposable compositeDisposable = new CompositeDisposable();

        final AtomicReference<Throwable> exceptionReference = new AtomicReference<>();
        Completable.complete()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeTest.run1");
                        String a = null;
                        for (int i = 0; i < 4000; i++) {
                            a += String.valueOf(i);
                        }
                        log("DisposeTest.run2");
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                        log("DisposeTest.onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        log("DisposeTest.onComplete");
//                        fail("这不该执行");
                        exceptionReference.set(new AssertionError("这不该执行"));

                    }

                    @Override
                    public void onError(Throwable e) {
                        log("DisposeTest.onError");
                        exceptionReference.set(e);
                    }
                });


        log("DisposeTest.main");
        log("disposable数量:" + compositeDisposable.size());
        compositeDisposable.dispose();
        log("disposable数量:" + compositeDisposable.size());
        TimeUnit.MILLISECONDS.sleep(500);
        TimeUnit.SECONDS.sleep(3);

        if (exceptionReference.get() != null) {
            throw exceptionReference.get();
        }
    }

    @Test
    public void isWork3() throws Throwable {

        final AtomicReference<Throwable> exceptionReference = new AtomicReference<>();
        DisposableCompletableObserver disposableCompletableObserver = new DisposableCompletableObserver() {

            @Override
            public void onComplete() {
                log("DisposeTest.onComplete");
//                        fail("这不该执行");
                exceptionReference.set(new AssertionError("这不该执行"));

            }

            @Override
            public void onError(Throwable e) {
                log("DisposeTest.onError");
                exceptionReference.set(e);
            }
        };
        // 提前销毁，是起作用的
//        disposableCompletableObserver.dispose();
        Completable.complete()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeTest.run1");
                        String a = null;
                        for (int i = 0; i < 4000; i++) {
                            a += String.valueOf(i);
                        }
                        log("DisposeTest.run2");
                    }
                })
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe(disposableCompletableObserver);


        log("DisposeTest.main");
        // 订阅之后销毁，是没有用的
        disposableCompletableObserver.dispose();
        TimeUnit.SECONDS.sleep(3);
        if (exceptionReference.get() != null) {
            throw exceptionReference.get();
        }
    }


    @Test(expected = AssertionError.class)
    public void notWork() throws Throwable {
        final AtomicReference<Throwable> exceptionReference = new AtomicReference<>();

        final CompositeDisposable compositeDisposable = new CompositeDisposable();
        Completable.complete()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeTest.run1");
                        String a = null;
                        for (int i = 0; i < 4000; i++) {
                            a += String.valueOf(i);
                        }
                        log("DisposeTest.run2");
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                        log("DisposeTest.onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        log("DisposeTest.onComplete");
//                        fail("这不该执行");
                        exceptionReference.set(new AssertionError("这不该执行"));

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        log("DisposeTest.onError");
                        exceptionReference.set(e);

                    }
                });


        log("DisposeTest.main");
        compositeDisposable.dispose();
        TimeUnit.SECONDS.sleep(3);
        if (exceptionReference.get() != null) {
            throw exceptionReference.get();
        }
    }


    @Test(expected = AssertionError.class)
    public void notWork2() throws Throwable {
        final AtomicReference<Throwable> exceptionReference = new AtomicReference<>();

        AtomicReference<Disposable> disposableAtomicReference = new AtomicReference<>();
        Completable.complete()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeTest.run1");
                        String a = null;
                        for (int i = 0; i < 4000; i++) {
                            a += String.valueOf(i);
                        }
                        log("DisposeTest.run2");
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposableAtomicReference.set(d);
                        log("DisposeTest.onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        log("DisposeTest.onComplete");
                        exceptionReference.set(new AssertionError("这不该执行"));

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        log("DisposeTest.onError");
                        exceptionReference.set(e);

                    }
                });

        log("DisposeTest.main");
        disposableAtomicReference.get().dispose();
        TimeUnit.SECONDS.sleep(3);
        if (exceptionReference.get() != null) {
            throw exceptionReference.get();
        }
    }

    synchronized static void log(String message) {
        System.out.println("[" + System.currentTimeMillis() + "] (" + Thread.currentThread().getName() + ") " + message);
    }


}
