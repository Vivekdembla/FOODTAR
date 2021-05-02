package com.food.vegtar

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_detail)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        val shopid = intent.getStringExtra("shopid")
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
        dialog.show()


        viewHolder.cartButton.setOnClickListener{
//            val userDetail=auth.currentUser
//            val messageDao = MessageDao()
//            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//            val message = Message("${userDetail.uid}$timeStamp","Garlic Bread","Amandeep","Dairy Mohalla","$timeStamp","ordered",shopid.toString())
//            messageDao.addMessage(message)
            Log.e("Checking","Cart Button Clicked")
            val intent = Intent(this,CartActivity::class.java)
            intent.putExtra("ImageUrl",foodListArray[position].EachFoodUrl)
            intent.putExtra("Name",foodListArray[position].EachFoodName)
            intent.putExtra("Price",foodListArray[position].EachFoodPrice)
            intent.putExtra("Description", foodListArray[position].EachFoodDes)
            intent.putExtra("Shopname",shop.NameOfShop)
            intent.putExtra("ShopImage",shop.shopImage)
            startActivity(intent)

        }




//       intent.putExtra("FoodName", foodListArray[position].EachFoodName)
//       intent.putExtra("FoodPrice", foodListArray[position].EachFoodPrice)
//       intent.putExtra("FoodDes", foodListArray[position].EachFoodDes)
//       intent.putExtra("FoodImage", foodListArray[position].EachFoodUrl)
//        startActivity(intent)
    }

}
