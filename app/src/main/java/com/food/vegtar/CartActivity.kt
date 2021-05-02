package com.food.vegtar

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.food.vegtar.Dao.OrderDao
import com.food.vegtar.models.OrderDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CartActivity : AppCompatActivity(), ICartAdapter {
    var SHARED_PRE="sharedpreference"
    var count: Int = 0
    lateinit var emptyMessage:TextView
    lateinit var firstshop:String
    lateinit var totalPrice:TextView
    lateinit var viewModel:CartViewModel
    lateinit var adapter:CartAdapter
    lateinit var dialog: Dialog
    lateinit var cartRecyclerView:RecyclerView
    lateinit var orderNow:Button
    lateinit var cartItem:List<OrderDetail>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_cart)
        orderNow = findViewById(R.id.orderNow)
        cartRecyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)
        emptyMessage = findViewById(R.id.emptyMessage)
        loadData()
        Log.e("Checking","Shop name is ${firstshop} and count= ${count}")
        adapter = CartAdapter(this)
        cartRecyclerView.adapter = adapter
        totalPrice = findViewById(R.id.totalPrice)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        viewModel=ViewModelProvider(
                this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
        )
        ).get(CartViewModel::class.java)

        val imageUrl = intent.getStringExtra("ImageUrl")
        val name = intent.getStringExtra("Name")
        val price =  intent.getStringExtra("Price")
        val description = intent.getStringExtra("Description")
        val shopName = intent.getStringExtra("Shopname")
        val ShopImage = intent.getStringExtra("ShopImage")

        viewModel.allCart.observe(this, androidx.lifecycle.Observer { list ->
            list?.let {
                adapter.updateList(it)
                updatePrice()
                cartItem = list
            }
        })

        orderNow.setOnClickListener {
            val intent = Intent(this,userDetail::class.java)
            intent.putExtra("fromcart","fromcart")
            val nameList = ArrayList<String>()
            val priceList = ArrayList<String>()
            val quantityList = ArrayList<Int>()
//            Log.e("Checking",cartItem.size.toString())
            for(i in 0..cartItem.size-1){
                nameList.add(cartItem[i].EachFoodName)
                priceList.add(cartItem[i].EachFoodPrice!!)
                quantityList.add(cartItem[i].quantity!!)
            }
            intent.putStringArrayListExtra("Foodlist",nameList)
            intent.putStringArrayListExtra("Pricelist",priceList)
            intent.putIntegerArrayListExtra("Quantitylist",quantityList)
            intent.putExtra("ShopImage",ShopImage)
            startActivity(intent)
        }

        if(shopName!=null){
            if(count!= 0 && shopName!=firstshop){

                val view = layoutInflater.inflate(R.layout.deletedialog, null)
                val builder = AlertDialog.Builder(this)
                builder.setView(view)
                builder.create()
                builder.setCancelable(false)
                dialog = builder.show()
                view.findViewById<Button>(R.id.cancelButton)?.setOnClickListener {
                    dialog.dismiss()
                }
                view.findViewById<Button>(R.id.confirm)?.setOnClickListener {
                    viewModel.deleteAll()
                    dialog.dismiss()
                    val x = OrderDetail(name!!, price, imageUrl, description, shopName)
                    viewModel.insertCart(x)
                    count = 1
                    savecount(count)
                    savedata(shopName)
                    loadData()

                }


//                val y:Boolean = showDialog()
//                if(y){
//                    val x = OrderDetail(name, price, imageUrl, description, shopName)
//                    viewModel.insertCart(x)
//                    count = 1
//                    savecount(count)
//                    savedata(shopName)
//                    loadData()
//                }

            }
            else if(count!=0 && shopName==firstshop){
                val x = OrderDetail(name!!, price, imageUrl, description, shopName)
                Log.e("Checking","count!=0 && shopName==firstshop se add hua h : $shopName")
                count=count+1
                savecount(count)
                viewModel.insertCart(x)
            }
            else if(count==0){
                savedata(shopName)
                count=count+1
                savecount(count)
                loadData()
                Log.e("Checking","Is time last name is value hai ${firstshop}")
                val x = OrderDetail(name!!, price, imageUrl, description, shopName)
                Log.e("Checking","count==0 se add hua h : $shopName")
                viewModel.insertCart(x)
            }
//            else{
//                val x = OrderDetail(name, price, imageUrl, description, shopName)
//                viewModel.insertCart(x)
//            }
        }
    }

    override fun onAddClick(position: Int, num: Int) {
        if(num==0){
            viewModel.deleteCart(adapter.cartItem[position])
        }
        else {
            val x = OrderDetail(adapter.cartItem[position].EachFoodName, adapter.cartItem[position].EachFoodPrice,
                    adapter.cartItem[position].EachFoodUrl, adapter.cartItem[position].EachFoodDes,
                    adapter.cartItem[position].EachShopName, num)
//            x.id = adapter.cartItem[position].id
            viewModel.updateCart(x)
        }
    }

    override fun onMinusClick(position: Int, num: Int) {
        if(num==0){
            viewModel.deleteCart(adapter.cartItem[position])
            count-=1
            savecount(count)
            loadData()
        }
        else {
            val x = OrderDetail(adapter.cartItem[position].EachFoodName, adapter.cartItem[position].EachFoodPrice,
                    adapter.cartItem[position].EachFoodUrl, adapter.cartItem[position].EachFoodDes,
                    adapter.cartItem[position].EachShopName, num)
//            x.id = adapter.cartItem[position].id
            viewModel.updateCart(x)
        }
    }

    private fun updatePrice(){
        val totalNumber = adapter.cartItem.size-1
        var a=0
        for( i in 0..totalNumber){
            a += Integer.valueOf(adapter.cartItem[i].EachFoodPrice)*adapter.cartItem[i].quantity!!
        }
        totalPrice.text = "₹${a.toString()}"
    }

    fun loadData(){
        var sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        firstshop = sharedPreferences.getString("last", "")!!
        count = sharedPreferences.getInt("count", 0)
        if(count==0){
            cartRecyclerView.visibility = View.GONE
            emptyMessage.visibility = View.VISIBLE
        }
        else{
            cartRecyclerView.visibility = View.VISIBLE
            emptyMessage.visibility = View.GONE
        }
    }
    fun savedata(x:String){
        var sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        var editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("last", x)
        editor.apply()
    }

    fun savecount(count:Int){
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("count",count)
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
        savecount(count)
        savedata(firstshop)
    }

}