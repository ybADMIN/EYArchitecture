package com.yb.btcinfo.repository.entity;

import java.io.Serializable;

/**
 * Created by ericYang on 2017/8/28.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class PlatformEntity  implements Serializable {

    /**
     * name : btc38
     * id : 1
     */

    private String name;
    private String logourl;
    private int id;

    public String getLogourl() {
        return logourl;
    }

    public void setLogourl(String logourl) {
        this.logourl = logourl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PlatformEntity{");
        sb.append("name='").append(name).append('\'');
        sb.append(", logourl='").append(logourl).append('\'');
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }
}
