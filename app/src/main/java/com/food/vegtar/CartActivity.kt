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
import android.widget.*
import android.widget.Toast.makeText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.food.vegtar.Dao.OrderDao
import com.food.vegtar.models.OrderDetail
import com.food.vegtar.models.Shop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Array.getInt


class CartActivity : AppCompatActivity(), ICartAdapter {
    var SHARED_PRE="sharedpreference"
    var count: Int = 1
    lateinit var emptyMessage:TextView
    lateinit var firstshop:String
    lateinit var totalPrice:TextView
    lateinit var viewModel:CartViewModel
    lateinit var adapter:CartAdapter
    lateinit var dialog: Dialog
    lateinit var cartRecyclerView:RecyclerView
    lateinit var orderNow:Button
    lateinit var ShopClosed:TextView
    lateinit var cartItem:List<OrderDetail>
    lateinit var progressBar: ProgressBar
    lateinit var datetime:String
    lateinit var minimumOrder:TextView
    lateinit var nothing:ImageView
    var a=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_cart)
        val sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        nothing = findViewById(R.id.nothing)
        var minimum = sharedPreferences.getInt("Minimum", 0)
        orderNow = findViewById(R.id.orderNow)
        cartRecyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)
        progressBar = findViewById(R.id.progressBar3)
        emptyMessage = findViewById(R.id.emptyMessage)
        ShopClosed = findViewById(R.id.timeOver)
        minimumOrder = findViewById(R.id.minimumOrder)
        loadData()
        Log.e("Checking","Count is $count")
        Log.e("Checking","Shop name is ${firstshop} and count= ${count}")
        var delivery = sharedPreferences.getInt("delivery",0)
        adapter = CartAdapter(this,delivery)
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

        viewModel.allCart.observe(this, androidx.lifecycle.Observer { list ->
            list?.let {
                adapter.updateList(it)
                updatePrice()
                cartItem = list
            }
        })
        if(count!=1){
            progressBar.visibility=View.VISIBLE}
        getMyData()


        orderNow.setOnClickListener {
            var intent= Intent(this,userDetail::class.java)
            intent.putExtra("fromcart","fromcart")
            val nameList = ArrayList<String>()
            val priceList = ArrayList<String>()
            val quantityList = ArrayList<Int>()
            Log.e("Checking",cartItem.size.toString())
            for(i in 0..cartItem.size-1){
                nameList.add(cartItem[i].EachFoodName)
                priceList.add(cartItem[i].EachFoodPrice!!)
                quantityList.add(cartItem[i].quantity!!)
            }
            intent.putStringArrayListExtra("Foodlist",nameList)
            intent.putStringArrayListExtra("Pricelist",priceList)
            intent.putIntegerArrayListExtra("Quantitylist",quantityList)
            intent.putExtra("DateTime",datetime)
            startActivity(intent)
        }

        if(shopName!=null){
            if(count!= 1 && shopName!=firstshop){

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
                    count = 2
                    savecount(count)
                    savedata(shopName)
                    loadData()
                }
            }
            else if(count!=1 && shopName==firstshop){
                val x = OrderDetail(name!!, price, imageUrl, description, shopName)
                Log.e("Checking","count!=0 && shopName==firstshop se add hua h : $shopName")
                count=count+1
                savecount(count)
                viewModel.insertCart(x)
            }
            else if(count==1){
                savedata(shopName)
                count=count+1
                savecount(count)
                loadData()
                Log.e("Checking","Is time last name is value hai ${firstshop}")
                val x = OrderDetail(name!!, price, imageUrl, description, shopName)
                Log.e("Checking","count==0 se add hua h : $shopName")
                viewModel.insertCart(x)
            }
        }
    }

    fun getMyData():String {
        orderNow.visibility = View.INVISIBLE
        ShopClosed.visibility= View.GONE
        minimumOrder.visibility = View.GONE
        var p:Double=0.0
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.timezonedb.com/")
            .build()
            .create(ApiInterface::class.java)
        val retrofitData = retrofitBuilder.getData()
        var myStringBuilder = StringBuilder()
        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                val responseBody = response.body()!!
                myStringBuilder = StringBuilder()
                myStringBuilder.append(responseBody.formatted)
                datetime=myStringBuilder.toString()
                Log.e("Checking", myStringBuilder.toString())
                val x = "${myStringBuilder.toString()[11]}${myStringBuilder.toString()[12]}" +
                        ".${myStringBuilder.toString()[14]}"+"${myStringBuilder.toString()[15]}"
                p =  x.toDouble()
                Log.e("Checking", "$p")
                val sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
                val open = sharedPreferences.getString("open", "0.0")!!.toDouble()
                val close = sharedPreferences.getString("close", "0.0")!!.toDouble()
                val minimum = sharedPreferences.getInt("Minimum", 0)
                if(a<minimum){
                    minimumOrder.text = "Minimum Order Should Be Of ₹$minimum"
                    minimumOrder.visibility = View.VISIBLE
                }
                else if(count!=1) {
                    if (p > open && p < close) {
                        orderNow.visibility = View.VISIBLE
                        ShopClosed.visibility = View.INVISIBLE
                    } else {
                        ShopClosed.visibility = View.VISIBLE
                        orderNow.visibility = View.INVISIBLE
                    }
                }
                progressBar.visibility = View.GONE
                nothing.visibility = View.GONE
            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                Toast.makeText(this@CartActivity,"Load this page again", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                nothing.visibility = View.GONE
            }
        })

        return myStringBuilder.toString()
    }


    override fun onAddClick(position: Int, num: Int, view: View) {
        progressBar.visibility = View.VISIBLE
        nothing.visibility = View.VISIBLE
        if(num==0){
            viewModel.deleteCart(adapter.cartItem[position])
        }
        else {
            val x = OrderDetail(adapter.cartItem[position].EachFoodName, adapter.cartItem[position].EachFoodPrice,
                adapter.cartItem[position].EachFoodUrl, adapter.cartItem[position].EachFoodDes,
                adapter.cartItem[position].EachShopName, num)
            viewModel.updateCart(x)
        }
        GlobalScope.launch(Dispatchers.IO) {
            getMyData()
        }
    }

    override fun onMinusClick(position: Int, num: Int, view: View) {
        nothing.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        if(num==0){
            viewModel.deleteCart(adapter.cartItem[position])
            count-=1
            savecount(count)
            loadData()
            Log.e("Checking","Count -> ${count}")
        }
        else {
            val x = OrderDetail(adapter.cartItem[position].EachFoodName, adapter.cartItem[position].EachFoodPrice,
                adapter.cartItem[position].EachFoodUrl, adapter.cartItem[position].EachFoodDes,
                adapter.cartItem[position].EachShopName,num)
//            x.id = adapter.cartItem[position].id
            viewModel.updateCart(x)
        }
        GlobalScope.launch(Dispatchers.IO) {
            getMyData()
        }
    }

    private fun updatePrice(){
        val totalNumber = adapter.cartItem.size-1
        a=0
        for( i in 0..totalNumber){
            a += Integer.valueOf(adapter.cartItem[i].EachFoodPrice)*adapter.cartItem[i].quantity!!
        }
        val sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        val delivery = sharedPreferences.getInt("delivery",0)
        totalPrice.text = "₹${a+delivery}"
    }

    fun loadData(){
        val sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        firstshop = sharedPreferences.getString("last", "")!!
        count = sharedPreferences.getInt("count", 1)
        if(count==1){
            cartRecyclerView.visibility = View.GONE
            orderNow.visibility = View.INVISIBLE
            ShopClosed.visibility = View.GONE
            emptyMessage.visibility= View.VISIBLE
            progressBar.visibility = View.GONE
        }
        else{
            cartRecyclerView.visibility = View.VISIBLE
            emptyMessage.visibility = View.GONE
        }
        Log.e("Checking","LoadData Count is $count")
    }
    fun savedata(x:String){
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
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

    fun nothing(view: View) {}
}