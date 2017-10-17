package mvp.data.download.down;

/**
 * Created by ericYang on 2017/6/20.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public interface DownloadErrorDispose {
    void retry(Exception e);
    void connectionSuccess();
}
