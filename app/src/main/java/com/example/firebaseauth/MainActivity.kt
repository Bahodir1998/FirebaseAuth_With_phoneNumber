package com.example.firebaseauth

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.firebaseauth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var controller: NavController
    private var isOn = false
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        controller = NavController(this)
        val backStackCount = controller.backStack.count()
        if (backStackCount == 0) {
            if (isOn) {
                super.onBackPressed()
            } else {
                isOn = true
                Toast.makeText(this, "Dasturdan chiqish uchun 2 marta bosing", Toast.LENGTH_SHORT)
                    .show()
            }
            handler.postDelayed({
                isOn = false
            }, 2000)
        } else {
            super.onBackPressed()
        }

    }


}