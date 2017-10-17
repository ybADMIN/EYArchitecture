package mvp.data.download.down;

import android.content.Context;
import android.support.annotation.NonNull;

import com.yb.ilibray.BuildConfig;
import mvp.data.store.FilePathManager;
import mvp.data.download.down.entity.DownloadEntity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by ericYang on 2017/6/1.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class DownLoadManager {
    private  OkHttpClient mClient;
    private  Context mContext;
    private  DownloadTaskManager mDownTaskManager;

    private static DownLoadManager instance;

    public DownLoadManager(Context context) {
        this.mContext = context;
        mClient = getOkHttpClient();
        mDownTaskManager=DownloadTaskManager.getInstance(mContext,mClient, FilePathManager.getCacheDir(mContext));
    }


    public static DownLoadManager getInstance(Context context) {
        if (instance == null) {
            synchronized (DownLoadManager.class) {
                if (instance == null) {
                    instance = new DownLoadManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * 添加一个下载
     * @param entity
     */
    public void addDownLoad(DownloadEntity entity){
        mDownTaskManager.addTask(new DownloadTask(entity));
    }

    /**
     * 暂停下载
     * @param downloadTask
     */
    public void pause(DownloadEntity downloadTask){
        mDownTaskManager.cancleTask(downloadTask);
    }

    /**
     * 取消下载并删除文件
     * @param downloadTask
     */
    public  void cancleAndDelete(DownloadEntity downloadTask) {
        mDownTaskManager.cancleTask(downloadTask);
        mDownTaskManager.deleteTask(downloadTask);
    }
    /**
     * 取消所有下载并销毁下载器实例
     * 不会删除数据记录和源文件
     */
    public void destroy(){
       mDownTaskManager.destroy();
        mClient =null;
        mContext=null;
        mDownTaskManager=null;
        instance = null;
    }
    /**
     * 是否存在该下载
     * @param downloadTask
     * @return
     */
    public  boolean exist(DownloadEntity downloadTask) {
        // 2017/6/15 需要考虑是否需要数据库认证，也就是匹配数据库中的任务
      return   mDownTaskManager.getTask(downloadTask)!=null;
    }

    public DownloadTaskManager getDownTaskManager() {
        return mDownTaskManager;
    }

    @NonNull
    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(9, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }
}
