package mvp.data.download.down;

import mvp.data.download.down.sql.DownloadRepository;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * Created by ericYang on 2017/6/14.
 * Email:eric.yang@huanmedia.com
 * what?
 */

class DownloadSingleClient implements Runnable, DownloadErrorDispose {
    private final DownloadTask mTask;
    private final FileHelper mFileHelper;
    private final DownloadRepository mRepository;
    private final OkHttpClient mClient;
    private int retryTimes;
    private Response mResp;

    public DownloadSingleClient(DownloadRepository repository, OkHttpClient client, DownloadTask task) {
        this.mTask = task;
        this.mFileHelper = new FileHelper();
        this.mRepository = repository;
        this.mClient = client;
    }

    public DownloadTask getTask() {
        return mTask;
    }

    @Override
    public void run() {
        try {
            getResponse();

            DownloadStatus status = new DownloadStatus(mTask.getEntity());
            if (retryTimes == 0) {
                status.setState(DownloadTask.START);
                DownLoadRxObserver.getDefault().post(status);
                mRepository.update(status);
            }
            File fileSaveDir = new File(mTask.getSaveDir(), mTask.getEntity().getFixName());
            ResponseBody body = mResp.body();
            if (body != null) {
                try {
                    mFileHelper.prepareFile(fileSaveDir, body.contentLength());
                } catch (IOException e) {
                    //// FIXME: 2017/6/15
                    status.setState(DownloadTask.ERROR);
                    status.setDescribe(e.getMessage());
                    DownLoadRxObserver.getDefault().post(status);
                    mRepository.update(status);
                    mTask.remove();
                    Utils.log("【文件创建失败】:" + e.getMessage());
                }
                mFileHelper.saveFile(mRepository, this, mTask.getDownloadTarget(), fileSaveDir, body);
            } else {
                //// FIXME: 2017/6/15
                status.setState(DownloadTask.ERROR);
                status.setDescribe(mResp.code() + "");
                DownLoadRxObserver.getDefault().post(status);
                mRepository.update(status);
                mTask.remove();
                Utils.log("【文件下载失败】:body 不能为空");
            }
        } catch (IOException e) {
            retry(e);
        } finally {
            if (mResp != null){
                mResp.close();
                mResp=null;
            }
        }
    }


    @Override
    public void retry(Exception e) {
        if (mResp != null){
            mResp.close();
            mResp=null;
        }

        if (retryTimes > 10) {
            String errorMessage = e == null ? "下载失败" : e.getMessage();
            DownloadStatus status = new DownloadStatus(mTask.getEntity());
            status.setState(DownloadTask.ERROR).setDescribe(errorMessage);
            DownLoadRxObserver.getDefault().post(status);
            mTask.remove();
            mRepository.update(status);
            Utils.log(errorMessage);
            return;
        }
        ++retryTimes;
        Utils.log("【重试】" + retryTimes);
        retryDownload();
    }

    private Response getResponse() throws IOException {
        Request.Builder builder = mTask.getEntity().bulideHeader();
        try {
            builder = mTask.getDownloadTarget().setRequestMethod(builder);
        } catch (UnsupportedEncodingException ignored) {
        }
        Request request = builder.build();
        Response response = null;
        response = mClient.newCall(request).execute();
        this.mResp = response;
        return response;
    }

    private void retryDownload() {
        try {
            Response response = getResponse();
            if (response.isSuccessful() && response.body() != null) {
                connectionSuccess();
                run();
            } else {
                DownloadStatus status = new DownloadStatus(mTask.getEntity());
                status.setState(DownloadTask.ERROR);
                status.setDescribe("【下载失败】" + response.code());
                DownLoadRxObserver.getDefault().post(status);
                mRepository.update(status);
                mTask.remove();
            }
        } catch (IOException e) {
            retry(e);
        }
    }

    @Override
    public void connectionSuccess() {
        Utils.log("【重新连接成功】");
        retryTimes = 0;
    }
}
