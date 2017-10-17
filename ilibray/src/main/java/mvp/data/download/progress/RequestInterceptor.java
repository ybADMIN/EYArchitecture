package mvp.data.download.progress;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {

    private final CopyOnWriteArrayList<ProgressListener> listeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<RequestFilter> filters = new CopyOnWriteArrayList<>();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        ReqBodyLinstener linstener = new ReqBodyLinstener(listeners,filters);
        Request request = original.newBuilder()
                .method(original.method(),new ProgressRequestBody(original.body(),linstener))
                .build();
        linstener.setRequest(request);
        return chain.proceed(request);
    }

    public void addListener(ProgressListener listener) {
        addListener(listener, RequestFilters.any());
    }
    public void addListener(ProgressListener listener, RequestFilter filter) {
        listeners.add(listener);
        filters.add(filter);
    }

    public void removeListener(ProgressListener listener) {
        int index = listeners.indexOf(listener);
        listeners.remove(index);
        filters.remove(index);
    }
    private static  class  ReqBodyLinstener implements ProgressListener {
        private  Request mRequest;
        private final CopyOnWriteArrayList<ProgressListener> mListeners;
        private final CopyOnWriteArrayList<RequestFilter> mFilters;

        public ReqBodyLinstener(CopyOnWriteArrayList<ProgressListener> listeners,CopyOnWriteArrayList<RequestFilter>  filters) {
            this.mListeners=listeners;
            this.mFilters = filters;
        }

        public void setRequest(Request request) {
            mRequest = request;
        }

        @Override
        public void onProgress(long contentLength, long progress, boolean isDone) {
            for (int i = 0; i < mListeners.size(); i++) {
                RequestFilter filter = mFilters.get(i);
                if (filter.listensFor(mRequest)) {
                    ProgressListener listener = mListeners.get(i);
                    listener.onProgress(contentLength, progress, isDone);
                }
            }
        }

        @Override
        public void progressIndeterminate() {
            for (int i = 0; i < mListeners.size(); i++) {
                RequestFilter filter = mFilters.get(i);
                if (filter.listensFor(mRequest)) {
                    ProgressListener listener = mListeners.get(i);
                    listener.progressIndeterminate();
                }
            }
        }
    }
}
