package tech.anshul1507.melodix.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class LocalDB : SQLiteOpenHelper {

    object DBObject {
        const val DB_NAME = "MelodixDB"
        const val DB_VERSION = 1
        const val TABLE_NAME = "FavoriteTable"
        const val COL_ID = "SongID"
        const val COL_TITLE = "SongTitle"
        const val COL_ARTIST = "SongArtist"
        const val COL_PATH = "SongPath"
    }

    constructor(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ) : super(context, name, factory, version) {
    }

    constructor(context: Context?) : super(
        context,
        DBObject.DB_NAME,
        null,
        DBObject.DB_VERSION
    ) {
    }

    @SuppressLint("SQLiteString")
    override fun onCreate(sqliteDB: SQLiteDatabase?) {

        sqliteDB?.execSQL(
            "CREATE TABLE " +
                    DBObject.TABLE_NAME + "( " +
                    DBObject.COL_ID + "INTEGER," +
                    DBObject.COL_TITLE + " STRING," +
                    DBObject.COL_ARTIST + " STRING," +
                    DBObject.COL_PATH + " STRING" + ");"
        )
    }

    override fun onUpgrade(sqliteDB: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun storeFavorite(id: Int?, songTitle: String?, artist: String?, path: String?) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(DBObject.COL_ID, id)
        contentValues.put(DBObject.COL_TITLE, songTitle)
        contentValues.put(DBObject.COL_ARTIST, artist)
        contentValues.put(DBObject.COL_PATH, path)

        db.insert(DBObject.TABLE_NAME, null, contentValues)
        db.close()
    }

    fun deleteFavorite(id: Int) {
        val db = this.writableDatabase
        db.delete(DBObject.TABLE_NAME, DBObject.COL_ID + "=" + id, null)
        db.close()
    }

    fun queryDBList(i: Long): Int? {
        var flag = 0
        /*Here a try-catch block is used to handle the exception as no songs in the database can result in null-pointer exception*/

        try {
            val db = this.readableDatabase

            /*The SQL query used for obtaining the songs is :
            * SELECT * FROM FavoriteTable
            * The query returns all the items present in the table*/
            val queryParams = "SELECT * FROM " + DBObject.TABLE_NAME
            val cSor = db.rawQuery(queryParams, null)

            /*The cSor stores the result obtained from the database
            * The function moveToFirst() checks if there are any entries or not*/
            if (cSor.moveToFirst()) {
                //TODO:: 1 or more rows are returned then we store all the entries into the array list songList
                do {
                    val id = cSor.getLong(cSor.getColumnIndexOrThrow(DBObject.COL_ID))
                    var title =
                        cSor.getString(cSor.getColumnIndexOrThrow(DBObject.COL_TITLE))
                    var artist =
                        cSor.getString(cSor.getColumnIndexOrThrow(DBObject.COL_ARTIST))
                    var songPath =
                        cSor.getString(cSor.getColumnIndexOrThrow(DBObject.COL_PATH))
                    if (id == i) {
                        flag = 1
                    }
                }
                /*This task is performed till there are items present*/
                while (cSor.moveToNext())
            }
            /*Otherwise null is returned*/
            else {
                return 0
            }
        }

        /*If there was any exception then it is handled by this*/
        catch (e: Exception) {
            e.printStackTrace()
        }

        //TODO:: return the songList
        return flag
    }

    @SuppressLint("Recycle")
    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        val queryParam = "SELECT * FROM " + DBObject.TABLE_NAME
        val cSor = db.rawQuery(queryParam, null)
        if (cSor.moveToFirst()) {
            do {
                counter += 1
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return counter
    }

}