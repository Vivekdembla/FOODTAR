package com.food.vegtar

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.food.vegtar.Dao.ShopDao
import com.food.vegtar.models.FoodDetail
import com.food.vegtar.models.Shop
import com.google.android.gms.ads.*
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
import java.util.*
import kotlin.collections.ArrayList

class ShopDetail : AppCompatActivity(), IFoodAdapter {
//    private var mInterstitialAd: InterstitialAd? = null
    lateinit var progressBar:ProgressBar
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
//    lateinit var mAdView : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop_detail)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        /////////////////////////////////
//        loadAd()
//        MobileAds.initialize(this) {}
//
//        mAdView = findViewById(R.id.adView)
//        val adRequest = AdRequest.Builder().build()
//        mAdView.loadAd(adRequest)
        /////////////////////////////////

        shopid = intent.getStringExtra("shopid")

        progressBar = findViewById(R.id.pBar)
        progressBar.visibility=View.VISIBLE
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

        val searchBar = findViewById<SearchView>(R.id.searchBar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.updateList(foodListArray)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return false
            }
        })

        addFoodData()

    }
    fun addFoodData(){
        val shopDao = ShopDao()
        GlobalScope.launch(Dispatchers.IO) {
            shop = shopDao.getFoodById(shopid!!).await().toObject(Shop::class.java)!!
            val number = shop.ListOfFood!!.size
            for (i in 0 ..number-1){
                Log.e("Check","i is ${i}")
                val x=FoodDetail("","","","")
                x.EachFoodName=shop.ListOfFood!![i]
                x.EachFoodUrl=shop.FoodImageUrl!![i]
                x.EachFoodPrice= shop.Price!![i]
                x.EachFoodDes= shop.Description!![i]
                foodListArray.add(x)
            }
            withContext(Dispatchers.Main) {
                adapter.updateList(foodListArray)
                progressBar.visibility = View.GONE
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
        Glide.with(viewHolder.orderImage.context )
            .load(foodListArray[position].EachFoodUrl)
            .transform(CenterCrop(), RoundedCorners(25))
            .into(viewHolder.orderImage)
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
                intent.putExtra("phone", shop.phone)

                intent.putExtra("open",shop.OpeningTime.toString())
                intent.putExtra("close",shop.ClosingTime.toString())
                intent.putExtra("shopId",shopid)
                intent.putExtra("ShopImage",shop.shopImage.toString())
                intent.putExtra("name",shop.NameOfShop)
                intent.putExtra("Minimum",shop.Minimum!!)
                intent.putExtra("delivery",shop.Delivery!!)

//                val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
//                val editor: SharedPreferences.Editor = sharedPreferences.edit()
//                editor.putString("open",shop.OpeningTime.toString()) //
//                editor.putString("close",shop.ClosingTime.toString()) //
//                editor.putString("shopId",shopid) //
//                editor.putString("ShopImage",shop.shopImage.toString()) //
//                editor.putString("name",shop.NameOfShop) //
//                editor.putInt("Minimum",shop.Minimum!!) //
//                editor.putInt("delivery",shop.Delivery!!) //
//                editor.apply()
                startActivity(intent)
        }

    }

//    fun loadAd(){
//        var adRequest = AdRequest.Builder().build()
//
//        InterstitialAd.load(this,getString(R.string.Interstitial), adRequest, object : InterstitialAdLoadCallback() {
//            override fun onAdFailedToLoad(adError: LoadAdError) {
//                Log.d("Checking", adError?.message)
//                mInterstitialAd = null
//            }
//
//            override fun onAdLoaded(interstitialAd: InterstitialAd) {
//                Log.d("Checking", "Ad was loaded.")
//                mInterstitialAd = interstitialAd
//
//                ///////////////
//                if (mInterstitialAd != null) {
//                    mInterstitialAd?.show(this@ShopDetail)
//                } else {
//                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
//                }
//                ///////////////
//            }
//        })
//        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
//            override fun onAdDismissedFullScreenContent() {
//                Log.d("Checking", "Ad was dismissed.")
//            }
//
//            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
//                Log.d("Checking", "Ad failed to show.")
//            }
//
//            override fun onAdShowedFullScreenContent() {
//                Log.d("TAG", "Ad showed fullscreen content.")
//                mInterstitialAd = null;
//            }
//        }
//    }
}
