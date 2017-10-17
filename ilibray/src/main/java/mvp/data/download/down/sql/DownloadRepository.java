package mvp.data.download.down.sql;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.Nullable;

import mvp.data.download.down.entity.DownloadEntity;
import mvp.data.download.down.DownloadStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericYang on 2017/6/6.
 * Email:eric.yang@huanmedia.com
 * what?
 */

public class DownloadRepository {
    private final Context mContext;
    private final RockDBHelper mHelper;

    public DownloadRepository(Context context) {
        this.mContext = context;
        mHelper = RockDBHelper.getInstance(mContext);
    }

    public long insertTask(DownloadEntity task) {
        try {
            long id = mHelper.getWritableDatabase()
                    .insert(DownloadEntity.Property.TABLENAME, null, task.convertToContentValues());
            task.setId(id);
            return id;
        } finally {
            mHelper.close();
        }
    }

    public boolean update(DownloadEntity task) {
        try {
            int affectedRow = mHelper.getWritableDatabase()
                    .update(DownloadEntity.Property.TABLENAME, task.convertToContentValues(), DownloadEntity.Property.COLUMN_ID + "=" + task.getId(), null);
            return affectedRow != 0;
        } finally {
            mHelper.close();
        }
    } public boolean update(DownloadStatus status) {
        try {
            int affectedRow = mHelper.getWritableDatabase()
                    .update(DownloadEntity.Property.TABLENAME, status.convertToContentValues(), DownloadEntity.Property.COLUMN_ID + "=" + status.getId(), null);
            return affectedRow != 0;
        } finally {
            mHelper.close();
        }
    }

    public List<DownloadEntity> getTasksInState(int state) {
        try {
            List<DownloadEntity> tasks = new ArrayList<>();
            String query;

            if (state < 6)
                query = "SELECT * FROM " + DownloadEntity.Property.TABLENAME + " WHERE " +DownloadEntity.Property.COLUMN_STATUS + "=" + SqlString.Int(state);
            else
                query = "SELECT * FROM " +DownloadEntity.Property.TABLENAME;

            Cursor cr = mHelper.getWritableDatabase().rawQuery(query, null);

            if (cr != null) {
                cr.moveToFirst();
                while (!cr.isAfterLast()) {
                    DownloadEntity task = new DownloadEntity();
                    task.cursorToTask(cr);
                    tasks.add(task);
                    cr.moveToNext();
                }

                cr.close();
            }

            return tasks;
        } finally {
            mHelper.close();
        }
    }

    public DownloadEntity getTaskInfo(int id) {
        try {
            String query = "SELECT * FROM " + DownloadEntity.Property.TABLENAME + " WHERE " + DownloadEntity.Property.COLUMN_ID + "=" + SqlString.Int(id);
            Cursor cr = mHelper.getWritableDatabase().rawQuery(query, null);

            DownloadEntity task = new DownloadEntity();
            if (cr != null && cr.moveToFirst()) {
                task.cursorToTask(cr);
                cr.close();
            }
            return task;
        } finally {
            mHelper.close();
        }
    }

    public DownloadEntity getTaskInfoWithKey(String key) {
        try {
            String query = "SELECT * FROM " + DownloadEntity.Property.TABLENAME + " WHERE " + DownloadEntity.Property.COLUMN_KEY + "=" + SqlString.String(key);
            Cursor cr = mHelper.getWritableDatabase().rawQuery(query, null);

            DownloadEntity task = new DownloadEntity();
            if (cr != null && cr.moveToFirst()) {
                task.cursorToTask(cr);
                cr.close();
            }
            return task;
        } finally {
            mHelper.close();
        }
    }
    @Nullable
    public DownloadEntity getTaskInfoWithName(String name) {
        try {
            String query = "SELECT * FROM " + DownloadEntity.Property.TABLENAME + " WHERE " + DownloadEntity.Property.COLUMN_NAME + "=" + SqlString.String(name);
            Cursor cr = mHelper.getWritableDatabase().rawQuery(query, null);

            DownloadEntity task = null;
            if (cr != null && cr.moveToFirst()) {
                task=new DownloadEntity();
                task.cursorToTask(cr);
                cr.close();
            }
            return task;
        } finally {
            mHelper.close();
        }
    }

    public boolean delete(long taskID) {
        try {
            int affectedRow = mHelper.getWritableDatabase()
                    .delete(DownloadEntity.Property.TABLENAME, DownloadEntity.Property.COLUMN_ID + "=" + SqlString.Long(taskID), null);

            return affectedRow != 0;

        } finally {
            mHelper.close();
        }
    }
    public boolean delete(String key) {
        try {
            int affectedRow = mHelper.getWritableDatabase()
                    .delete(DownloadEntity.Property.TABLENAME, DownloadEntity.Property.COLUMN_KEY + "=" +  SqlString.String(key), null);

            return affectedRow != 0;

        } finally {
            mHelper.close();
        }
    }

    public boolean containsTask(String key) {
        try {
            boolean result = false;
            String query = "SELECT * FROM " + DownloadEntity.Property.TABLENAME + " WHERE " + DownloadEntity.Property.COLUMN_KEY + "=" + SqlString.String(key);
            Cursor cr = mHelper.getWritableDatabase().rawQuery(query, null);
            if (cr != null && cr.getCount()!= 0) {
                result = true;
                cr.close();
            }
            return result;
        } finally {
            mHelper.close();
        }
    }

    public boolean containsTaskForName(String fixName) {
        try {
            boolean result = false;
            String query = "SELECT * FROM " + DownloadEntity.Property.TABLENAME + " WHERE " + DownloadEntity.Property.COLUMN_NAME + "=" + SqlString.String(fixName);
            Cursor cr = mHelper.getWritableDatabase().rawQuery(query, null);
            if (cr != null && cr.getCount()!= 0) {
                result = true;
                cr.close();
            }
            return result;
        } finally {
            mHelper.close();
        }
    }
}
