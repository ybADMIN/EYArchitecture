package mvp.data.download.down;

import android.support.annotation.IntDef;
import android.text.TextUtils;

import mvp.data.download.down.entity.DownloadEntity;
import mvp.data.download.down.sql.DownloadRepository;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by ericYang on 2017/6/1.
 * Email:eric.yang@huanmedia.com
 * 任务信息
 */

public class DownloadTask{
    private  DownloadTaskManager mDownloadTaskManager;
    private  DownloadEntity mEntity;
    private File saveDir;
    private DownloadTarget mDownloadTarget;
    private long startTime=0;
    public static final int FAIL = -1;
    public static final int ERROR = -2;
    public static final int DELETE = -3;
    public static final int DEFAULT = 0;
    public static final int PREPARE = 1;
    public static final int START = 2;
    public static final int PAUSE = 3;
    public static final int LOADING = 4;
    public static final int FINISH = 5;
    private  int mChuckFinish;
    private volatile boolean mErrorChuck;
    private volatile long mLastTimeUpUI=0;
    private volatile boolean mChuckRunning;

    public DownloadTask(DownloadEntity downloadEntity) {
        this.mEntity=downloadEntity;
    }
    File getSaveDir() {
        return saveDir;
    }

    DownloadTask setDownloadTaskManager(DownloadTaskManager manager) {
         mDownloadTaskManager =manager;
        return this;
    }
    protected void remove(){
        if (mDownloadTaskManager !=null)
            mDownloadTaskManager.removeTask(mEntity);
    }
    private DownloadTask setChunk(boolean chunk) {
        this.mEntity.setChunk(chunk);
        return this;
    }


    public boolean isChunk() {
        return mEntity.isChunk();
    }
    DownloadTask setSaveDir(File saveDir) {
        this.saveDir = saveDir;
        this.mEntity.setSave_address(saveDir.getAbsolutePath());
        return this;
    }
    public long getId() {
        return mEntity.getId();
    }
    public String getKey() {
        return mEntity.getKey();
    }

    void newEntity(DownloadEntity download){
        this.mEntity = download;
    }

    public String getUrl() {
        return mEntity.getUrl();
    }

    public DownloadEntity getEntity() {
        return mEntity;
    }

    /**
     * 获得一个任务下载的客户端
     * @param client
     * @param loadExcutor
     * @return
     */
    DownloadTarget createDownloadTarget(OkHttpClient client, DownLoadExcutor loadExcutor, DownloadRepository repository) {
        mDownloadTarget = new DownloadTarget(client,this,loadExcutor,repository);
        return mDownloadTarget;
    }


    /**
     * 取消任务
     * @return
     */
    void cancleTask() {
        if (mDownloadTarget !=null){
             mDownloadTarget.cancle();
        }
    }

    /**
     * 取消任务并删除源文件
     */
    void cancleAndDeleteTask() {
        if (mDownloadTarget !=null){
             mDownloadTarget.cancleAndDelete();
        }
    }

    DownloadTarget getDownloadTarget() {
        return mDownloadTarget;
    }

    /**
     * 分块是否下载完成
     * @return
     */
    synchronized int chuckFinish() {
        return --mChuckFinish;
    }
    public boolean isFinish() {
        return mChuckFinish==0;
    }
    boolean getErrorChuck() {
        return mErrorChuck;
    }

    void setErrorChuck() {
        mErrorChuck=true;
    }
    /**
     * 分块下载数量
     * @param chuckFinish
     */
    void setChuckFinish(int chuckFinish) {
        mChuckFinish = chuckFinish;
    }

    long lastTimeUpUI() {
        return mLastTimeUpUI;
    }
    void setLastTimeUpUI(long lastTime) {
         this.mLastTimeUpUI = lastTime;
    }

    DownloadTask setStartTime(long startTime) {
        this.startTime = startTime;
        return this;
    }

    long getStartTime() {
        return startTime;
    }

    boolean isChuckRunning() {
        return mChuckRunning;
    }

    DownloadTask setChuckRunning(boolean chuckRunning) {
        mChuckRunning = chuckRunning;
        return this;
    }

    @IntDef({FAIL, PAUSE, LOADING,PREPARE,DELETE ,FINISH,START,DEFAULT,ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Tpye {
    }

    /**
     *   根据返回数据创建名称如果返回数据中没有名称则通过URL创建
     */
    String generateName(Response res) {
        return getFileNmae(mEntity.getUrl(), Utils.contentDisposition(res));
    }
    private String getFileNmae(String url, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            if (url != null) {
                fileName=Utils.matcherName(url);
                if (TextUtils.isEmpty(fileName)) {
                    fileName = "unKnow"+getUrl().hashCode()+"";
                }
            }
        }
        mEntity.setFixName(fileName);
        if (mEntity.getExtension()==null)
            mEntity.setExtension(Utils.matcherExtension(fileName));
        return fileName;
    }
}
