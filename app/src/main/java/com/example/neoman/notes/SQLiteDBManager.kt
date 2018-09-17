package com.example.neoman.notes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class SQLiteDBManager{
    //db,table,col info
    val dbName="MyNotes"
    val dbTable="Notes"
    val idCol="ID"
    val titleCol="Title"
    val contentCol="Content"
    val dbVersion=1

    /*if a table of Notes does not exist already then create one,
      it will have 3 columns titled:
        1)ID(Int(used to lookup items in table))
        2)Title(Text(string each row will contain that note's title))
        3)Content(Text(string each row will contain that note's content))
    */
    val SQLTableCreation="CREATE TABLE IF NOT EXISTS "+dbTable+" ("+idCol+" INTEGER PRIMARY KEY,"+titleCol+" TEXT, "+contentCol+" TEXT);"
    var SQLDB:SQLiteDatabase?=null

    constructor(context:Context){
        val db=DatabaseHelper(context)
        SQLDB=db.writableDatabase
    }

    inner class DatabaseHelper(val context: Context):SQLiteOpenHelper(context,dbName,null,dbVersion){
        override fun onCreate(p0: SQLiteDatabase?) {
            p0!!.execSQL(SQLTableCreation)
            Toast.makeText(context," database created",Toast.LENGTH_LONG).show()
        }

        override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {//if upgrading whole database or if adding and item?
            p0!!.execSQL("DROP TABLE IF EXISTS "+dbTable)
        }
    }

    fun Insert(note:ContentValues):Long{
        //insert new row containing note
        return SQLDB!!.insert(dbTable,"",note)
    }
    fun Query(columns:Array<String>,rows:String,rowArgs:Array<String>,sort:String):Cursor{
        //columns:which entries you want from the rows ,rows:the SQL query,rowArgs:which rows do you want query to get, sort:which column to sort by
        val qb=SQLiteQueryBuilder()
        qb.tables=dbTable
        return qb.query(SQLDB,columns,rows,rowArgs,null,null,sort)
    }
    fun Delete(rows:String,rowArgs:Array<String>):Int{
        //delete rows that match rowArgs
        return SQLDB!!.delete(dbTable,rows,rowArgs)
    }

    fun Update(note:ContentValues,rows:String,rowArgs:Array<String>):Int{
        //update rows that match rowArgs to note
        return SQLDB!!.update(dbTable,note,rows,rowArgs)
    }
}