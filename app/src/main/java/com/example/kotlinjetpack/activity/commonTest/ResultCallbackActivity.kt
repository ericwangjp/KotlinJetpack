package com.example.kotlinjetpack.activity.commonTest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.kotlinjetpack.databinding.ActivityResultCallbackBinding

class ResultCallbackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultCallbackBinding
    private val callbackValue = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK){
            binding.tvResultTwo.text = "新方式：${it.data?.getStringExtra("result")}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultCallbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btnCallbackOne.setOnClickListener {
            val intent = Intent(this, DianZanActivity::class.java)
            startActivityForResult(intent, 100)
        }
        binding.btnCallbackTwo.setOnClickListener {
            val intent = Intent(this, DianZanActivity::class.java)
            callbackValue.launch(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            binding.tvResultOne.text = "旧方式：${ data?.getStringExtra("result") }"
        }
    }
}