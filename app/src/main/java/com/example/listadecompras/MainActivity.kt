package com.example.listadecompras

import Item
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.common.reflect.TypeToken
import com.google.gson.Gson




class MainActivity : AppCompatActivity() {
    private val items = mutableListOf<Item>()
    private lateinit var adapter: ItemAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val ITEM_PREFS_KEY = "item_prefs_key"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editTextItem = findViewById<EditText>(R.id.editTextItem)
        val buttonAdd = findViewById<Button>(R.id.buttonAdd)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        adapter = ItemAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicialize o SharedPreferences
        sharedPreferences = getSharedPreferences("com.example.listadecompras.PREFERENCES", MODE_PRIVATE)

        // Carregue os itens do SharedPreferences após a inicialização do adapter
        loadItemsFromSharedPreferences()

        adapter = ItemAdapter(items)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonAdd.setOnClickListener {
            val itemName = editTextItem.text.toString()
            if (itemName.isNotEmpty()) {
                val newItem = Item(System.currentTimeMillis(), itemName, false)
                items.add(newItem)
                adapter.notifyItemInserted(items.size - 1)
                editTextItem.text.clear()

                saveItemsToSharedPreferences()
            }else{
                showEmptyItemAlert()
            }
            val buttonDelete = findViewById<Button>(R.id.buttonDelete)

            buttonDelete.setOnClickListener {
                showConfirmationDialog()
            }
            loadItemsFromSharedPreferences()
            }

        }

    private fun showEmptyItemAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Erro")
        builder.setMessage("Você está tentando adicionar um item vazio.")
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }


    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmação")
        builder.setMessage("Tem certeza de que deseja excluir os itens marcados?")

        builder.setPositiveButton("Sim") { _, _ ->
            // O usuário confirmou a exclusão, então você pode remover os itens marcados aqui
            removeCheckedItems()
        }

        builder.setNegativeButton("Não") { _, _ ->
            // O usuário cancelou a exclusão, não é necessário fazer nada
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun removeCheckedItems() {
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.checked) {
                iterator.remove()
            }
        }
        adapter.notifyDataSetChanged()

        saveItemsToSharedPreferences()
    }

    fun saveItemsToSharedPreferences() {
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(items)
        editor.putString(ITEM_PREFS_KEY, json)
        editor.apply()
    }

    fun loadItemsFromSharedPreferences() {
        val json = sharedPreferences.getString(ITEM_PREFS_KEY, null)
        if (json != null) {
            val gson = Gson()
            val itemType = object : TypeToken<List<Item>>() {}.type
            items.clear()
            items.addAll(gson.fromJson(json, itemType))
            adapter.notifyDataSetChanged()
        }
    }
}

