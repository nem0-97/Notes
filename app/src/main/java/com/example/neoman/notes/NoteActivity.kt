package com.example.neoman.notes

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_note.*

class NoteActivity : AppCompatActivity() {

    var ID:Int?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        val b:Bundle=intent.extras
        //use var cause might change name and note content
        val title=b.getString("title")
        val content=b.getString("content")
        ID=b.getInt("ID",-1)

        NoteTitle.text=SpannableStringBuilder(title)
        NoteContent.text=SpannableStringBuilder(content)
    }

    //onPause works just check if note with same ID already exist
    //or if ID=-1 that means new note because no ID was passed in by intent
    //which only happens when you come here from the new note item in menu
    override fun onPause() {//check if note exists in db, if not add new one, else update it
        val dbManager=SQLiteDBManager(this)

        val noteValues=ContentValues()
        noteValues.put("title",NoteTitle.text.toString())
        noteValues.put("content",NoteContent.text.toString())
        if(ID==-1){
            val id=dbManager.Insert(noteValues)
            if(id>0){
                Toast.makeText(this,"Note Saved",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"Note Save Error",Toast.LENGTH_LONG).show()
            }
        }
        else {
           //update the note,show how many notes were updated,(for testing to make sure ids are unique, should always be 1)
            Toast.makeText(this, dbManager.Update(noteValues, "ID=?", arrayOf(ID.toString())).toString()
                            + " Notes Were Updated", Toast.LENGTH_LONG).show()
        }
        super.onPause()
    }
}
