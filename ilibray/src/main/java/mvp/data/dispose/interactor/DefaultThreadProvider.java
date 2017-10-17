package mvp.data.dispose.interactor;


import mvp.data.dispose.executor.JobExecutor;
import mvp.data.dispose.executor.PostExecutionThread;
import mvp.data.dispose.executor.ThreadExecutor;
import mvp.data.dispose.executor.UIThread;

/**
 * Created by ericYang on 2017/5/19.
 * Email:eric.yang@huanmedia.com
 * what?
 */
public  class DefaultThreadProvider {
    private static DefaultThreadProvider instance;
    private JobExecutor mJobExecutor;
    private UIThread mUIThread;
    private DefaultThreadProvider() {
        this.mJobExecutor=new JobExecutor();
        this.mUIThread=new UIThread();
    }
    public static DefaultThreadProvider getInstance() {
        if (instance==null){
            synchronized (DefaultThreadProvider.class)
            {
                if (instance==null){
                    instance = new DefaultThreadProvider();
                }
            }
        }
        return instance;
    }

    public ThreadExecutor getThreadExecutor() {
        return mJobExecutor;
    }

    public PostExecutionThread getPostExcution() {
        return mUIThread;
    }
}
