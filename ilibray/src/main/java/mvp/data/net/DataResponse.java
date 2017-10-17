package mvp.data.net;

import com.google.gson.annotations.SerializedName;

//DataResponse.java
public class DataResponse<T> {
    @SerializedName("code")
    private int mCode;
    @SerializedName("mesg")
    private String mMessage;
    @SerializedName("result")
    private T mResult;
    public int getCode() {
        return mCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public T getResult() {
        return mResult;
    }

    public void setCode(int code) {
        mCode = code;
    }


    /**
     * API是否请求失败
     *
     * @return 失败返回true, 成功返回false
     */
    public boolean isCodeInvalid() {
        return mCode != 1000;
    }
}