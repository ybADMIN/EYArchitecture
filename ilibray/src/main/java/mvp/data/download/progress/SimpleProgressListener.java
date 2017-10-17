package mvp.data.download.progress;

public abstract class SimpleProgressListener implements ProgressListener {

    @Override
    public void onProgress(long contentLength, long downloadLength,boolean isDone){
        // no-op
    }

    @Override
    public void progressIndeterminate() {
        // no-op
    }
}
