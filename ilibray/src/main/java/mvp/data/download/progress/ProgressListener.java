package mvp.data.download.progress;

public interface ProgressListener {
    void progressIndeterminate();
    void onProgress(long contentLength, long downloadLength,boolean isDone);
}
