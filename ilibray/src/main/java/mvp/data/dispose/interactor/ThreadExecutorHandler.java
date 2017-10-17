package mvp.data.dispose.interactor;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ericYang on 2017/5/19.
 * Email:eric.yang@huanmedia.com
 * what?
 */
public class  ThreadExecutorHandler{
    public static <T>  ObservableTransformer<T, T> toMain(DefaultThreadProvider mThreadHandler) {
        return new ObservableTransformer<T, T>() {
            @Override//.map(Reply::getData)
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.from(mThreadHandler.getThreadExecutor()))
                        .observeOn(mThreadHandler.getPostExcution().getScheduler());
            }
        };
    }
}
