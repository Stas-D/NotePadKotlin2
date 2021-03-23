package com.example.andoid.notepadkotlin2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.andoid.notepad_kotlin.db.MyDbManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val myDbManager = MyDbManager(this)
    val myAdapter = MyAdapter(ArrayList(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter()
        }


    fun onClickSaveAdd(view: View) {
      val i = Intent (this, EditActivity::class.java)
        startActivity(i)

        }



    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    fun init (){

        rcView.layoutManager = LinearLayoutManager(this)
        val swapHelper = getSwapMg()
        swapHelper?.attachToRecyclerView(rcView)
        rcView.adapter = myAdapter
    }


    private fun searchView (){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val list = myDbManager.readDbData(newText!!)
                myAdapter.updateAdapter(list)
                return true
            }
        })
    }


    fun fillAdapter (){

        val list = myDbManager.readDbData("")
        myAdapter.updateAdapter(list)


    }

    private fun getSwapMg(): ItemTouchHelper? {
        return ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)
            }
        })
    }

}

