package mvp.data.download.down.entity;

/**
 * Author: Season(ssseasonnn@gmail.com)
 * Date: 2016/10/21
 * Time: 15:28
 * Download Range
 */
public class DownloadRange {
    public long start;
    public long end;
    public long size;

    public DownloadRange(long start, long end) {
        this.start = start;
        this.end = end;
        this.size = end - start + 1;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DownloadRange{");
        sb.append("start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", size=").append(size);
        sb.append('}');
        return sb.toString();
    }

    public boolean legal() {
        return start <= end;
    }
}
