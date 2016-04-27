package com.restws.cync.kamal.cync.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CyncDBHelper
  extends SQLiteOpenHelper
{
  private static final String DATABASE_NAME = CyncDBContract.RegistrationEntry.TABLE_NAME;
  private static final int DATABASE_VERSION = 2;

  public CyncDBHelper(Context paramContext)
  {
    super(paramContext, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CyncDBContract.RegistrationEntry.TABLE_NAME);
    paramSQLiteDatabase.execSQL("CREATE TABLE "+ CyncDBContract.RegistrationEntry.TABLE_NAME +" "+
            "( "+ CyncDBContract.RegistrationEntry.COLUMN_ID+" INTEGER PRIMARY KEY, " +
            CyncDBContract.RegistrationEntry.COLUMN_EMAIL +" TEXT UNIQUE NOT NULL,"+
            CyncDBContract.RegistrationEntry.COLUMN_REGISTER_STATUS +" TEXT UNIQUE NOT NULL  );");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CyncDBContract.ContactsEntry.TABLE_NAME);
    paramSQLiteDatabase.execSQL("CREATE TABLE "+ CyncDBContract.ContactsEntry.TABLE_NAME +"("+
            CyncDBContract.ContactsEntry.COLUMN_ID +" INTEGER PRIMARY KEY," +
            CyncDBContract.ContactsEntry.COLUMN_CONTACT_ID +" INTEGER NOT NULL," +
            CyncDBContract.ContactsEntry.COLUMN_NAME +" TEXT NOT NULL," +
            CyncDBContract.ContactsEntry.COLUMN_NUMBER +" TEXT UNIQUE NOT NULL );");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CyncDBContract.ServerContactsEntry.TABLE_NAME);
    paramSQLiteDatabase.execSQL("CREATE TABLE "+ CyncDBContract.ServerContactsEntry.TABLE_NAME +
            " ("+ CyncDBContract.ServerContactsEntry.COLUMN_ID +" INTEGER PRIMARY KEY," +
            CyncDBContract.ServerContactsEntry.COLUMN_NUMBER +" TEXT UNIQUE NOT NULL,"+
            CyncDBContract.ServerContactsEntry.COLUMN_IP +" TEXT NOT NULL );");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CyncDBContract.CurrentContactEntry.TABLE_NAME);
    paramSQLiteDatabase.execSQL("CREATE TABLE "+ CyncDBContract.CurrentContactEntry.TABLE_NAME +
            " ("+ CyncDBContract.CurrentContactEntry.COLUMN_ID +" INTEGER PRIMARY KEY, "+
            CyncDBContract.CurrentContactEntry.COLUMN_CURRENT_CONTACT +" TEXT UNIQUE NOT NULL);");
  }

  public void onInsert(SQLiteDatabase paramSQLiteDatabase, String paramString, ContentValues paramContentValues)
  {
    paramSQLiteDatabase.insert(paramString, null, paramContentValues);
  }

  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CyncDBContract.RegistrationEntry.TABLE_NAME);
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CyncDBContract.ContactsEntry.TABLE_NAME);
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CyncDBContract.ServerContactsEntry.TABLE_NAME);
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ CyncDBContract.CurrentContactEntry.TABLE_NAME);
    onCreate(paramSQLiteDatabase);
  }
}
