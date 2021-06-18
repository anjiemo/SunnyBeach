package com.example.blogsystem.db.dao;

import android.database.Cursor;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.blogsystem.db.Converters;
import com.example.blogsystem.utils.CookieStore;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import okhttp3.Cookie;

@SuppressWarnings({"unchecked", "deprecation"})
public final class CookieDao_Impl implements CookieDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CookieStore> __insertionAdapterOfCookieStore;

  private final Converters __converters = new Converters();

  public CookieDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCookieStore = new EntityInsertionAdapter<CookieStore>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `tb_cookies` (`host`,`cookies`) VALUES (?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CookieStore value) {
        if (value.getHost() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getHost());
        }
        final String _tmp;
        _tmp = __converters.cookiesToJson(value.getCookies());
        if (_tmp == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, _tmp);
        }
      }
    };
  }

  @Override
  public Object save(final List<CookieStore> cookieStoreSet, final Continuation<? super Unit> p1) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCookieStore.insert(cookieStoreSet);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, p1);
  }

  @Override
  public CookieStore getCookiesByHost(final String host) {
    final String _sql = "SELECT * FROM tb_cookies WHERE host = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (host == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, host);
    }
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
      try {
        final int _cursorIndexOfHost = CursorUtil.getColumnIndexOrThrow(_cursor, "host");
        final int _cursorIndexOfCookies = CursorUtil.getColumnIndexOrThrow(_cursor, "cookies");
        final CookieStore _result;
        if(_cursor.moveToFirst()) {
          final String _tmpHost;
          if (_cursor.isNull(_cursorIndexOfHost)) {
            _tmpHost = null;
          } else {
            _tmpHost = _cursor.getString(_cursorIndexOfHost);
          }
          final List<Cookie> _tmpCookies;
          final String _tmp;
          if (_cursor.isNull(_cursorIndexOfCookies)) {
            _tmp = null;
          } else {
            _tmp = _cursor.getString(_cursorIndexOfCookies);
          }
          _tmpCookies = __converters.jsonFromCookies(_tmp);
          _result = new CookieStore(_tmpHost,_tmpCookies);
        } else {
          _result = null;
        }
        __db.setTransactionSuccessful();
        return _result;
      } finally {
        _cursor.close();
        _statement.release();
      }
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<CookieStore> getCookies() {
    final String _sql = "SELECT * FROM tb_cookies";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
      try {
        final int _cursorIndexOfHost = CursorUtil.getColumnIndexOrThrow(_cursor, "host");
        final int _cursorIndexOfCookies = CursorUtil.getColumnIndexOrThrow(_cursor, "cookies");
        final List<CookieStore> _result = new ArrayList<CookieStore>(_cursor.getCount());
        while(_cursor.moveToNext()) {
          final CookieStore _item;
          final String _tmpHost;
          if (_cursor.isNull(_cursorIndexOfHost)) {
            _tmpHost = null;
          } else {
            _tmpHost = _cursor.getString(_cursorIndexOfHost);
          }
          final List<Cookie> _tmpCookies;
          final String _tmp;
          if (_cursor.isNull(_cursorIndexOfCookies)) {
            _tmp = null;
          } else {
            _tmp = _cursor.getString(_cursorIndexOfCookies);
          }
          _tmpCookies = __converters.jsonFromCookies(_tmp);
          _item = new CookieStore(_tmpHost,_tmpCookies);
          _result.add(_item);
        }
        __db.setTransactionSuccessful();
        return _result;
      } finally {
        _cursor.close();
        _statement.release();
      }
    } finally {
      __db.endTransaction();
    }
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
