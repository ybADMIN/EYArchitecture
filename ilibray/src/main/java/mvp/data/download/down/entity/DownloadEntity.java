package mvp.data.download.down.entity;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import mvp.data.download.down.DownloadTask;
import com.yb.ilibray.utils.data.assist.Check;
import com.yb.ilibray.utils.data.cipher.MD5Cipher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import okhttp3.Request;

/**
 * Created by ericYang on 2017/6/12.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class DownloadEntity {

    private static final Gson mGson=new Gson();

    public ContentValues convertToContentValues() {
        ContentValues convalues = new ContentValues();
        convalues.put(Property.COLUMN_NAME,fixName);
        convalues.put(Property.COLUMN_URL,getUrl());
        convalues.put(Property.COLUMN_METHOD,getMethod());
        convalues.put(Property.COLUMN_CONTEXTSIZE,getContextSize());
        convalues.put(Property.COLUMN_DOWNLOADSIZE,getDownloadSize());
        convalues.put(Property.COLUMN_CREATETIME,createTime);
        convalues.put(Property.COLUMN_STATUS,getStatus());
        convalues.put(Property.COLUMN_SAVE_ADDRESS,save_address);
        convalues.put(Property.COLUMN_EXTENSION,extension);
        convalues.put(Property.COLUMN_KEY,getKey());
        convalues.put(Property.COLUMN_CHUNK,chunk?1:0);
        convalues.put(Property.COLUMN_LASTMODIFY,lastModify);
        String jheades=null;
        if (heades!=null){
            jheades = mGson.toJson(heades);
        }
        convalues.put(Property.COLUMN_HEADES,jheades);
        String jparms=null;
        if (parms!=null){
            jparms = mGson.toJson(parms);
        }
        convalues.put(Property.COLUMN_PARMS,jparms);
        return convalues;
    }

    public void cursorToTask(Cursor cr){
        id = cr.getInt(
                cr.getColumnIndex(Property.COLUMN_ID));
        fixName = cr.getString(
                cr.getColumnIndex(Property.COLUMN_NAME));
        contextSize = cr.getLong(
                cr.getColumnIndex(Property.COLUMN_CONTEXTSIZE));
        downloadSize = cr.getLong(
                cr.getColumnIndex(Property.COLUMN_DOWNLOADSIZE));
        createTime = cr.getLong(
                cr.getColumnIndex(Property.COLUMN_CREATETIME));
        status = cr.getInt(
                cr.getColumnIndex(Property.COLUMN_STATUS));
        method = cr.getString(
                cr.getColumnIndex(Property.COLUMN_METHOD));
        url = cr.getString(
                cr.getColumnIndex(Property.COLUMN_URL));
        key = cr.getString(
                cr.getColumnIndex(Property.COLUMN_KEY));
        chunk = cr.getInt(
                cr.getColumnIndex(Property.COLUMN_CHUNK))==1;
        save_address = cr.getString(
                cr.getColumnIndex(Property.COLUMN_SAVE_ADDRESS));
        extension = cr.getString(
                cr.getColumnIndex(Property.COLUMN_EXTENSION));
        lastModify = cr.getString(
                cr.getColumnIndex(Property.COLUMN_LASTMODIFY));
        String headestr = cr.getString(
                cr.getColumnIndex(Property.COLUMN_HEADES));
        if (!Check.isEmpty(headestr))
            heades = mGson.fromJson(headestr,new TypeToken<Map<String,String>>() { }.getType());
        String parmstr = cr.getString(
                cr.getColumnIndex(Property.COLUMN_PARMS));
        if (!Check.isEmpty(parmstr))
            parms = mGson.fromJson(parmstr,new TypeToken<Map<String,String>>() { }.getType());
    }

    public String strParms() {
        String params = getParams();
        if (params!=null)
            return params;
        return "";
    }

    public String strHeaders() {
        if (heades==null)return null;
        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        for (String key : heades.keySet()) {
            if (pos > 0) {
                tempParams.append("\n\r");
            }
            try {
                tempParams.append(String.format("%s:%s", key, URLEncoder.encode(parms.get(key), "utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pos++;
        }
        return tempParams.toString();
    }

    public Request.Builder bulideHeader() {
        Request.Builder builder=new Request.Builder();
        if (heades==null)return builder;
        Set<Map.Entry<String, String>> entrys = heades.entrySet();
        Iterator<Map.Entry<String,String>> iterator=entrys.iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> header = iterator.next();
            builder.header(header.getKey(),header.getValue());
        }
        return builder;
    }
    public String getParams()  {
        if (parms==null)return null;
        StringBuilder tempParams = new StringBuilder();
        int pos = 0;
        for (String key : parms.keySet()) {
            if (pos > 0) {
                tempParams.append("&");
            }
            try {
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(parms.get(key), "utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            pos++;
        }
        return tempParams.toString();
    }



    public final static class Property{
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_CHUNK = "chunk";
        public static String TABLENAME="downloadconfig";
        public static String COLUMN_ID="_id";
        public static String COLUMN_URL="url";
        public static String COLUMN_METHOD="method";
        public static String COLUMN_NAME ="name";
        public static String COLUMN_CONTEXTSIZE="contextSize";
        public static String COLUMN_DOWNLOADSIZE="downloadSize";
        public static String COLUMN_CREATETIME="createTime";
        public static String COLUMN_STATUS="status";
        public static String COLUMN_SAVE_ADDRESS="save_address";
        public static String COLUMN_EXTENSION="extension";
        public static String COLUMN_PARMS="parms";
        public static String COLUMN_HEADES="heades";
        public static String COLUMN_LASTMODIFY="lastModify";
    }
    private String lastModify;
    private long id=-1;
    private String url;
    private Map<String, String> parms;
    private Map<String, String> heades;
    private String method="GET";
    private String fixName;
    private String save_address;
    private String extension;
    private long contextSize;
    private long downloadSize;
    private long createTime;
    private int status;
    private String key;
    private boolean chunk;
    /**
     * 获取下载的唯一标识
     * @return
     */
    public  String getKey(){
        if (key==null){
            createTime =System.currentTimeMillis();
            if (getUrl()!=null)
              return new String(
                      new MD5Cipher().encrypt(
                      (
                              getUrl()
                              +strParms()
                              +strHeaders()

                      ).getBytes()));
            else
                return "";
        }else {
            return key;
        }
    }
    public String lastModify() {
        return lastModify;
    }

    public DownloadEntity setLastModify(String lastModify) {
        this.lastModify = lastModify;
        return this;
    }

    public long getId() {
        return id;
    }

    public boolean isChunk() {
        return chunk;
    }

    public String getUrl() {
        return url;
    }


    public Map<String, String> getParms() {
        return parms;
    }


    public Map<String, String> getHeades() {
        return heades;
    }


    public String getMethod() {
        return method;
    }


    public String getFixName() {
        return fixName;
    }

    public String getSave_address() {
        return save_address;
    }


    public String getExtension() {
        return extension;
    }


    public long getContextSize() {
        return contextSize;
    }


    public long getDownloadSize() {
        return downloadSize;
    }


    public long getCreateTime() {
        return createTime;
    }


    public int getStatus() {
        return status;
    }

    public DownloadEntity setId(long id) {
        this.id = id;
        return this;
    }

    public DownloadEntity setChunk(boolean chunk) {
        this.chunk = chunk;
        return this;
    }

    public DownloadEntity setUrl(String url) {
        this.url = url;
        return this;
    }

    public DownloadEntity setParms(Map<String, String> parms) {
        this.parms = parms;
        return this;
    }

    public DownloadEntity setHeades(Map<String, String> heades) {
        this.heades = heades;
        return this;
    }

    public DownloadEntity setMethod(String method) {
        this.method = method;
        return this;
    }

    public DownloadEntity setFixName(String fixName) {
        this.fixName = fixName;
        return this;
    }

    public DownloadEntity setSave_address(String save_address) {
        this.save_address = save_address;
        return this;
    }

    public DownloadEntity setExtension(String extension) {
        this.extension = extension;
        return this;
    }

    public DownloadEntity setContextSize(long contextSize) {
        this.contextSize = contextSize;
        return this;
    }

    public DownloadEntity setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
        return this;
    }

    public DownloadEntity setCreateTime(long createTime) {
        this.createTime = createTime;
        return this;
    }

    public DownloadEntity setStatus(@DownloadTask.Tpye int status) {
        this.status = status;
        return this;
    }
}
