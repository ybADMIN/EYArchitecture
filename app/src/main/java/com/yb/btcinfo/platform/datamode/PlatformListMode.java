package com.yb.btcinfo.platform.datamode;

/**
 * Created by ericYang on 2017/9/11.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class PlatformListMode {

    /**
     * id : 287
     * platform : 比特时代
     * title : 比特时代助学公益项目进展情况
     * url : http://www.btc38.com/news/2017/9/15351.html
     * logourl : http://47.94.92.29/content/logo/bite.png
     * time : 2017-09-11 09:58:12
     */

    private int id;
    private String platform;
    private String title;
    private String url;
    private String logourl;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
