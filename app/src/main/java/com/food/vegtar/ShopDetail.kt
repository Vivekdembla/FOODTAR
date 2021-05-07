package com.food.vegtar

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.food.vegtar.Dao.MessageDao
import com.food.vegtar.Dao.ShopDao
import com.food.vegtar.Dao.userDao
import com.food.vegtar.models.FoodDetail
import com.food.vegtar.models.Message
import com.food.vegtar.models.Shop
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShopDetail : AppCompatActivity(), IFoodAdapter {
    var SHARED_PRE="sharedpreference"
    var position:Int?=null
    lateinit var adapter: FoodAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var foodListArray:ArrayList<FoodDetail>
    lateinit var dialog:Dialog
    private lateinit var auth: FirebaseAuth
    private lateinit var view: View
    lateinit var viewHolder: PopUpViewHolder
    lateinit var shop:Shop
    var shopid:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_detail)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        shopid = intent.getStringExtra("shopid")
        val shopDao = ShopDao()
        foodListArray = ArrayList()
        recyclerView = findViewById(R.id.recyclerView2)
        auth = Firebase.auth


        adapter = FoodAdapter(this)
        recyclerView.adapter=adapter
        recyclerView.layoutManager=LinearLayoutManager(this)
        dialog = BottomSheetDialog(this)
        view = layoutInflater.inflate(R.layout.dialog_layout, null)

        viewHolder = PopUpViewHolder(view)

        dialog.setContentView(view)



        GlobalScope.launch(Dispatchers.IO) {
            shop = shopDao.getFoodById(shopid!!).await().toObject(Shop::class.java)!!
            val number = shop.ListOfFood!!.size
            Log.e("Check","Number is ${number}")
            Log.e("Check","ListOfFood!![0] is ${shop.ListOfFood!![0]}")
            for (i in 0 ..number-1){
                Log.e("Check","i is ${i}")
                val x=FoodDetail("","","","")
                x.EachFoodName=shop.ListOfFood!![i]
                x.EachFoodUrl=shop.FoodImageUrl!![i]
                x.EachFoodPrice= shop.Price!![i]
                x.EachFoodDes= shop.Description!![i]
                foodListArray.add(x)
                Log.e("Check","foodListArray[i].EachFoodName is ${foodListArray[i].EachFoodName}")
            }
            withContext(Dispatchers.Main) {
                adapter.updateList(foodListArray)
            }

        }

    }


    private fun getMyData():Double {
        var p:Double=0.0
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.timezonedb.com/")
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
                val x = "${myStringBuilder.toString()[11]}${myStringBuilder.toString()[12]}" +
                        ".${myStringBuilder.toString()[14]}"+"${myStringBuilder.toString()[15]}"
                p =  x.toDouble()
                Log.e("Checking", "$p")
                if(p>shop.OpeningTime!!&&p<shop.ClosingTime!!) {
                    view.findViewById<Button>(R.id.cartButton).visibility = View.VISIBLE

                }
                else{
                    view.findViewById<TextView>(R.id.closed).visibility = View.VISIBLE
                }
                view.findViewById<ProgressBar>(R.id.progressBar2).visibility = View.GONE
            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                Toast.makeText(this@ShopDetail,"Try Again. Error -> $t",Toast.LENGTH_SHORT).show()
            }
        })
        return p
    }

    class PopUpViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val one = itemView.findViewById<TextView>(R.id.one)
        val cartButton = itemView.findViewById<Button>(R.id.cartButton)
        val orderImage = itemView.findViewById<ImageView>(R.id.orderImage)
        val price = itemView.findViewById<TextView>(R.id.price)
        val description = itemView.findViewById<TextView>(R.id.Description)
    }

    override fun onClickFood(position: Int) {
        this.position = position
        Log.e("Check","Name is = ${foodListArray[position].EachFoodName}")
        view = layoutInflater.inflate(R.layout.dialog_layout, null)
        viewHolder = PopUpViewHolder(view)
        viewHolder.one.text = foodListArray[position].EachFoodName.toString()
        Glide.with(viewHolder.orderImage.context ).load(foodListArray[position].EachFoodUrl).into(viewHolder.orderImage)
        viewHolder.price.text = foodListArray[position].EachFoodPrice.toString()
        viewHolder.description.text = foodListArray[position].EachFoodDes.toString()
        dialog.setContentView(view)
        getMyData()
        dialog.show()


        viewHolder.cartButton.setOnClickListener{
                Log.e("Checking", "Cart Button Clicked")
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("ImageUrl", foodListArray[position].EachFoodUrl)
                intent.putExtra("Name", foodListArray[position].EachFoodName)
                intent.putExtra("Price", foodListArray[position].EachFoodPrice)
                intent.putExtra("Description", foodListArray[position].EachFoodDes)
                intent.putExtra("Shopname", shop.NameOfShop)
                intent.putExtra("ShopImage", shop.shopImage)
                val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("open",shop.OpeningTime.toString())
                editor.putString("close",shop.ClosingTime.toString())
                editor.putString("shopId",shopid)
                editor.putString("ShopImage",shop.shopImage.toString())
                editor.putString("name",shop.NameOfShop)
                editor.putInt("Minimum",shop.Minimum!!)
                editor.putInt("delivery",shop.Delivery!!)
                editor.apply()
                startActivity(intent)
        }

    }

}
