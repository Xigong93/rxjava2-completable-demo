# rxjava2 Completable解除订阅不起作用的bug
不起作用的代码
```java

    @Test
    public void notWork() throws Exception {
        final CompositeDisposable compositeDisposable = new CompositeDisposable();
        Completable.complete()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeDemo.run1");
                        String a = null;
                        for (int i = 0; i < 4000; i++) {
                            a += String.valueOf(i);
                        }
                        log("DisposeDemo.run2");
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                        log("DisposeDemo.onSubscribe");
                    }

                    @Override
                    public void onComplete() {
                        log("DisposeDemo.onComplete");
                        fail("这不该执行");

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        log("DisposeDemo.onError");
                    }
                });


        log("DisposeDemo.main");
        log("disposable数量:" + compositeDisposable.size());
        compositeDisposable.dispose();
        log("disposable数量:" + compositeDisposable.size());
        TimeUnit.MILLISECONDS.sleep(500);
        TimeUnit.SECONDS.sleep(3);
    }


```
起作用的代码

```java
    @Test
    public void isWork() throws Exception {
        Disposable disposable = Completable.complete()
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeDemo.run1");
                        String a = null;
                        for (int i = 0; i < 4000; i++) {
                            a += String.valueOf(i);
                        }
                        log("DisposeDemo.run2");
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        log("DisposeDemo.onComplete");
                        fail("这不该执行");
                    }
                });
        disposable.dispose();
        System.out.println("DisposeDemo.test2");
        TimeUnit.SECONDS.sleep(3);
    }

```


关键是分析方法，小的切入点，知道脉络和拆解脉络。    


# 重点是，需要了解，dispose 方法的执行流程
> 了解到，在订阅之前，销毁，是起作用的
> 在订阅之后，销毁，是不起作用的

## 在订阅之前销毁，dispose() 执行堆栈

## 在订阅之后销毁，dispose() 执行堆栈


# 如何获取方法的运行堆栈
使用这个工具 https://github.com/gousiosg/java-callgraph