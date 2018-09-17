package com.example.neoman.notes

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.list_element_layout.view.*

class MainActivity : AppCompatActivity() {
    private var notes=ArrayList<Note>()
    private var adapter:ListAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //load all notes from database
        LoadQuery("%")

    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    fun LoadQuery(match:String){//get notes fom database(use this with search pass in "%" to load all notes)
        val dbManager=SQLiteDBManager(this)
        //search through all db rows for ones where the Title column entry matches match string return matching rows sorted by the Title column entries
        val cursor=dbManager.Query(arrayOf("ID","Title","Content"),"Title LIKE ?", arrayOf(match),"Title")

        notes.clear()
        if(cursor.moveToFirst()){//if it returned any results based on query
            var title=cursor.getString(cursor.getColumnIndex("Title"))
            var content=cursor.getString(cursor.getColumnIndex("Content"))
            var ID=cursor.getInt(cursor.getColumnIndex("ID"))
            notes.add(Note(ID,title,content))
            while(cursor.moveToNext()){//keep adding notes while more are left
                title=cursor.getString(cursor.getColumnIndex("Title"))
                content=cursor.getString(cursor.getColumnIndex("Content"))
                ID=cursor.getInt(cursor.getColumnIndex("ID"))
                notes.add(Note(ID,title,content))
            }
        }
        //link notes to list
        adapter= ListAdapter(this,notes)
        NoteList.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        val sv= menu!!.findItem(R.id.Search).actionView as SearchView
        val sm= getSystemService(Context.SEARCH_SERVICE) as SearchManager

        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                //call loadquery every time they change text? maybe everytime they change text and text is >3 characters?
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                LoadQuery("%"+p0+"%")
                //only searches through title, (make fun for search that searches content and title?)

                Toast.makeText(applicationContext,p0,Toast.LENGTH_LONG).show()
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.Add->{
                val intent= Intent(this,NoteActivity::class.java)
                intent.putExtra("title","New Note")
                intent.putExtra("content","")
                this.startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class ListAdapter(var context: Context, var items:ArrayList<Note>): BaseAdapter() {

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val item=items[p0]
            val view=(context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.list_element_layout,null)

            view.Title.text=item.title
            view.Cont.text=item.content
            view.setOnClickListener{//does not run if button in the view is clicked(makes things easier)
                val intent= Intent(context,NoteActivity::class.java)
                //figure out what to pass to note activity
                intent.putExtra("title",item.title)
                intent.putExtra("content",item.content)
                intent.putExtra("ID",item.ID)
                context.startActivity(intent)
            }
            view.Del.setOnClickListener {
                val dbManager=SQLiteDBManager(context)
                //search through all db rows for ones where the Title column entry matches match string return matching rows sorted by the Title column entries
                dbManager.Delete("ID=?", arrayOf(item.ID.toString()))
                LoadQuery("%")
            }
            return view
        }

        override fun getItem(p0: Int): Any {
            return items[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return items.size
        }

    }
}
