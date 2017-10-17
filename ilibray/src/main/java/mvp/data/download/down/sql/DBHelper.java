package mvp.data.download.down.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import mvp.data.download.down.entity.DownloadEntity;

/**
 * Created by ericYang on 2017/6/1.
 * Email:eric.yang@huanmedia.com
 * what?
 */
public class DBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "com.ilibray.idownload.db";
	private final static int DATABASE_VERSION = 5;

	private final String CREATE_TABLE_TASKS =
			"CREATE TABLE IF NOT EXISTS "+ DownloadEntity.Property.TABLENAME + " ("
					+ DownloadEntity.Property.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ DownloadEntity.Property.COLUMN_NAME + " VARCHAR( 128 ) NOT NULL, "
					+ DownloadEntity.Property.COLUMN_CONTEXTSIZE + " INTEGER, "
					+ DownloadEntity.Property.COLUMN_DOWNLOADSIZE + " INTEGER,"
					+ DownloadEntity.Property.COLUMN_STATUS + " INT( 3 ), "
					+ DownloadEntity.Property.COLUMN_URL + " VARCHAR( 256 ) NOT NULL, "
					+ DownloadEntity.Property.COLUMN_SAVE_ADDRESS + " VARCHAR( 256 ),"
					+ DownloadEntity.Property.COLUMN_EXTENSION + " VARCHAR( 32 ),"
					+ DownloadEntity.Property.COLUMN_METHOD + " VARCHAR( 3 ),"
					+ DownloadEntity.Property.COLUMN_CREATETIME + " INTEGER,"
					+ DownloadEntity.Property.COLUMN_PARMS + " VARCHAR,"
					+ DownloadEntity.Property.COLUMN_KEY + " VARCHAR,"
					+ DownloadEntity.Property.COLUMN_LASTMODIFY + " VARCHAR(128),"
					+ DownloadEntity.Property.COLUMN_CHUNK + " INT(1),"
					+ DownloadEntity.Property.COLUMN_HEADES + " VARCHAR"
					+ " ); ";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + DownloadEntity.Property.TABLENAME);
		onCreate(db);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL(CREATE_TABLE_TASKS);
	}
}