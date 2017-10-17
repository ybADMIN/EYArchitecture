package mvp.data.download.down;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import mvp.data.download.down.entity.ChuckRelevance;
import mvp.data.download.down.entity.DownloadEntity;
import mvp.data.download.down.exception.DownloadException;
import mvp.data.download.down.sql.DownloadRepository;
import com.yb.ilibray.utils.data.assist.Check;
import com.yb.ilibray.utils.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ericYang on 2017/6/1.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class DownloadTarget implements Runnable, DownloadOption ,DownloadErrorDispose{
    private final DownLoadExcutor mLoadExcutor;
    private final DownloadRepository mRepository;
    private final DownloadTask mTask;
    private final FileHelper mFileHelper;
    private OkHttpClient mClient;
    private List<Runnable> taskRunnables=new ArrayList<>();
    private volatile boolean isCancle;
    private volatile boolean isDelete;
    private int retryTimes;
    private  Response resp ;
    public DownloadTarget(OkHttpClient client, DownloadTask task, DownLoadExcutor loadExcutor, DownloadRepository repository) {
        this.mClient = client;
        this.mTask = task;
        this.mLoadExcutor = loadExcutor;
        this.mRepository = repository;
        this.mFileHelper = new FileHelper();
        //创建文件夹
        String cachePath = TextUtils.concat(mTask.getSaveDir().getAbsolutePath(), File.separator, Utils.CACHE).toString();
        Utils.mkdirs(cachePath, mTask.getSaveDir().toString());
    }

    public DownloadTask getTask() {
        return mTask;
    }

    @Override
    public void run() {
        //检查下载链接可用性
        //获取网络任务信息
        //本地信息对比下载文件是否改变，改变后重新下载
        //检查是否支持断点续传
        //支持断点续传后分块传输创建分块
        //是否分块
        //如果分块 获取分块信息
        //服务器数据是否改变
        //创建下载参数
        //通过client执行下载
        //发送下载状态
        //保存下载进度数据


        DownloadStatus status = new DownloadStatus(mTask.getEntity());
        status.setState(DownloadTask.PREPARE);
        DownLoadRxObserver.getDefault().post(status);
        try {
            resp = getCheckResponse();
            if (resp == null) return;
            if (isCancle()){
               pauseNotification();
                return;
            }
            //判断是否重名，如果重名重新命名文件并下载
            boolean isExist = mRepository.containsTask(mTask.getKey());
            ChuckRelevance chuckRelevance = Utils.getFiles(mTask.getEntity().getFixName(), mTask.getSaveDir().getAbsolutePath());
            if (isExist) {
                DownloadEntity downloadEntity = mRepository.getTaskInfoWithKey(mTask.getKey());
                mTask.newEntity(downloadEntity);
                //此处文件名有可能被修改过 因此重新获取一次文件名称
                chuckRelevance = Utils.getFiles(mTask.getEntity().getFixName(), mTask.getSaveDir().getAbsolutePath());

                if ((!Check.isNull(mTask.getEntity().lastModify())&&!Check.isNull(Utils.lastModify(resp))) &&
                        !mTask.getEntity().lastModify().equals(Utils.lastModify(resp))){
                    //服务器文件更变处理
                    Utils.log("【服务器文件有更改重新下载】"+"  NameLocal:"+mTask.getEntity().getFixName()+"  RemoteName:"+mTask.generateName(resp));
                    Utils.log("【服务器文件有更改重新下载】"+"  lastmodifyLocal:"+mTask.getEntity().lastModify()+"  RemoteLastmodify:"+Utils.lastModify(resp));
                    modifyDownload(chuckRelevance, resp);
                }else {
                    Utils.log("【已下载任务】" + mTask.getEntity().getFixName());
                    reDownload(resp, chuckRelevance);
                }

            } else {
                Utils.log("【新建下载任务】");
                if ( duplicationName(0)){//如果文件地址不同文件名称相同重命名处理
                    //此处文件名称被修改应该重新获取
                    chuckRelevance = Utils.getFiles(mTask.getEntity().getFixName(), mTask.getSaveDir().getAbsolutePath());
                }

                checkAndDownLoad(resp, chuckRelevance);
            }

        } catch (IOException e) {
                retry(e);
        }finally {
            if (resp!=null){
                resp.close();
                resp=null;
            }
        }


    }


    @Override
    public void retry(Exception e) {
        if (resp!=null){
            resp.close();
            resp=null;
        }
        if (retryTimes>10){
            String errorMessage = e == null ? "下载失败" : e.getMessage();
            DownloadStatus status = new DownloadStatus(mTask.getEntity());
            status.setState(DownloadTask.ERROR).setDescribe(errorMessage);
            DownLoadRxObserver.getDefault().post(status);
            mTask.remove();
            Utils.log(errorMessage);
            return;
        }
        ++retryTimes;
        Utils.log("【重试】"+retryTimes);
        run();
    }

    @Override
    public void connectionSuccess() {
        Utils.log("【连接成功】");
        retryTimes=0;
    }

    /**
     * 如果重名后自动处理并重新命名
     *
     * @return
     */
    private boolean duplicationName(int nameIndex) {
        File file;
        if (nameIndex != 0) {
            file = new File(mTask.getSaveDir(), mTask.getEntity().getFixName() + nameIndex);
        } else {
            file = new File(mTask.getSaveDir(), mTask.getEntity().getFixName());
        }
        if (file.exists()) {
            Utils.log("【重命名处理】");
            if (mTask.getEntity().getExtension() != null) {
                mTask.getEntity().setFixName(mTask.getEntity().getFixName().replace(mTask.getEntity().getExtension(), "") + "(" + (nameIndex++) + ")" + mTask.getEntity().getExtension());
            } else {
                mTask.getEntity().setFixName(mTask.getEntity().getFixName() + (nameIndex++));
            }
            duplicationName(nameIndex);
        } else {
            if (nameIndex != 0) {
                Utils.log("【重命名文件】" + mTask.getEntity().getFixName());
                return true;
            }
        }
        return false;
    }

    /**
     * 访问网络检查任务信息
     *
     * @return 如果检查失败返回空数据
     * @throws IOException
     */
    @Nullable
    private Response getCheckResponse() throws IOException {
        Request.Builder builder = mTask.getEntity().bulideHeader();
        builder.header("Range", "bytes=0-");
        builder = setRequestMethod(builder);
        if (builder == null) {
            return null;
        }
        Request request = builder.build();
        Response resp = mClient.newCall(request).execute();
        if (resp.isSuccessful()) {//请求成功
            connectionSuccess();
            if (Check.isEmpty(mTask.getEntity().getFixName())) {
                mTask.generateName(resp);//设置扩展名
                long contentLength = 0;
                ResponseBody body = resp.body();
                if (body != null) {
                    contentLength = body.contentLength();
                }
                Utils.log("【文件名称:】" + mTask.getEntity().getFixName());
                Utils.log("【文件大小:】" + FileUtils.byteCountToDisplaySize(contentLength));
                Utils.log("【文件Key:】" + mTask.getKey());
            }
        } else {
            Utils.log("【检查任务信息失败】:" + resp.code());
        }
        return resp;
    }

    /**
     * 检查是否支持分块传输 并且下载文件
     */
    private void checkAndDownLoad(Response resp, ChuckRelevance chuckRelevance) throws IOException {
        DownloadStatus status = new DownloadStatus(mTask.getEntity());
            if (Utils.getAvailableStorage()<Utils.contentLength(resp)){//空间不足处理
                DownLoadRxObserver.getDefault().post(status.setState(DownloadTask.ERROR).setDescribe(DownloadException.DOWNLOAD_DISK_NO_SPACE));
                return;
            }
        //服务器最后一次更改时间
        mTask.getEntity().setLastModify(Utils.lastModify(resp));
        if (!isCancle()) {
            long contentLength = Utils.contentLength(resp);
            switch (resp.code()) {
                case 200://不支持断点续传
                    Utils.log("【不支持断点续传】");
                    mTask.getEntity().setStatus(DownloadTask.START);
                    mTask.getEntity().setChunk(false);
                    mRepository.insertTask(mTask.getEntity());
//                  mFutures.add(mLoadExcutor.getThreadPoolExecutor().submit(new DownloadSingleClient(resp, mTask)));
                    DownloadSingleClient runableSingle = new DownloadSingleClient(mRepository, mClient, mTask);
                    mLoadExcutor.getThreadPoolExecutor().execute(runableSingle);
                    taskRunnables.add(runableSingle);
                    break;
                case 206://支持断点续传
                    mTask.getEntity().setStatus(DownloadTask.START);
                    mTask.getEntity().setChunk(true);
                    mRepository.insertTask(mTask.getEntity());//保存下载信息到数据库
                    Utils.log("【支持断点续传】");
                    int chunkSize = DownLoadExcutor.getTaskThreadSize();
                    mTask.setChuckFinish(chunkSize);
                    mFileHelper.prepareFile(chuckRelevance.tempFile, chuckRelevance.saveFile, contentLength);
                    Utils.log("【报文长度】:" + contentLength);
                    for (int i = 0; i < chunkSize; i++) {
//                            mFutures.add(mLoadExcutor.getThreadPoolExecutor().submit(new DownloadChunkClient(mClient,request,mFileHelper,chuckRelevance,mTask, i)));
                        DownloadChunkClient runableChuck = new DownloadChunkClient(mRepository, mClient, mFileHelper, chuckRelevance, mTask, i);
                        mLoadExcutor.getThreadPoolExecutor().execute(runableChuck);
                        taskRunnables.add(runableChuck);
                    }
                    break;
                case 302:
                case 301:
                case 303://30X 跳转
                    Utils.log("【30X 跳转】");
                    status.setState(DownloadTask.ERROR);
                    status.setDescribe("无法下载该文件：" + resp.code());
                    DownLoadRxObserver.getDefault().post(status);
                    break;
                default:
                    status.setState(DownloadTask.ERROR);
                    status.setDescribe("无法下载该文件：" + resp.code());
                    DownLoadRxObserver.getDefault().post(status);
                    break;
            }
        }else {
            pauseNotification();
            mTask.getEntity().setStatus(DownloadTask.PAUSE);
            mRepository.insertTask(mTask.getEntity());
        }
    }

    /**
     * 已下载任务
     *
     * @param resp
     * @param chuckRelevance
     */
    private void reDownload(Response resp, ChuckRelevance chuckRelevance) throws IOException {
        ResponseBody body = resp.body();
        if (isCancle()){
            pauseNotification();
            mTask.getEntity().setStatus(DownloadTask.PAUSE);
            mRepository.update(mTask.getEntity());
            return;
        }
        if (mTask.isChunk()) {
            assert body != null;
            //// FIXME: 2017/6/16 文件下载完成后设置数据库状态
            boolean isFinish = mTask.getEntity().getStatus() == DownloadTask.FINISH;
            if (isFinish && chuckRelevance.saveFile.exists()){//文件已下载完成
                DownloadStatus status = new DownloadStatus(mTask.getEntity());
                status.setState(DownloadTask.ERROR).setDescribe("任务已存在");
                DownLoadRxObserver.getDefault().post(status);
                mTask.remove();
                Utils.log("【任务已存在】");
                return;
            }else {
                boolean isDamage = mFileHelper.tempFileDamaged(chuckRelevance.tempFile, body.contentLength());
                boolean isNoComplete = mFileHelper.fileNotComplete(chuckRelevance.tempFile);
                if (isDamage || !chuckRelevance.saveFile.exists()) {//文件损坏后删除数据重新下载
                    if (isDamage) {
                        Utils.log("【文件损坏】重新下载");
                    } else {
                        Utils.log("【文件删除或移动】重新下载该任务");
                    }
                    reDamgeDowland(chuckRelevance, resp);
                    return;
                }
                if (!isNoComplete) {
                    mTask.remove();
                    DownloadStatus status = new DownloadStatus(mTask.getEntity());
                    status.setState(DownloadTask.FINISH).setDescribe("文件已下载完成");
                    DownLoadRxObserver.getDefault().post(status);
                    Utils.log("【文件已下载完成】");
                } else {

                    int taskThreadSize=0;
                    for (int i = 0; i < DownLoadExcutor.getTaskThreadSize(); i++) {
                        if (mFileHelper.readDownloadRange(chuckRelevance.tempFile,i).size>0){
                            taskThreadSize++;
                        }
                    }
                    mTask.setChuckFinish(taskThreadSize);
                    for (int i = 0; i < DownLoadExcutor.getTaskThreadSize(); i++) {
                        if (mFileHelper.readDownloadRange(chuckRelevance.tempFile,i).size>0){
                            DownloadChunkClient runnableChuck=new DownloadChunkClient(mRepository, mClient, mFileHelper, chuckRelevance, mTask, i);
                            mLoadExcutor.execute(runnableChuck);
                            taskRunnables.add(runnableChuck);
                        }

                    }
                }
            }

        } else {
            Utils.log("【不支持断点续传，重新下载】");
            if (mTask.getEntity().getStatus() != DownloadTask.FINISH || !chuckRelevance.saveFile.exists()) {
                if (mTask.getEntity().getStatus() == DownloadTask.FINISH && !chuckRelevance.saveFile.exists()) {
                    mRepository.update(mTask.getEntity().setStatus(DownloadTask.START));
                    Utils.log("【文件删除或移动】重新下载该任务");
                }
                DownloadSingleClient runnableSingle = new DownloadSingleClient(mRepository, mClient, mTask);
                mLoadExcutor.getThreadPoolExecutor().execute(runnableSingle);
                taskRunnables.add(runnableSingle);
            } else {
                mTask.remove();
                DownloadStatus status = new DownloadStatus(mTask.getEntity());
                status.setState(DownloadTask.FINISH).setDescribe("文件已下载完成");
                DownLoadRxObserver.getDefault().post(status);
                Utils.log("【文件已下载完成】");
            }
        }

    }

    private void pauseNotification() {
        DownloadStatus status = new DownloadStatus(mTask.getEntity());
        status.setState(DownloadTask.PAUSE);
        DownLoadRxObserver.getDefault().post(status);
    }

    /**
     * 服务器文件有修改重新下载
     *
     * @param chuckRelevance
     * @param response
     * @throws IOException
     */
    private void modifyDownload(ChuckRelevance chuckRelevance, Response response) throws IOException {
        reDamgeDowland(chuckRelevance, response);
    }

    /**
     * 断点文件损坏重新下载
     *
     * @param chuckRelevance
     * @param response
     */
    private void reDamgeDowland(ChuckRelevance chuckRelevance, Response response) throws IOException {
        mRepository.delete(mTask.getId());
        Utils.deleteFiles(chuckRelevance.saveFile, chuckRelevance.tempFile);
        mTask.getEntity().setDownloadSize(0);
        mTask.getEntity().setStatus(DownloadTask.PREPARE);
        checkAndDownLoad(response, chuckRelevance);
    }

    @Nullable
    Request.Builder setRequestMethod(Request.Builder builder) throws UnsupportedEncodingException {
        String requestUrl;
        if (mTask.getEntity().getMethod().equals("POST")) {
            Utils.log("【POST下载】");
            requestUrl = mTask.getUrl();
            builder.method(mTask.getEntity().getMethod(), RequestBody.create(MediaType.parse("text/plain"), mTask.getEntity().strParms()));
        } else {
            Utils.log("【GET下载】");

            if (mTask.getEntity().getParms() != null) {
                String paramsStr = mTask.getEntity().getParams();
                requestUrl = String.format("%s?%s", mTask.getUrl(), paramsStr);
            } else {
                requestUrl = mTask.getUrl();
            }
            builder.method(mTask.getEntity().getMethod(), null);
        }
        try {
            builder.url(requestUrl);
        } catch (IllegalStateException | IllegalArgumentException e) {
            Utils.log("【无效的下载链接】");
            return null;
        }
        return builder;
    }

    @Override
    public synchronized void cancle() {
        for (int i = 0; i < taskRunnables.size(); i++) {
           mLoadExcutor.getThreadPoolExecutor().remove( taskRunnables.get(i));
        }
        isCancle = true;
    }


    public boolean isCancle() {
        return isCancle;
    }


    public boolean isDelete() {
        return isDelete;
    }
    /**
     * 取消任务并删除源文件
     * @return
     */
    public void cancleAndDelete(){
        cancle();
        isDelete = true;
    }
}
