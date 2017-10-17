package mvp.data.net.converter;

import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public final class RetryWhenHandler implements Function<Observable<Throwable>, ObservableSource<?>> {

  private static final int INITIAL = 1;
  private int maxConnectCount = 1;

  public RetryWhenHandler(int retryCount) {
    this.maxConnectCount += retryCount;
  }


  @Override
  public ObservableSource<?> apply(Observable<Throwable> errorObservable) throws Exception {
    return errorObservable.zipWith(Observable.range(INITIAL, maxConnectCount),
            new BiFunction<Throwable, Integer, ThrowableWrapper>() {
              @Override
              public ThrowableWrapper apply(Throwable throwable, Integer integer) throws Exception {
                //①
                if (throwable instanceof IOException) return new ThrowableWrapper(throwable, integer);
                return new ThrowableWrapper(throwable, maxConnectCount);
              }
            }).concatMap(new Function<ThrowableWrapper, ObservableSource<?>>() {
      @Override
      public ObservableSource<?> apply(ThrowableWrapper throwableWrapper) throws Exception {
        final int retryCount = throwableWrapper.getRetryCount();
        Log.i("RetryHandler",retryCount+"");

        //②
        if (maxConnectCount == retryCount) {
          return Observable.error(throwableWrapper.getSourceThrowable());
        }

        //③
        return Observable.timer((long) Math.pow(2, retryCount), TimeUnit.SECONDS, Schedulers.single());
      }
    });
  }

  private static final class ThrowableWrapper {

    private Throwable sourceThrowable;
    private Integer retryCount;

    ThrowableWrapper(Throwable sourceThrowable, Integer retryCount) {
      this.sourceThrowable = sourceThrowable;
      this.retryCount = retryCount;
    }

    Throwable getSourceThrowable() {
      return sourceThrowable;
    }

    Integer getRetryCount() {
      return retryCount;
    }
  }
}