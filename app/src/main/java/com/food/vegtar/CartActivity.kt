package com.food.vegtar

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.food.vegtar.models.OrderDetail
import com.google.android.gms.ads.*
//import com.google.android.gms.ads.interstitial.InterstitialAd
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.cartlist.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.math.RoundingMode
import java.text.DecimalFormat


class CartActivity : AppCompatActivity(), ICartAdapter {
    var SHARED_PRE="sharedpreference"
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
//    private var mInterstitialAd: InterstitialAd? = null
    var a=0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_cart)


        /////////////////////////////
//        loadAd()
        /////////////////////////////


        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        val sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        var delivery = sharedPreferences.getInt("delivery",0)
        viewModel=ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        ).get(CartViewModel::class.java)
        adapter = CartAdapter(this,delivery,viewModel)
        cartRecyclerView.adapter = adapter
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        nothing = findViewById(R.id.nothing)
        orderNow = findViewById(R.id.orderNow)
        progressBar = findViewById(R.id.progressBar3)
        emptyMessage = findViewById(R.id.emptyMessage)
        ShopClosed = findViewById(R.id.timeOver)
        minimumOrder = findViewById(R.id.minimumOrder)
        totalPrice = findViewById(R.id.totalPrice)
        loadData()


        val imageUrl = intent.getStringExtra("ImageUrl")
        val name = intent.getStringExtra("Name")
        val price =  intent.getStringExtra("Price")
        val description = intent.getStringExtra("Description")
        val shopName = intent.getStringExtra("Shopname")

        viewModel.allCart.observe(this, androidx.lifecycle.Observer { list ->
            list?.let {
                adapter.updateList(it)
                cartItem = list
                refreshView()
            }
        })


        orderNow.setOnClickListener {

//            if (mInterstitialAd != null) {
//                mInterstitialAd?.show(this@CartActivity)
//            } else {
//                Log.d("TAG", "The interstitial ad wasn't ready yet.")
//            }


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
            if(shopName!=firstshop){
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
                        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("open",intent.getStringExtra("open")) //
                        editor.putString("close",intent.getStringExtra("close")) //
                        editor.putString("shopId",intent.getStringExtra("shopId")) //
                        editor.putString("ShopImage",intent.getStringExtra("ShopImage")) //
                        editor.putString("name",intent.getStringExtra("name")) //
                        editor.putString("phone",intent.getStringExtra("phone")) //
                        editor.putInt("Minimum",intent.getIntExtra("Minimum",50)) //
                        editor.putInt("delivery",intent.getIntExtra("delivery",0)) //
                        editor.apply()

                    dialog.dismiss()
                    cartRecyclerView.visibility=View.VISIBLE
                    totalPrice.visibility = View.VISIBLE
                    emptyMessage.visibility = View.GONE
                    viewModel.deleteAll()
                    val x = OrderDetail(name!!, price, imageUrl, description, shopName)
                    GlobalScope.launch(Dispatchers.Main) {
                        viewModel.insertCart(x)
                    }
                    savedata(shopName)
                    loadData()
                }
            }
            else{
                val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("open",intent.getStringExtra("open")) //
                editor.putString("close",intent.getStringExtra("close")) //
                editor.putString("shopId",intent.getStringExtra("shopId")) //
                editor.putString("ShopImage",intent.getStringExtra("ShopImage")) //
                editor.putString("name",intent.getStringExtra("name")) //
                editor.putString("phone",intent.getStringExtra("phone")) //
                editor.putInt("Minimum",intent.getIntExtra("Minimum",50)) //
                editor.putInt("delivery",intent.getIntExtra("delivery",0)) //
                editor.apply()
                val x = OrderDetail(name!!, price, imageUrl, description, shopName)
                viewModel.insertCart(x)
                savedata(shopName)
            }
        }
        refreshView()
    }

    fun refreshView(){
        minimumOrder.visibility = View.GONE
        cartRecyclerView.visibility = View.GONE
        totalPrice.visibility = View.GONE
        ShopClosed.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        emptyMessage.visibility = View.GONE
        orderNow.visibility = View.INVISIBLE
        updatePrice()
        val sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        val delivery = sharedPreferences.getInt("delivery",0)
        val minimum = sharedPreferences.getInt("Minimum", 0)
        val q= adapter.itemCount
        Log.e("Checking","q is ${adapter.itemCount}")
        if(q>=2){
            cartRecyclerView.visibility = View.VISIBLE
            if(a+delivery>=minimum){
                    getMyData()
            }
            else{
                minimumOrder.text = "Minimum order should be atleast ₹${minimum}"
                minimumOrder.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
        else{
            emptyMessage.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }

    }

    fun getMyData():String {
        nothing.visibility = View.VISIBLE
        orderNow.visibility = View.INVISIBLE
        ShopClosed.visibility= View.GONE
        minimumOrder.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        updatePrice()
        val sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        val delivery = sharedPreferences.getInt("delivery",0)
        val minimum = sharedPreferences.getInt("Minimum", 0)
        var p = 0.0
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

                    if (p > open && p < close) {
                        orderNow.visibility = View.VISIBLE
                    } else {
                        ShopClosed.visibility = View.VISIBLE
                    }
                    totalPrice.visibility = View.VISIBLE
                    cartRecyclerView.visibility=View.VISIBLE
                progressBar.visibility = View.GONE
                nothing.visibility = View.GONE
                if((a+delivery)<minimum){
                    orderNow.visibility = View.GONE
                    totalPrice.visibility = View.GONE
                }
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
        nothing.visibility = View.GONE
    }

    override fun onMinusClick(position: Int, num: Int, view: View) {
        nothing.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        Log.e("Checking", "Num is $num")
        if(num==0){
            GlobalScope.launch(Dispatchers.IO) {
                viewModel.deleteCart(adapter.cartItem[position])
            }
        }
        else {
            val x = OrderDetail(adapter.cartItem[position].EachFoodName, adapter.cartItem[position].EachFoodPrice,
                adapter.cartItem[position].EachFoodUrl, adapter.cartItem[position].EachFoodDes,
                adapter.cartItem[position].EachShopName,num)
            viewModel.updateCart(x)
        }
        nothing.visibility = View.GONE
    }

    override fun onQuantityClick(position: Int, value: String, view: View) {
        Toast.makeText(applicationContext, "Quantity Changed", Toast.LENGTH_SHORT).show()
        GlobalScope.launch (Dispatchers.IO){
            val ab: Int = Integer.valueOf(value)
            if (ab != 0) {
                updatefunction(ab, position)
            } else {
                val x = OrderDetail(
                    adapter.cartItem[position].EachFoodName,
                    adapter.cartItem[position].EachFoodPrice,
                    adapter.cartItem[position].EachFoodUrl,
                    adapter.cartItem[position].EachFoodDes,
                    adapter.cartItem[position].EachShopName,
                    adapter.cartItem[position].quantity
                )
                viewModel.deleteCart(x)
            }
            try {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: Exception) {

            }
        }
    }
    fun updatefunction(ab:Int,position:Int){
        val name = adapter.cartItem[position].EachFoodName
            val x = OrderDetail(
                name,
                adapter.cartItem[position].EachFoodPrice.toString(),
                adapter.cartItem[position].EachFoodUrl.toString(),
                adapter.cartItem[position].EachFoodDes.toString(),
                adapter.cartItem[position].EachShopName.toString(),
                ab
            )
            viewModel.updateCart(x)
    }

    private fun updatePrice(){
        val totalNumber = adapter.cartItem.size-1
        a=0.0
        for( i in 0..totalNumber){
            a += adapter.cartItem[i].EachFoodPrice!!.toDouble()*adapter.cartItem[i].quantity!!
        }
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING

        a = df.format(a).toDouble()
        val sharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        val delivery = sharedPreferences.getInt("delivery",0)
        totalPrice.text = "₹${a+delivery}"
    }
    fun savedata(x:String){
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString("last", x)
        editor.apply()
    }
    fun loadData(){
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PRE, MODE_PRIVATE)
        firstshop = sharedPreferences.getString("last", "")!!
    }

    override fun onPause() {
        super.onPause()
        savedata(firstshop)
    }

    fun nothing(view: View) {}
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
//                    mInterstitialAd?.show(this@CartActivity)
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