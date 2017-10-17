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
package mvp.data.download.down;

import android.support.annotation.NonNull;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Decorated {@link ThreadPoolExecutor}
 */
public class DownLoadExcutor{
  private final ThreadPoolExecutor threadPoolExecutor;
  private static final   int taskThreadSize = 3;//每个任务最大线程数
  private static final   int threadIncreaseSize = 2;//队列满后，增加倍数
  private static  int maxTaskSize = 5;//最大任务数
  private static  int corePoolSize = taskThreadSize * maxTaskSize;//初始线程数量
  private static  int maximumPoolSize = threadIncreaseSize * corePoolSize;//最大线程数量是初始线程的两倍，如果等待队列满了后
  private static  long keepAliveTime = 5;//线程存活时间 （分钟）
  public DownLoadExcutor() {
    this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(), new JobThreadFactory());
  }

  public static int getTaskThreadSize() {
    return taskThreadSize;
  }

  public static int getMaxTaskSize() {
    return maxTaskSize;
  }

  public static int getCorePoolSize() {
    return corePoolSize;
  }

  public static int getMaximumPoolSize() {
    return maximumPoolSize;
  }

  public static long getKeepAliveTime() {
    return keepAliveTime;
  }

//  /**
//   * @param corePoolSize 初始线程数量
//   * @param maximumPoolSize 最大线程数量
//   * @param keepAliveTime  TimeUnit.SECONDS
//   */
//  public DownLoadExcutor(int corePoolSize,
//                         int maximumPoolSize,
//                         long keepAliveTime) {
//    this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
//            new LinkedBlockingQueue<>(), new JobThreadFactory());
//  }

  public ThreadPoolExecutor getThreadPoolExecutor() {
    return threadPoolExecutor;
  }

  public void execute(@NonNull Runnable runnable) {
    this.threadPoolExecutor.execute(runnable);
  }

  private static class JobThreadFactory implements ThreadFactory {
    private int counter = 0;

    @Override public Thread newThread(@NonNull Runnable runnable) {
      return new Thread(runnable, "android_" + counter++);
    }
  }
}
