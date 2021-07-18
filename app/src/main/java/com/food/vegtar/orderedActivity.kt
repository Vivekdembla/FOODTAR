package com.food.vegtar

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class orderedActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        setContentView(R.layout.activity_ordered)

//        intent.putExtra("ShopName",message?.Name)
//        intent.putExtra("Amount",message?.amount)
//        intent.putExtra("Delivery",message?.deliveryCharges)
//        intent.putExtra("Status",message?.status)
//        intent.putExtra("Address",message?.Address

        val Amount = intent.getDoubleExtra("Amount",0.0)
        val Delivery = intent.getIntExtra("Delivery",0)
        val Status = intent.getStringExtra("Status")
        val Address = intent.getStringExtra("Address")
        val Message = intent.getStringExtra("Message")
        val specialMessage = intent.getStringExtra("specialMessage")
        val ShopName = intent.getStringExtra("ShopName")
        val Pnumber = intent.getStringExtra("number")
        val image = intent.getStringExtra("image")

        val bill = findViewById<TextView>(R.id.bill1)
        val deliveryCharges = findViewById<TextView>(R.id.deliveryCharges1)
        val total1 = findViewById<TextView>(R.id.total1)
        val address = findViewById<TextView>(R.id.address)
        val status = findViewById<TextView>(R.id.Status)
        val itemName = findViewById<TextView>(R.id.itemName)
        val msg = findViewById<TextView>(R.id.message2)
        val shop = findViewById<TextView>(R.id.shop)
        val photo = findViewById<ImageView>(R.id.photo)
        val call_button = findViewById<Button>(R.id.call_Button)

        deliveryCharges.text = "₹$Delivery"
        total1.text = "₹${Amount+Delivery}"
        status.text = Status
        address.text = Address.toString()
        msg.text = specialMessage.toString()
        shop.text = ShopName
        Glide.with(photo).
        load(image).apply(RequestOptions.circleCropTransform()).
        into(photo)

        call_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:$Pnumber")
            startActivity(intent)
        }

        var b=0
        var food:String=""
        var amount:String=""
        for(i in 0..Message!!.length-1){
            if(Message[i]=='='){
                var a=""
                for(j in b..i-1){
                    a+=Message[j]
                }
                food += a + "\n"
                b=i+1
            }
            else if(Message[i]==','){
                var x=""
                for(j in b..i-1){
                    x+=Message[j]
                }
                amount += "₹"+x + "\n"
                b=i+1
            }
        }
        itemName.text=food
        bill.text=amount
    }



}