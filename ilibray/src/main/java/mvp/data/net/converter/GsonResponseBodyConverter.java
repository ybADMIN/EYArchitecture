/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mvp.data.net.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.orhanobut.logger.Logger;
import com.yb.ilibray.utils.io.Charsets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import mvp.data.net.ApiException;
import mvp.data.net.DataResponse;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final Gson gson;
  private final TypeAdapter<T> adapter;

  GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
    this.gson = gson;
    this.adapter = adapter;
  }

  @SuppressWarnings("unchecked")
  @Override public T convert(ResponseBody value) throws IOException {
    long time =System.currentTimeMillis();
    String response = value.string();
    MediaType contentType = value.contentType();
    Charset charset = contentType != null ? contentType.charset(Charsets.UTF_8) : Charsets.UTF_8;
    InputStream inputStream = new ByteArrayInputStream(response.getBytes());
    Reader reader = new InputStreamReader(inputStream, charset);
    JsonReader jsonReader = gson.newJsonReader(reader);
    try {
      T object= adapter.read(jsonReader);
      if (object instanceof DataResponse){
        DataResponse httpStatus = (DataResponse) object;
        if (httpStatus.isCodeInvalid()) {
          value.close();
          throw new ApiException(httpStatus.getCode(), httpStatus.getMessage());
        }
      }
      return object;
    }
    finally {
      Logger.d("使用时间: %d",System.currentTimeMillis()-time);
      value.close();
    }
  }
}
