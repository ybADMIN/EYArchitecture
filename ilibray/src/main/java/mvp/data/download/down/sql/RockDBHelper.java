package mvp.data.download.down.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ericYang on 2017/6/1.
 * Email:eric.yang@huanmedia.com
 * what?
 */
public class RockDBHelper {
	private final   AtomicInteger mOpenCounter;
	private  static RockDBHelper instance;
	private SQLiteDatabase mDatabase;
	private static DBHelper dbHelper;
	private RockDBHelper(Context context) {
		this.mOpenCounter = new AtomicInteger(0);
		dbHelper=new DBHelper(context);
	}

	public  static RockDBHelper getInstance(Context context){
		if(instance == null){
			synchronized(RockDBHelper.class){

				if(instance == null){
					instance = new RockDBHelper(context);
				}
			}
		}
		return  instance;
	}

	public synchronized SQLiteDatabase getWritableDatabase() {
		if(mOpenCounter.incrementAndGet() == 1) {
			// 获取一个数据库实例
			mDatabase = dbHelper.getWritableDatabase();
		}
		return mDatabase;
	}

	public synchronized void close() {
		if(mOpenCounter.decrementAndGet() == 0) {
			//关闭没有连接的数据库
			if (mDatabase!=null)
			mDatabase.close();
		}
	}

}