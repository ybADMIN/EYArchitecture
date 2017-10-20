package com.yb.btcinfo;

import org.junit.Test;

import java.util.List;

import mvp.data.download.down.entity.DownloadEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void verify_behaviour(){
        //模拟创建一个List对象
        List mock = mock(List.class);
        //使用mock的对象
        mock.add(1);
        mock.clear();
        //验证add(1)和clear()行为是否发生
        verify(mock).add(1);
        verify(mock).clear();
    }
    @Test
    public void mockTest(){
        DownloadEntity downloadEntity = mock(DownloadEntity.class);

        String url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";
         downloadEntity .setMethod("GET");
         downloadEntity.setUrl(url);

        verify(downloadEntity).setUrl(url);
        verify(downloadEntity).setMethod("GET");
        try {
            Thread.sleep(2000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}