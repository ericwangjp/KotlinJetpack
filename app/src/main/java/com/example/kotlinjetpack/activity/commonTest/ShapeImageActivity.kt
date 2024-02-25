package com.example.kotlinjetpack.activity.commonTest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.databinding.ActivityShapeImageBinding

class ShapeImageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShapeImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShapeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}