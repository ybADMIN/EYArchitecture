package mvp.data.download.progress;

import okhttp3.Request;

public interface RequestFilter {
    boolean listensFor(Request request);
}
