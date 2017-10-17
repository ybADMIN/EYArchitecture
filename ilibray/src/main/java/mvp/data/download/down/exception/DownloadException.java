package mvp.data.download.down.exception;

/**
 * Created by ericYang on 2017/6/8.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class DownloadException extends Exception {
    public static String DOWNLOAD_DISK_NO_SPACE="下载失败，没有足够的磁盘空间";

    public DownloadException() {
    }

    public DownloadException(String message) {
        super(message);
    }
}
