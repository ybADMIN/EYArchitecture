package mvp.data.net.converter;//package com.huanmedia.yourchum.common.http.converter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 用于返回String的converterFactory
 */
public  class StringConverterFactory extends Converter.Factory {

  public static final StringConverterFactory INSTANCE = new StringConverterFactory();

  public static StringConverterFactory create() {
    return INSTANCE;
  }

  // 我们只关实现从ResponseBody 到 String 的转换，所以其它方法可不覆盖
  @Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
    if (String.class.equals(type)) {
      return StringConverter.INSTANCE;
    }
    //其它类型我们不处理，返回null就行
    return null;
  }

  public static class StringConverter implements Converter<ResponseBody, String> {

    public static final StringConverter INSTANCE = new StringConverter();

    @Override
    public String convert(ResponseBody value) throws IOException {
      return value.string();
    }
  }
}