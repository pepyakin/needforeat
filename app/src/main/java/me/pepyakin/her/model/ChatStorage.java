package me.pepyakin.her.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.QueryObservable;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

final class ChatStorage  {

    private static class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, "chat", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            try {
                db.execSQL(
                        "CREATE TABLE chat (" +
                                "id INTEGER PRIMARY KEY, " +
                                "inbound INTEGER," +
                                "msg_text TEXT," +
                                "timestamp INTEGER" +
                                ")");

                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Expected to be first version, but was.
            throw new UnsupportedOperationException("oldVersion=" +
                    oldVersion + ", newVersion=" + newVersion);
        }
    }

    public static ChatStorage createChatStorage(Context context) {
        OpenHelper openHelper = new OpenHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        BriteDatabase db = sqlBrite.wrapDatabaseHelper(openHelper,
                Schedulers.io());
        return new ChatStorage(db);
    }

    private BriteDatabase db;

    ChatStorage(BriteDatabase db) {
        this.db = db;
    }

    public void insertChatItem(ChatItem chatItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("msg_text", chatItem.text);
        contentValues.put("inbound", chatItem.inbound ? 1 : 0);
        contentValues.put("timestamp", chatItem.timestamp);

        db.insert("chat", contentValues);
    }

    public Observable<List<ChatItem>> queryChat() {
        QueryObservable chatQuery = db.createQuery(
                "chat", "SELECT * FROM (SELECT * FROM chat ORDER BY " +
                        "timestamp DESC LIMIT 100) ORDER BY timestamp ASC");
        return chatQuery.mapToList(new Func1<Cursor, ChatItem>() {
            @Override
            public ChatItem call(Cursor cursor) {
                int msgTextIdx = cursor.getColumnIndexOrThrow("msg_text");
                int inboundIdx = cursor.getColumnIndexOrThrow("inbound");
                int timestampIdx = cursor.getColumnIndexOrThrow("timestamp");

                String msgText = cursor.getString(msgTextIdx);
                boolean inbound = cursor.getInt(inboundIdx) != 0;
                long timestamp = cursor.getLong(timestampIdx);

                return new ChatItem(inbound, msgText, timestamp);
            }
        });
    }
}
