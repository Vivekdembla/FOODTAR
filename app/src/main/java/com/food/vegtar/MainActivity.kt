package com.food.vegtar

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.food.vegtar.Dao.ShopDao
import com.food.vegtar.models.Shop
import com.google.firebase.firestore.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity(), IShopAdapter {
    private lateinit var home: ImageView
    private lateinit var cart: ImageView
    private lateinit var profile: ImageView
    private lateinit var adapter: shopAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var shopDao: ShopDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        shopDao = ShopDao()
        home = findViewById(R.id.home)
        cart = findViewById(R.id.cartView)
        profile = findViewById(R.id.profile)
        setUpRecyclerView()
        profile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        getMyData()

    }

    private fun getMyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://api.timezonedb.com/")
            .build()
            .create(ApiInterface::class.java)
        val retrofitData = retrofitBuilder.getData()
        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                val responseBody = response.body()!!
                val myStringBuilder = StringBuilder()
                myStringBuilder.append(responseBody.formatted)
                myStringBuilder.append("\n")
                Log.e("Checking", myStringBuilder.toString())
            }

            override fun onFailure(call: Call<com.food.vegtar.MyData?>, t: Throwable) {

            }
        })
    }

    private fun setUpRecyclerView(){
        val shopCollection=shopDao.shopCollection
        val query = shopCollection.orderBy("NameOfShop", Query.Direction.ASCENDING)
        val recyclerViewOption = FirestoreRecyclerOptions.Builder<Shop>().setQuery(
            query,
            Shop::class.java
        ).build()
        adapter = shopAdapter(recyclerViewOption, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onShopClicked(shopid: String) {
        val intent = Intent(this, ShopDetail::class.java)
        intent.putExtra("shopid", shopid)
        startActivity(intent)
    }

    fun cartActivity(view: View) {
        val intent = Intent(this, CartActivity::class.java)
        startActivity(intent)
    }
}