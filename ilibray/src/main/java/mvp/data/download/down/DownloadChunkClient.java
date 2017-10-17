package mvp.data.download.down;

import mvp.data.download.down.entity.ChuckRelevance;
import mvp.data.download.down.entity.DownloadRange;
import mvp.data.download.down.sql.DownloadRepository;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ericYang on 2017/6/14.
 * Email:eric.yang@huanmedia.com
 * what?
 */

class DownloadChunkClient implements Runnable ,DownloadErrorDispose{
    private final DownloadTask mTask;
    private final int mIndex;
    private final FileHelper mFileHelper;
    private final OkHttpClient mClient;
    private final DownloadRepository mRepository;
    private Request mRequest;
    private final ChuckRelevance mChuckRelevance;
    private int retryTimes;
    Response response;
    public DownloadChunkClient(DownloadRepository repository, OkHttpClient client, FileHelper fileHelper, ChuckRelevance chuckRelevance, DownloadTask task, int index) {
        this.mTask = task;
        this.mIndex = index;
        this.mFileHelper = fileHelper;
        this.mClient = client;
        this.mChuckRelevance= chuckRelevance;
        this.mRepository = repository;
    }

    public DownloadTask getTask() {
        return mTask;
    }

    @Override
    public void run() {
        //1.获取文件路径
        //2.获取文件网络大小
        //3.未下载过创建文件建立下载
        //4.已下载过 获取下载进度下载文件
        //5 通知更新
        DownloadStatus status = new DownloadStatus(mTask.getEntity());
        try {
                //获取块数据
                if (!mTask.isChuckRunning() && retryTimes==0){
                    mTask.setChuckRunning(true);
                    mTask.setStartTime(System.currentTimeMillis());
                    status.setState(DownloadTask.START);
                    DownLoadRxObserver.getDefault().post(status);
                    mRepository.update(status);
                }
                DownloadRange downloadRange =mFileHelper.readDownloadRange(mChuckRelevance.tempFile,mIndex);
                Request.Builder builder = mTask.getEntity().bulideHeader();
                builder.header("Range", "bytes="+downloadRange.start+"-"+downloadRange.end);
                builder = mTask.getDownloadTarget().setRequestMethod(builder);
                Request request=builder.build();
                Utils.log("【获取区块数据】:"+"Chuck ID:"+mIndex +"  "+downloadRange.toString());
                 response = mClient.newCall(request).execute();
                if (response.isSuccessful() && response.body()!=null){
                    connectionSuccess();
                    Utils.log("【分块信息】:"+"ContentLenght:"+Utils.contentLength(response)+"  Content-Ranges:"+Utils.contentRange(response));
                    mFileHelper.saveFile(mRepository,this,mTask.getDownloadTarget(),mIndex,mChuckRelevance.tempFile,mChuckRelevance.saveFile,response.body());
                }else {
                    mTask.remove();
                    mTask.setErrorChuck();
                    status.setState(DownloadTask.ERROR);
                    status.setDescribe(response.code()+"");
                    DownLoadRxObserver.getDefault().post(status);
                    mRepository.update(status);
                    Utils.log("【下载失败】:"+"chuck ID:"+mIndex+" 错误码:"+response.code());
                }

        } catch (IOException e) {
            retry(e);
        }finally {
            if (response!=null){
                response.close();
                response=null;
            }
        }


    }

    @Override
    public void retry(Exception e) {
        if (response!=null){
            response.close();
            response=null;
        }
        if (retryTimes>10){
            String errorMessage = e == null ? "下载失败" : e.getMessage();
            DownloadStatus status = new DownloadStatus(mTask.getEntity());
            status.setState(DownloadTask.ERROR).setDescribe(errorMessage);
            DownLoadRxObserver.getDefault().post(status);
            mTask.remove();
            mTask.setErrorChuck();
            mRepository.update(status);
            Utils.log(errorMessage);
            return;
        }
        ++retryTimes;
        Utils.log("【重试 "+retryTimes+"】");
        run();
    }

    @Override
    public void connectionSuccess() {
        Utils.log("【连接成功】");
        retryTimes=0;
    }
}
