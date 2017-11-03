package com.yb.btcinfo.main.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ericYang on 2017/8/28.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class PlatformModel implements Parcelable {

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
        final StringBuffer sb = new StringBuffer("PlatformModel{");
        sb.append("name='").append(name).append('\'');
        sb.append(", logourl='").append(logourl).append('\'');
        sb.append(", id=").append(id);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.logourl);
        dest.writeInt(this.id);
    }

    public PlatformModel() {
    }

    protected PlatformModel(Parcel in) {
        this.name = in.readString();
        this.logourl = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<PlatformModel> CREATOR = new Creator<PlatformModel>() {
        @Override
        public PlatformModel createFromParcel(Parcel source) {
            return new PlatformModel(source);
        }

        @Override
        public PlatformModel[] newArray(int size) {
            return new PlatformModel[size];
        }
    };
}
