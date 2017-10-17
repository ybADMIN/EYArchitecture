package mvp.data.store.glide;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.Excludes;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpLibraryGlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.yb.ilibray.BuildConfig;
import com.yb.ilibray.utils.AppUtil;

import mvp.data.store.FilePathManager;

/**
 * Created by ericYang on 2017/5/25.
 * Email:eric.yang@huanmedia.com
 * what?
 */
@Excludes({OkHttpLibraryGlideModule.class})
@GlideModule
public final class MyAppGlideModule extends AppGlideModule {
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

//    @Override
//    public void registerComponents(Context context, Registry registry) {
//    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        MemorySizeCalculator calculator = new MemorySizeCalculator.Builder(context)
                .setMemoryCacheScreens(2)
                .build();
        builder.setDiskCache(new DiskLruCacheFactory(FilePathManager.getImageCacheDir(context).getAbsolutePath(),DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE))
                .setDefaultRequestOptions(
                        new RequestOptions()
                                .format(DecodeFormat.PREFER_RGB_565)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .signature(new ObjectKey(AppUtil.getPackageInfo(context).versionCode + AppUtil.getPackageInfo(context).versionName)))
                .setMemoryCache(new LruResourceCache(calculator.getMemoryCacheSize()));
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(Log.VERBOSE);
        }
        super.applyOptions(context, builder);
    }
}
