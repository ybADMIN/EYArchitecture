package mvp.data.download.down;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by ericYang on 2017/6/1.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class DownLoadRxObserver {
    private static volatile DownLoadRxObserver defaultInstance;

    private final Subject<Object> bus;
    // PublishSubject只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public DownLoadRxObserver() {
        bus = PublishSubject.create().toSerialized();
    }
    // 单例RxBus
    public static DownLoadRxObserver getDefault() {
        if (defaultInstance == null) {
            synchronized (DownLoadRxObserver.class) {
                if (defaultInstance == null) {
                    defaultInstance = new DownLoadRxObserver();
                }
            }
        }
        return defaultInstance ;
    }
    public void post (Object o) {
//        if (o instanceof DownloadStatus){
//            Utils.log("任务状态:"+((DownloadStatus)o).getState()+"");
//        }
        bus.onNext(o);
    }
    public <T> Observable<T> register(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}
