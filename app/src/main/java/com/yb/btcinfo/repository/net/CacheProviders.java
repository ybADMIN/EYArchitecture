package com.yb.btcinfo.repository.net;

import android.content.Context;

import com.yb.btcinfo.repository.entity.IndexEntity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EncryptKey;
import io.rx_cache2.LifeCache;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import mvp.data.store.FilePathManager;

/**
 * Created by ericYang on 2017/5/25.
 * Email:eric.yang@huanmedia.com
 * what?
 */
@EncryptKey("A@B$$5aa")
public interface CacheProviders {
    class Factory {
        private Factory() {
        }

        public static CacheProviders createProviders(Context context) {

            return new RxCache.Builder()
                    .setMaxMBPersistenceCache(50)//Max Cache 50 MB
                    .persistence(
                            FilePathManager.getHttpCacheDir(context)
                            , new GsonSpeaker())
                    .using(CacheProviders.class);
        }
    }
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<List<IndexEntity>> getIndexNews(Observable<List<IndexEntity>> observable, DynamicKey number);
}
