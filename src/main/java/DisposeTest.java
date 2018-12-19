import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;

public class DisposeTest {

    @Test
    public void notWork() throws Exception {
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
                        fail("这不该执行");

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        log("DisposeTest.onError");
                    }
                });


        log("DisposeTest.main");
        log("disposable数量:" + compositeDisposable.size());
        compositeDisposable.dispose();
        log("disposable数量:" + compositeDisposable.size());
        TimeUnit.MILLISECONDS.sleep(500);
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void isWork2() throws Exception {
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
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                })
                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                        log("DisposeTest.onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        log("DisposeTest.onComplete");
                        fail("这不该执行");

                    }

                    @Override
                    public void onError(Throwable e) {
                        log("DisposeTest.onError");
                        throw new RuntimeException(e);
                    }
                });


        log("DisposeTest.main");
        log("disposable数量:" + compositeDisposable.size());
        compositeDisposable.dispose();
        log("disposable数量:" + compositeDisposable.size());
        TimeUnit.MILLISECONDS.sleep(500);
        TimeUnit.SECONDS.sleep(3);

    }

    /**
     * 打印当前线程的调用堆栈
     */
    void printTrack() {
        StackTraceElement[] st = Thread.currentThread().getStackTrace();
        if (st == null) {
            System.out.println("无堆栈...");
            return;
        }
        StringBuffer sbf = new StringBuffer();
        for (StackTraceElement e : st) {
            if (sbf.length() > 0) {
                sbf.append(" <- ");
                sbf.append(System.getProperty("line.separator"));
            }
            sbf.append(java.text.MessageFormat.format("{0}.{1}() {2}"
                    , e.getClassName()
                    , e.getMethodName()
                    , e.getLineNumber()));
        }
        System.out.println(sbf.toString());
    }

    @Test
    public void isWork() throws Exception {
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
                        fail("这不该执行");
                    }
                });
        disposable.dispose();
        System.out.println("DisposeTest.test2");
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    public void test3() throws Exception {
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
                        fail("这不该执行");

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        log("DisposeTest.onError");
                    }
                });

        log("DisposeTest.main");
        disposableAtomicReference.get().dispose();
        TimeUnit.SECONDS.sleep(3);
    }

    synchronized static void log(String message) {
        System.out.println("[" + System.currentTimeMillis() + "] (" + Thread.currentThread().getName() + ") " + message);
    }


}
