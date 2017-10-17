package com.yb.btcinfo.repository.entity;

import java.io.Serializable;

/**
 * Created by ericYang on 2017/8/24.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class IndexEntity implements Serializable {

    /**
     * id : 48
     * platform : jubi
     * title : 关于聚币网区块链资产交易风险的再次提醒
     * url : https: //www.jubi.com/gonggao/2798.html
     * time : 2017/8/2316: 13: 05
     */

    private int id;
    private String platform;
    private String title;
    private String url;
    private String time;
    private String logourl;

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IndexEntity{");
        sb.append("id=").append(id);
        sb.append(", platform='").append(platform).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append(", logourl='").append(logourl).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
