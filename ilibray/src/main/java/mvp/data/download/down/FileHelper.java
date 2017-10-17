package mvp.data.download.down;

import mvp.data.download.down.entity.DownloadRange;
import mvp.data.download.down.sql.DownloadRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import okhttp3.ResponseBody;

import static com.yb.ilibray.utils.io.IOUtils.closeQuietly;

/**
 * Created by ericYang on 2017/6/14.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class FileHelper {
    private static final int EACH_RECORD_SIZE = 16; //long + long = 8 + 8
    private static final String ACCESS = "rw";
    private final static int bufferSize = 2048;
    private int maxThreads = DownLoadExcutor.getTaskThreadSize();
    private int RECORD_FILE_TOTAL_SIZE = EACH_RECORD_SIZE * maxThreads;

    public void saveFile(DownloadRepository repository, DownloadErrorDispose dispose, DownloadTarget target, int i, File tempFile,
                         File saveFile, ResponseBody response) {
        RandomAccessFile record = null;
        FileChannel recordChannel = null;
        RandomAccessFile save = null;
        FileChannel saveChannel = null;
        InputStream inStream = null;
        DownloadStatus status = new DownloadStatus(target.getTask().getEntity());
        if (response == null) {
            target.getTask().remove();
            upRepositoryData(repository, status);
            DownLoadRxObserver.getDefault().post(status.setState(DownloadTask.ERROR).setDescribe("下载失败"));
            Utils.log("【下载失败】" + " 【下载分块:】:" + i);
            //// FIXME: 2017/6/14 如果没有返回类容提示下载失败
            return;
        }
        try {
            try {
                int readLen;

                byte[] buffer = new byte[bufferSize];

                record = new RandomAccessFile(tempFile, ACCESS);
                recordChannel = record.getChannel();
                MappedByteBuffer recordBuffer = recordChannel
                        .map(FileChannel.MapMode.READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);

                int startIndex = i * EACH_RECORD_SIZE;

                long start = recordBuffer.getLong(startIndex);
//                long end = recordBuffer.getLong(startIndex + 8);

                long totalSize = recordBuffer.getLong(RECORD_FILE_TOTAL_SIZE - 8) + 1;
                status.setTotalSize(totalSize);

                save = new RandomAccessFile(saveFile, ACCESS);
                saveChannel = save.getChannel();

                inStream = response.byteStream();

                status.setState(DownloadTask.LOADING);
                upStatusAndNotify(repository, status);
                status.setDwonloadStartTime(target.getTask().getStartTime());
                while ((readLen = inStream.read(buffer)) != -1 && !target.isCancle() && !target.getTask().getErrorChuck()) {
                    MappedByteBuffer saveBuffer = saveChannel.map(FileChannel.MapMode.READ_WRITE, start, readLen);
                    start += readLen;
                    saveBuffer.put(buffer, 0, readLen);
                    recordBuffer.putLong(startIndex, start);

                    status.setDownloadSize(totalSize - getResidue(recordBuffer));
                    //获得多个线程下载的总大小


                    status.setChuckDownloadSize(status.getDownloadSize() - target.getTask().getEntity().getDownloadSize());
                    //分块下载大（本次任务开始到当前时间为准的下载大小）5-2=chuckDownloadSize


                    //// FIXME: 2017/6/14 进度更新回调
                    if (status.isUpUi(target.getTask().lastTimeUpUI())) {
                        //速度计算
                        repository.update(status);
                        target.getTask().setLastTimeUpUI(System.currentTimeMillis());
                        DownLoadRxObserver.getDefault().post(status);
//                     Utils.log("【下载进度】:" + status.getFormatDownloadSize() + " 【下载分块:】:" + i);
                    }
                }
                //// FIXME: 2017/6/14 完成回调
                Utils.log("【rangs】:" + new FileHelper().readDownloadRange(tempFile, i).toString()+" isCancle:"+target.isCancle()+" Status:"+status.getState());
                if (target.isCancle()) {
                    int finish = target.getTask().chuckFinish();
                    Utils.log("【取消任务子线程】" + " chunkedFinish:" + finish);
                    if (target.isDelete()) {//取消并删除源文件
                        if (finish == 0) {
                            Utils.deleteFiles(saveFile,tempFile);
                            status.setState(DownloadTask.DELETE);
                            repository.delete(target.getTask().getId());
                            upStatusAndNotify(repository, status);
                            Utils.log("【任务取消并且删除源文件】");
                        }

                    } else {

                        if (finish == 0) {
                            status.setState(DownloadTask.PAUSE);
                            upStatusAndNotify(repository, status);
                            Utils.log("【任务暂停】");
                        }
                    }

                } else
                if (target.getTask().getErrorChuck()) {
                    Utils.log("【任务未完成】" + saveFile.getName());
                } else{
                    int finish = target.getTask().chuckFinish();
                    if (finish == 0 && !fileNotComplete(tempFile)) {
                        target.getTask().remove();
                        status.setDownloadSize(totalSize - getResidue(recordBuffer));
                        //获得多个线程下载的总大小
                        status.setChuckDownloadSize(status.getDownloadSize() - target.getTask().getEntity().getDownloadSize());
                        //分块下载大（本次任务开始到当前时间为准的下载大小）5-2=chuckDownloadSize
                        status.setState(DownloadTask.FINISH);
                        Utils.deleteFiles(tempFile);
                        Utils.log("【下载完成】" + saveFile.getName()+"  完成度："+status.getPercent());
                        upStatusAndNotify(repository,status);
                    } else {
                        Utils.log("【下载完成】" + " chunked:" + i);
                    }

                }

            } finally {
                closeQuietly(record);
                closeQuietly(recordChannel);
                closeQuietly(save);
                closeQuietly(saveChannel);
                closeQuietly(inStream);
                closeQuietly(response);
            }
        } catch (IOException e) {
            dispose.retry(e);
            Utils.log("【下载出错】:" + e.getMessage());
            //// FIXME: 2017/6/14 错误回调
        }
    }

    private void upRepositoryData(DownloadRepository repository, DownloadStatus status) {
        repository.update(status);
    }

    public void saveFile(DownloadRepository repository, DownloadErrorDispose dispose, DownloadTarget target, File saveFile,
                         ResponseBody body) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        DownloadStatus status = new DownloadStatus(target.getTask().getEntity());
        try {
            try {
                int readLen;
                int downloadSize = 0;
                byte[] buffer = new byte[8192];
                if (body == null) {
                    DownLoadRxObserver.getDefault().post(status.setState(DownloadTask.ERROR).setDescribe("下载失败"));
                    upRepositoryData(repository, status);
                    //// FIXME: 2017/6/14 如果没有返回类容提示下载失败
                    return;
                }
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(saveFile);

                long contentLength = body.contentLength();
                status.setTotalSize(contentLength);
                status.setState(DownloadTask.LOADING);
                DownLoadRxObserver.getDefault().post(status);
                status.setDwonloadStartTime(System.currentTimeMillis());
                while ((readLen = inputStream.read(buffer)) != -1 && !target.isCancle()) {
                    outputStream.write(buffer, 0, readLen);
                    downloadSize += readLen;
                    //总大小
                    status.setDownloadSize(downloadSize);


                    status.setChuckDownloadSize(status.getDownloadSize() - target.getTask().getEntity().getDownloadSize());
                    repository.update(status);
                    //// FIXME: 2017/6/14 进度更新回调
                    if (status.isUpUi(target.getTask().lastTimeUpUI())) {
                        target.getTask().setLastTimeUpUI(System.currentTimeMillis());
                        DownLoadRxObserver.getDefault().post(status);
//                        Utils.log("【下载进度】:" + status.getFormatDownloadSize());
                    }
                }

                outputStream.flush(); // This is important!!!
                //// FIXME: 2017/6/14 完成回调
                if (target.isCancle()) {
                    if (target.isDelete()) {//取消并删除源文件
                        status.setState(DownloadTask.DELETE);
                        Utils.deleteFiles(saveFile);
                        repository.delete(target.getTask().getId());
                        upStatusAndNotify(repository, status);
                        Utils.log("【任务取消并且删除源文件】");
                    } else {
                        status.setState(DownloadTask.PAUSE);
                        upStatusAndNotify(repository, status);
                        Utils.log("【任务暂停】");
                    }
                } else {
                    status.setDownloadSize(downloadSize);
                    target.getTask().remove();
                    status.setState(DownloadTask.FINISH);
                    upRepositoryData(repository, status);
                    Utils.log("【下载完成】" + saveFile.getName());
                    DownLoadRxObserver.getDefault().post(status);
                }
//                if (!target.isCancle() && !target.isDelete()){
//                    target.getTask().remove();
//                    status.setState(DownloadTask.FINISH);
//                    upRepositoryData(repository, status);
//                    Utils.log("【下载完成】" + saveFile.getName());
//                    DownLoadRxObserver.getDefault().post(status);
//                }

            } finally {
                closeQuietly(inputStream);
                closeQuietly(outputStream);
                closeQuietly(body);
            }
        } catch (IOException e) {
            dispose.retry(e);
            Utils.log("【下载出错】:" + e.getMessage());
        }

    }

    /**
     * 更新状态并通知UI
     * @param repository
     * @param status
     */
    private void upStatusAndNotify(DownloadRepository repository, DownloadStatus status) {
        upRepositoryData(repository, status);
        DownLoadRxObserver.getDefault().post(status);
    }

    public void prepareFile(File tempFile, File saveFile, long fileLength)
            throws IOException {
        Utils.log("【准备文件】");
        RandomAccessFile rFile = null;
        RandomAccessFile rRecord = null;
        FileChannel channel = null;
        try {
            rFile = new RandomAccessFile(saveFile, ACCESS);
            rFile.setLength(fileLength);//设置下载文件的长度

            rRecord = new RandomAccessFile(tempFile, ACCESS);
            rRecord.setLength(RECORD_FILE_TOTAL_SIZE); //设置指针记录文件的大小

            channel = rRecord.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);

            long start;
            long end;
            int eachSize = (int) (fileLength / maxThreads);

            for (int i = 0; i < maxThreads; i++) {
                if (i == maxThreads - 1) {
                    start = i * eachSize;
                    end = fileLength - 1;
                } else {
                    start = i * eachSize;
                    end = (i + 1) * eachSize - 1;
                }
                buffer.putLong(start);
                buffer.putLong(end);
            }
        } finally {
            closeQuietly(channel);
            closeQuietly(rRecord);
            closeQuietly(rFile);
        }
    }

    public void prepareFile(File saveFile, long fileLength) throws IOException {
        Utils.log("【准备文件】");
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(saveFile, ACCESS);
            if (fileLength != -1) {
                file.setLength(fileLength);//设置下载文件的长度
            } else {
                Utils.log("Chunked 下载, 无需设置文件大小");
            }
        } finally {
            closeQuietly(file);
        }
    }

    /**
     * 判断文件是否下载完成
     *
     * @param tempFile
     * @return
     * @throws IOException
     */
    public boolean fileNotComplete(File tempFile) throws IOException {
        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            record = new RandomAccessFile(tempFile, ACCESS);
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);

            long startByte;
            long endByte;
            for (int i = 0; i < maxThreads; i++) {
                startByte = buffer.getLong();
                endByte = buffer.getLong();
                if (startByte <= endByte) {
                    return true;
                }
            }
            return false;
        } finally {
            closeQuietly(channel);
            closeQuietly(record);
        }
    }

    /**
     * 临时文件是否损坏
     *
     * @param tempFile
     * @param fileLength
     * @return
     * @throws IOException
     */
    public boolean tempFileDamaged(File tempFile, long fileLength) throws IOException {

        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            record = new RandomAccessFile(tempFile, ACCESS);
            channel = record.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, RECORD_FILE_TOTAL_SIZE);
            long recordTotalSize = buffer.getLong(RECORD_FILE_TOTAL_SIZE - 8) + 1;
            return recordTotalSize != fileLength;
        } finally {
            closeQuietly(channel);
            closeQuietly(record);
        }
    }

    public DownloadRange readDownloadRange(File tempFile, int i) throws IOException {

        RandomAccessFile record = null;
        FileChannel channel = null;
        try {
            //读取分块位置
            record = new RandomAccessFile(tempFile, ACCESS);
            channel = record.getChannel();
            MappedByteBuffer buffer = channel
                    .map(FileChannel.MapMode.READ_WRITE, i * EACH_RECORD_SIZE, (i + 1) * EACH_RECORD_SIZE);
            long startByte = buffer.getLong();
            long endByte = buffer.getLong();
            return new DownloadRange(startByte, endByte);
        } finally {
            closeQuietly(channel);
            closeQuietly(record);
        }
    }

    /**
     * 还剩多少字节没有下载
     *
     * @param recordBuffer buffer
     * @return 剩余的字节
     */
    private long getResidue(MappedByteBuffer recordBuffer) {
        long residue = 0;
        for (int j = 0; j < maxThreads; j++) {
            long startTemp = recordBuffer.getLong(j * EACH_RECORD_SIZE);
            long endTemp = recordBuffer.getLong(j * EACH_RECORD_SIZE + 8);
            long temp = endTemp - startTemp + 1;
            residue += temp;
        }
        return residue;
    }
}
