import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

/**
 * 可以解除订阅的观察者
 */
public abstract class DisposableCompletableObserver implements CompletableObserver, Disposable {
    volatile boolean disposed = false;

    @Override
    public synchronized void dispose() {
        disposed = true;
    }

    @Override
    public synchronized boolean isDisposed() {
        return disposed;
    }
}
