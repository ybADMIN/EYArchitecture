package com.yb.ilibray.utils.data.cipher;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *加密字节数据
 * @author MaTianyu
 * @date 14-7-31
 */
public class MD5Cipher extends Cipher {
    private Cipher cipher;

    public MD5Cipher() {
    }

    public MD5Cipher(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public byte[] decrypt(byte[] res) {
        return res;
    }

    @Override
    public byte[] encrypt(byte[] res) {
        if(cipher != null) res = cipher.encrypt(res);
        return Md5(res);
    }

    public byte[] Md5(byte[] res) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(res);
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if(i<0) i+= 256;
                if(i<16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return  buf.toString().getBytes();//32位的加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }
}
