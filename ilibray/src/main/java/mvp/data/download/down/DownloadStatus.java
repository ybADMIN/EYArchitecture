package mvp.data.download.down;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import mvp.data.download.down.entity.DownloadEntity;
import com.yb.ilibray.utils.io.FileUtils;

import java.text.NumberFormat;

/**
 * Created by ericYang on 2017/6/12.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class DownloadStatus implements Parcelable {
    private  String key;
    private  long id=-1;
    private String url;
    private String describe;
    private int state=DownloadTask.DEFAULT;
    private long totalSize;
    private long downloadSize;
    private long upDownloadSize;
    private static final int defProgressSize = 1000;
    private long dwonloadStartTime;
    private long mChuckDownloadSize;

    public ContentValues convertToContentValues() {
        ContentValues convalues = new ContentValues();
        convalues.put(DownloadEntity.Property.COLUMN_URL,url);
        convalues.put(DownloadEntity.Property.COLUMN_CONTEXTSIZE,totalSize);
        convalues.put(DownloadEntity.Property.COLUMN_DOWNLOADSIZE,downloadSize);
        convalues.put(DownloadEntity.Property.COLUMN_STATUS,state);
        convalues.put(DownloadEntity.Property.COLUMN_KEY,key);
        return convalues;
    }

    public void cursorToTask(Cursor cr){
        id = cr.getInt(
                cr.getColumnIndex(DownloadEntity.Property.COLUMN_ID));
        downloadSize = cr.getLong(
                cr.getColumnIndex(DownloadEntity.Property.COLUMN_DOWNLOADSIZE));
        url = cr.getString(
                cr.getColumnIndex(DownloadEntity.Property.COLUMN_URL));
        key = cr.getString(
                cr.getColumnIndex(DownloadEntity.Property.COLUMN_KEY));
    }

    public long getId() {
        return id;
    }

    public DownloadStatus setId(long id) {
        this.id = id;
        return this;
    }

    public String getDescribe() {
        return describe;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public DownloadStatus setDescribe(String describe) {
        this.describe = describe;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public DownloadStatus setUrl(String url) {
        this.url = url;
        return this;
    }
    @DownloadTask.Tpye
    public int getState() {
        return state;
    }

    public DownloadStatus setState(@DownloadTask.Tpye int state) {
        this.state = state;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeLong(this.id);
        dest.writeString(this.key);
        dest.writeInt(this.state);
    }

    public DownloadStatus() {
    }
    public DownloadStatus(DownloadEntity entity) {
        this.downloadSize = entity.getDownloadSize();
        this.totalSize = entity.getContextSize();
        this.key=entity.getKey();
        this.id=entity.getId();
        this.url = entity.getUrl();
        this.state = entity.getStatus();
    }
    protected DownloadStatus(Parcel in) {
        this.url = in.readString();
        this.key = in.readString();
        this.state = in.readInt();
        this.id = in.readLong();
    }

    public static final Creator<DownloadStatus> CREATOR = new Creator<DownloadStatus>() {
        @Override
        public DownloadStatus createFromParcel(Parcel source) {
            return new DownloadStatus(source);
        }

        @Override
        public DownloadStatus[] newArray(int size) {
            return new DownloadStatus[size];
        }
    };

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public void setDwonloadStartTime(long dwonloadStartTime) {
        this.dwonloadStartTime = dwonloadStartTime;
    }

    public long getDwonloadStartTime() {
        return dwonloadStartTime;
    }

    public String getDownloadSpeed() {
        long usedSecond = ((System.currentTimeMillis() - dwonloadStartTime))/1000;
        long speed=0;
        if (usedSecond>0 && mChuckDownloadSize>0){
            speed = mChuckDownloadSize / usedSecond;
        }
        return FileUtils.byteCountToDisplaySize(speed)+"/s";
    }
    /**
     * 获得格式化的总Size
     *
     * @return example: 2KB , 10MB
     */
    public String getFormatTotalSize() {
        return FileUtils.formatSize(totalSize);
    }

    public String getFormatDownloadSize() {
        return FileUtils.formatSize(downloadSize);
    }

    /**
     * 获得格式化的状态字符串
     *
     * @return example: 2MB/36MB
     */
    public String getFormatStatusString() {
        return getFormatDownloadSize() + "/" + getFormatTotalSize();
    }

    /**
     * 获得下载的百分比, 保留两位小数
     *
     * @return example: 5.25%
     */
    public String getPercent() {
        String percent;
        Double result;
        if (totalSize == 0L) {
            result = 0.0;
        } else {
            result = downloadSize * 1.0 / totalSize;
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(2);//控制保留小数点后几位，2：表示保留2位小数点
        percent = nf.format(result);
        return percent;
    }

    /**
     * 最少500毫秒更新一次
     * @param lastTime
     * @return
     */
    public boolean isUpUi(long lastTime) {
        return System.currentTimeMillis()-lastTime>500;
    }


    /**
     * 获得下载的百分比数值
     *
     * @return example: 5%  will return 5, 10% will return 10.
     */
    public double getPercentNumber() {
        double result;
        if (totalSize == 0L) {
            result = 0.0;
        } else {
            result = downloadSize * 1.0 / totalSize;
        }
        return result * 100.0;
    }

    public double getPercentNumber(int progressSize) {
        double result;
        if (totalSize == 0L) {
            result = 0.0;
        } else {
            result = downloadSize * 1.0 / totalSize;
        }
        return result * progressSize;
    }

    public void setChuckDownloadSize(long chuckDownloadSize) {
        mChuckDownloadSize = chuckDownloadSize;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DownloadStatus{");
        sb.append("key='").append(key).append('\'');
        sb.append(", id=").append(id);
        sb.append(", url='").append(url).append('\'');
        sb.append(", describe='").append(describe).append('\'');
        sb.append(", state=").append(state);
        sb.append(", totalSize=").append(totalSize);
        sb.append(", downloadSize=").append(downloadSize);
        sb.append('}');
        return sb.toString();
    }
}
