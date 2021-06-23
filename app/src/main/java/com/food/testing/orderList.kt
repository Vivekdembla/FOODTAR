package com.food.testing

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.food.testing.Dao.MessageDao
import com.food.testing.models.Message
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class orderList : AppCompatActivity(), IOrderAdapter {
    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : orderAdapter
    private var mInterstitialAd: InterstitialAd? = null
    lateinit var mAdView2 : AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_order_list)
        loadAd()
        recyclerView = findViewById(R.id.orderList)
        progressBar = findViewById(R.id.progressBar4)
        progressBar.visibility = View.VISIBLE
        setRecyclerView()

        //////////////////////
        MobileAds.initialize(this) {}

        mAdView2 = findViewById(R.id.adView2)
        val adRequest = AdRequest.Builder().build()
        mAdView2.loadAd(adRequest)
        //////////////////////
    }
    fun setRecyclerView(){
        val auth = Firebase.auth
        val uid = auth.currentUser.uid
        Log.e("Checking","Uid is $uid")
        val messageDao = MessageDao()
        val Collection=messageDao.userCollection2
        val messageCollection = Collection.document(uid).collection("Message")
        val query = messageCollection.orderBy("key", Query.Direction.DESCENDING)
        val recyclerViewOption = FirestoreRecyclerOptions.Builder<Message>().setQuery(
                query,
                Message::class.java
        ).build()
        adapter = orderAdapter(recyclerViewOption,this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        progressBar.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onOrderClicked(orderid: String) {
        val intent = Intent(this,orderedActivity::class.java)
        val messageDao = MessageDao()
        var uid = Firebase.auth.currentUser.uid
        GlobalScope.launch(Dispatchers.IO) {
            val message = messageDao.getUserById(orderid,uid).await().toObject(Message::class.java)
            intent.putExtra("ShopName",message?.shopname)
            intent.putExtra("Amount",message?.amount)
            intent.putExtra("Delivery",message?.deliveryCharges)
            intent.putExtra("Status",message?.status)
            intent.putExtra("Address",message?.address)
            intent.putExtra("Message",message?.message)
            intent.putExtra("specialMessage",message?.specialMessage)
            intent.putExtra("number",message?.number)
            intent.putExtra("image",message?.image)
            startActivity(intent)
        }
    }

    fun loadAd(){
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,getString(R.string.Interstitial), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("Checking", adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d("Checking", "Ad was loaded.")
                mInterstitialAd = interstitialAd

                ///////////////
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this@orderList)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                ///////////////
            }
        })
        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("Checking", "Ad was dismissed.")
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                Log.d("Checking", "Ad failed to show.")
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("TAG", "Ad showed fullscreen content.")
                mInterstitialAd = null;
            }
        }
    }

}