/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package mvp.data.store;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 *
 * 存储工具类
 *
 * @author yb (yb498869020[at]hotmail[dot]com)
 * @since 1.0.0
 */
public final class StorageUtil {

	private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
	private static final String INDIVIDUAL_DIR_NAME = "iLibray";

	private StorageUtil() {
	}

	/**
	 * 缓存目录默认路径是：/Android/data/"packagename"/cache
	 * 如果不能获取到外部目录则使用app内部缓存目录context.getCacheDir()
	 * 如果不能获取到内部缓存目录则使用"/data/data/" + context.getPackageName() + "/cache/"
	 */
	protected static File getCacheDirectory(Context context, boolean preferExternal) {
		File appCacheDir = null;
		String externalStorageState;
		try {
			externalStorageState = Environment.getExternalStorageState();
		} catch (NullPointerException e) { // (sh)it happens (Issue #660)
			externalStorageState = "";
		}
		if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
			appCacheDir = getExternalCacheDir(context);
		}
		if (appCacheDir == null) {
			appCacheDir = context.getCacheDir();
		}
		if (appCacheDir == null) {
			String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
			Logger.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
			appCacheDir = new File(cacheDirPath);
		}
		return appCacheDir;
	}

	/**
	 *创建默认缓存路径下的默认缓存目录YourChum 路径：/Android/data/"packagename"/cache/{@value INDIVIDUAL_DIR_NAME}
	 */
	protected static File getIndividualCacheDirectory(Context context) {
		return getIndividualCacheDirectory(context, INDIVIDUAL_DIR_NAME);
	}

	/**
	 * 在默认缓存目录下创建目录：（eg:/Android/data/"packagename"/"cache/cacheDir"）
	 * 如果不存在SD卡则默认会使用eg:"/data/data/" + context.getPackageName() + "/cache/"cacheDir
	 */
	protected static File getIndividualCacheDirectory(Context context, String cacheDir) {
		File appCacheDir = getCacheDirectory(context,true);
		File individualCacheDir = new File(appCacheDir, cacheDir);
		if (!individualCacheDir.exists()) {
			if (!individualCacheDir.mkdirs()) {
				individualCacheDir = appCacheDir;
			}
		}
		return individualCacheDir;
	}


	/**
	 * @param preferExternal 如果true表示定义一个外部存储目录在根目录下  否则 - Android 返回android默认应用程序缓存目录：context.getCacheDir()
	 * @return Cache {@link File directory}
	 */
	protected static File getOwnCacheDirectory(Context context, String cacheDir, boolean preferExternal) {
		File appCacheDir = null;
		if (preferExternal && MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
			appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
			if (appCacheDir!=null && !appCacheDir.exists()){
				appCacheDir.mkdirs();
			}
			return appCacheDir;
		}
		if (!preferExternal) {
			appCacheDir = context.getCacheDir();
		}
		return appCacheDir;
	}

	/**
	 * @param context
	 * @return 外部缓存目录：/Android/data/"packetname"/cache 如果没有创建成功返回空
	 */
	private static File getExternalCacheDir(Context context) {
		File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
		File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
		if (!appCacheDir.exists()) {
			if (!appCacheDir.mkdirs()) {
				Logger.w("Unable to create external cache directory",appCacheDir.getAbsoluteFile().toString());
				return null;
			}
			try {
				new File(appCacheDir, ".nomedia").createNewFile();
			} catch (IOException e) {
				Logger.i("Can't create \".nomedia\" file in application external cache directory",e.getMessage());
			}
		}
		return appCacheDir;
	}




	/**
	 * 存储卡权限判断
	 * @param context
	 * @return
	 */
	private static boolean hasExternalStoragePermission(Context context) {
		int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
		return perm == PackageManager.PERMISSION_GRANTED;
	}
}
