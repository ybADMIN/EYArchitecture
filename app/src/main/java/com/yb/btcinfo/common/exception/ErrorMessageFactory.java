/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
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
package com.yb.btcinfo.common.exception;

import android.content.Context;

import com.yb.btcinfo.R;

import java.net.SocketTimeoutException;

import io.reactivex.exceptions.CompositeException;
import mvp.data.net.ApiException;
import retrofit2.HttpException;

/**
 * Factory used to create error messages from an Exception as a condition.
 */
public class ErrorMessageFactory {

  private ErrorMessageFactory() {
    //empty
  }
  private static Throwable compositeExceptionToRealException(Throwable throwable){
    if (throwable instanceof CompositeException){
      if (throwable.getCause()!=null){
        if (!(throwable.getCause() instanceof CompositeException)){
          if (throwable.getCause().getCause()!=null){
            return throwable.getCause().getCause();
          }else
          return throwable.getCause();
        }else {
          return compositeExceptionToRealException(throwable.getCause());
        }
      }else {
        return new RuntimeException("未知异常");
      }
    }
    return throwable;
  }
  /**
   * Creates a String representing an error message.
   *
   * @param context Context needed to retrieve string resources.
   * @param throwable An exception used as a condition to retrieve the correct error message.
   * @return {@link String} an error message.
   */
  public static String create(Context context, Throwable throwable) {
    throwable =compositeExceptionToRealException(throwable);
    String message = context.getString(R.string.exception_message_generic);
    if (throwable instanceof ApiException)
    {
      message= throwable.getMessage();
    }
    else if (throwable instanceof HttpException){
      message=HttpStatusCodes.getCodesMap().get(((HttpException)throwable).code());
    }
    else if (throwable instanceof SocketTimeoutException){
      message=  context.getString(R.string.exception_message_network_connError);
    }
    else {
      message= throwable.getMessage()==null ?message:throwable.getMessage();
    }
    return message;
  }
}
