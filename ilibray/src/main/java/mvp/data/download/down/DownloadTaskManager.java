package mvp.data.download.down;

import android.content.Context;

import mvp.data.store.FilePathManager;
import mvp.data.download.down.entity.ChuckRelevance;
import mvp.data.download.down.entity.DownloadEntity;
import mvp.data.download.down.sql.DownloadRepository;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;

/**
 * Created by ericYang on 2017/6/12.
 * Email:eric.yang@huanmedia.com
 * 执行任务和重建任务等
 */

public class DownloadTaskManager {
    private  OkHttpClient mClient;
    private  DownloadRepository mDownloadRepostory;
    private  ExecutorService mNetExcutor;
    private File mDir;
    private DownLoadExcutor mLoadExcutor;
    private static DownloadTaskManager instance;
//    private static HashMap<String, Future> mFutureHashMap = new HashMap<>();
    private static HashMap<String, DownloadTask> mTaskHashMap = new HashMap<>();

    private DownloadTaskManager(Context context, OkHttpClient client, File dir) {
        this.mClient = client;
        if (dir == null) {
            dir = FilePathManager.getCacheDir(context);
        }
        this.mDir = dir;
        mLoadExcutor = new DownLoadExcutor();
        mNetExcutor= Executors.newFixedThreadPool(DownLoadExcutor.getMaxTaskSize());
        mDownloadRepostory=new DownloadRepository(context);

    }

    public DownloadRepository getDownloadRepostory() {
        return mDownloadRepostory;
    }

    public static DownloadTaskManager getInstance(Context context, OkHttpClient client, File dir) {
        if (instance == null) {
            synchronized (DownloadTaskManager.class) {
                if (instance == null) {
                    instance = new DownloadTaskManager(context, client, dir);
                }
            }
        }
        return instance;
    }

    public void setDownloadDir(File dir) {
        mDir = dir;
    }

    public static synchronized HashMap<String, DownloadTask> getTaskHashMap() {
        return mTaskHashMap;
    }

    /**
     * 添加任务 通过任务对象创建下载任务
     *
     * @param task
     * @return
     */
    public boolean addTask(DownloadTask task) {
        //判断是否有重复任务
        //添加任务并保存到数据库和文件
        //创建downClient并添加到下载队列
        if (mTaskHashMap.get(task.getKey()) == null) {
            task.setSaveDir(mDir);
//            mFutureHashMap.put(task.getKey(), mNetExcutor.submit(task.createDownloadTarget(mClient,mLoadExcutor,mDownloadRepostory)));
            DownloadTarget downloadTarget =task.setDownloadTaskManager(this).createDownloadTarget(mClient,mLoadExcutor,mDownloadRepostory);
            mNetExcutor.execute(downloadTarget);
            mTaskHashMap.put(task.getKey(), task);
            return true;
        } else {
            Utils.log("【任务已存在列表中】");
//            DownLoadRxObserver.getDefault().post(new DownloadStatus().setState(DownloadTask.ERROR).setDescribe("任务已存在列表中"));
        }
        return false;
    }

    /**
     * 获取一个任务信息
     * @param entity
     * @return
     */
    public DownloadTask getTask(DownloadEntity entity) {
        //判断是否有重复任务
        //添加任务并保存到数据库和文件
        //创建downClient并添加到下载队列
        DownloadTask downloadTask=mTaskHashMap.get(entity.getKey());
        if (downloadTask != null) {
            return downloadTask;
        }
        return null;
    }

    /**
     * 移除任务不会删除数据记录和源文件
     * @param entity
     */
    protected void removeTask(DownloadEntity entity) {
        //判断是否有重复任务
        //添加任务并保存到数据库和文件
        //创建downClient并添加到下载队列
        mTaskHashMap.remove(entity.getKey());
//        mFutureHashMap.remove(entity.getKey());
    }

    /**
     * 取消任务并删除源文件
     * @param entity
     */
    public void deleteTask(DownloadEntity entity) {
        //判断是否有重复任务
        //添加任务并保存到数据库和文件
        //创建downClient并添加到下载队列
        DownloadTask task = mTaskHashMap.remove(entity.getKey());
        if (task!=null)
            task.cancleAndDeleteTask();
        else {
            DownloadStatus status = new DownloadStatus(entity);
            ChuckRelevance chuckRelevance= Utils.getFiles(entity.getFixName(),entity.getSave_address());
            Utils.deleteFiles(chuckRelevance.tempFile,chuckRelevance.tempFile);
            status.setState(DownloadTask.DELETE);
            getDownloadRepostory().delete(entity.getId());
            DownLoadRxObserver.getDefault().post(status);
        }
    }
    public void cancleTask(DownloadEntity entity) {
        //判断是否有重复任务
        //添加任务并保存到数据库和文件
        //创建downClient并添加到下载队列
        DownloadTask task = mTaskHashMap.remove(entity.getKey());
             if (task!=null){
                 task.cancleTask();
             }
             else {
                 DownloadStatus downloadStatus  = new DownloadStatus(entity);
                 downloadStatus.setState(DownloadTask.PAUSE);
                 DownLoadRxObserver.getDefault().post(downloadStatus);
             }
//        mFutureHashMap.remove(entity.getKey()).cancel(true);
    }

    /**
     * 取消所有下载并销毁下载器实例
     * 不会删除数据记录和源文件
     */
    void destroy(){
        for (String s : mTaskHashMap.keySet()) {
            DownloadTask task = mTaskHashMap.get(s);
            if (task != null)
                task.cancleTask();
        }
        mTaskHashMap.clear();
        mLoadExcutor.getThreadPoolExecutor().shutdown();
        mNetExcutor.shutdown();
        mClient=null;
        mDownloadRepostory = null;
        mDir=null;
        instance =null;
    }

}
