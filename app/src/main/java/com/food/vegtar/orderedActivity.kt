package com.food.vegtar

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

class orderedActivity : AppCompatActivity() {
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

        val bill = findViewById<TextView>(R.id.bill1)
        val deliveryCharges = findViewById<TextView>(R.id.deliveryCharges1)
        val total1 = findViewById<TextView>(R.id.total1)
        val address = findViewById<EditText>(R.id.address)
        val status = findViewById<TextView>(R.id.Status)
        val itemName = findViewById<TextView>(R.id.itemName)

        deliveryCharges.text = "₹$Delivery"
        total1.text = "₹${Amount+Delivery}"
        status.text = Status
        address.setText(Address.toString())

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