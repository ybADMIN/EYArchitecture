package mvp.data.download.down;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import mvp.data.download.down.entity.ChuckRelevance;
import com.yb.ilibray.utils.data.assist.Check;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.internal.http.HttpHeaders;


/**
 * Created by ericYang on 2017/6/7.
 * Email:eric.yang@huanmedia.com
 * what?
 */

 class Utils {

    private static boolean DEBUG = true;
    public static final String TAG = "DownloadManager";
    public static final String TMP_SUFFIX = ".tmp";  //temp file
    public static final String CACHE = ".cache";    //cache directory
    public static void setDebug(boolean flag) {
        DEBUG = flag;
    }

    public static boolean empty(String string) {
        return TextUtils.isEmpty(string);
    }

    public static void log(String message) {
        if (empty(message)) return;
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    public static void log(String message, Object... args) {
        log(String.format(Locale.getDefault(), message, args));
    }

    public static void log(Throwable throwable) {
        Log.w(TAG, throwable);
    }

    public static String formatStr(String str, Object... args) {
        return String.format(Locale.getDefault(), str, args);
    }

    /**
     * convert long to GMT string
     *
     * @param lastModify long
     * @return String
     */
    public static String longToGMT(long lastModify) {
        Date d = new Date(lastModify);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(d);
    }

    /**
     * convert GMT string to long
     *
     * @param GMT String
     * @return long
     * @throws ParseException
     */
    public static long GMTToLong(String GMT) throws ParseException {
        if (GMT == null || "".equals(GMT)) {
            return new Date().getTime();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = sdf.parse(GMT);
        return date.getTime();
    }
    public static String lastModify(Response response) {
        return response.headers().get("Last-Modified");
    }

    public static long contentLength(Response response) {
        return HttpHeaders.contentLength(response.headers());
    }

    public static boolean notSupportRange(Response response) {
        return (TextUtils.isEmpty(contentRange(response)) && !TextUtils.equals(acceptRanges(response), "bytes")) || contentLength(response) == -1 ;
    }
    public static String matcherName(String url) {
        if (Check.isEmpty(url))
            return "";

        String path = HttpUrl.parse(url).url().getPath();

        Pattern pat=Pattern.compile("[^\\ /:*？\"<>|]+[\\.][\\w]+");//匹配文件名
        Matcher mc=pat.matcher(path);//条件匹配
        String substring = "";
        while(mc.find()){
            substring = mc.group();//截取文件名
        }
        if (Check.isEmpty(substring)){
            String[] name = path.split("/");
            if (name.length>0){
                substring=name[name.length-1];
            }
        }
        return substring;
    }

    public static String contentDisposition(Response response) {
        String disposition = response.headers().get("Content-Disposition");
        if (Check.isEmpty(disposition)) {
            return "";
        }
        Matcher m = Pattern.compile(".*filename=(.*)").matcher(disposition.toLowerCase());
        if (m.find()) {
            return m.group(1);
        } else {
            return "";
        }
    }
    public static  String matcherExtension(String str){
        String extensionstr ="";
        Pattern extension=Pattern.compile("[\\.][\\w]+");//后缀
        Matcher exmc= extension.matcher(str);
        while (exmc.find()){
            extensionstr =exmc.group();
        }
        return extensionstr;
    }

    /**
     * return file paths
     *
     * @param saveName saveName
     * @param savePath savePath
     * @return filePath, tempPath, lmfPath
     */
    private static String[] getPaths(String saveName, String savePath) {
        String cachePath = TextUtils.concat(savePath, File.separator, CACHE).toString();
        String filePath = TextUtils.concat(savePath, File.separator, saveName).toString();
        String tempPath = TextUtils.concat(cachePath, File.separator, saveName, TMP_SUFFIX).toString();
        return new String[]{filePath, tempPath};
    }

    /**
     * return files
     *
     * @param saveName saveName
     * @param savePath savePath
     * @return file, tempFile, lmfFile
     */
    public static ChuckRelevance getFiles(String saveName, String savePath) {
        String[] paths = getPaths(saveName, savePath);
        return new ChuckRelevance(new File(paths[0]), new File(paths[1]));
    }

    /**
     * create dirs with params path
     *
     * @param paths paths
     */
    public static void mkdirs(String... paths) {
        for (String each : paths) {
            File file = new File(each);
            if (file.exists() && file.isDirectory()) {
                log(Constant.DIR_EXISTS_HINT, each);
            } else {
                log(Constant.DIR_NOT_EXISTS_HINT, each);
                boolean flag = file.mkdirs();
                if (flag) {
                    log(Constant.DIR_CREATE_SUCCESS, each);
                } else {
                    log(Constant.DIR_CREATE_FAILED, each);
                }
            }
        }
    }

    /**
     * delete files
     *
     * @param files files
     */
    public static void deleteFiles(File... files) {
        for (File each : files) {
            if (each.exists()) {
                boolean flag = each.delete();
                if (flag) {
                    log(String.format(Locale.getDefault(), Constant.FILE_DELETE_SUCCESS, each.getName()));
                } else {
                    log(String.format(Locale.getDefault(), Constant.FILE_DELETE_FAILED, each.getName()));
                }
            }
        }
    }

    public static String contentRange(Response response) {
        return response.headers().get("Content-Range");
    }

    private static String acceptRanges(Response response) {
        return response.headers().get("Accept-Ranges");
    }

//    public static void checkSpace(long loaded, long total) throws DownloadException {
//        long storage = getAvailableStorage();
//        Utils.log("need = " + (total - loaded) + " = " + total + " - " + loaded + "\nspace = " + storage);
//        if (total - loaded > storage) {
//            throw new DownloadException(DownloadException.DOWNLOAD_DISK_NO_SPACE);
//        }
//    }
    public static long getAvailableStorage() {
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
            } else {
                return (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
            }
        } catch (RuntimeException ex) {
            return 0;
        }
    }

}
