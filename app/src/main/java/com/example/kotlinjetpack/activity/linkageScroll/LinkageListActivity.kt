package com.example.kotlinjetpack.activity.linkageScroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinjetpack.databinding.ActivityLinkageListBinding
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.kunminx.linkage.bean.DefaultGroupedItem

class LinkageListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLinkageListBinding
    private val dataJson: String = """
        [
          {
            "header": "优惠",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "好吃的食物，增肥神器，有求必应",
              "group": "优惠",
              "title": "全家桶"
            }
          },
          {
            "header": "热卖",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "热卖",
              "title": "烤全翅"
            }
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 1999 件",
              "group": "本地",
              "title": "油炸生蚝"
            }
          },
          {
            "header": "套餐",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "好吃的食物，增肥神器，有求必应",
              "group": "套餐",
              "title": "全家桶"
            }
          },
          {
            "header": "饮料",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料",
              "title": "烤全翅"
            }
          },
          {
            "header": "套餐二",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "好吃的食物，增肥神器，有求必应",
              "group": "套餐二",
              "title": "全家桶"
            }
          },
          {
            "header": "饮料二",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料二",
              "title": "烤全翅"
            }
          },
          {
            "header": "饮料三",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料三",
              "title": "烤全翅"
            }
          },
          {
            "header": "饮料四",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料四",
              "title": "烤全翅"
            }
          },
          {
            "header": "饮料五",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料五",
              "title": "烤全翅"
            }
          },
          {
            "header": "饮料六",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料六",
              "title": "烤全翅"
            }
          },
          {
            "header": "饮料七",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料七",
              "title": "烤全翅"
            }
          },
          {
            "header": "饮料八",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料八",
              "title": "烤全翅"
            }
          },
          {
            "header": "饮料⑨",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料⑨",
              "title": "烤全翅"
            }
          },
          {
            "header": "饮料十",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "饮料十",
              "title": "烤全翅"
            }
          },
          {
            "header": "电影",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "电影",
              "title": "烤全翅"
            }
          },
          {
            "header": "外卖",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "外卖",
              "title": "烤全翅"
            }
          },
          {
            "header": "商超",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "商超",
              "title": "烤全翅"
            }
          },
          {
            "header": "美食",
            "isHeader": true
          },
          {
            "isHeader": false,
            "info": {
              "content": "爆款热卖，月销超过 999 件",
              "group": "美食",
              "title": "烤全翅"
            }
          }
        ]
    """.trimIndent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLinkageListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        val itemList: List<DefaultGroupedItem> = Gson().fromJson(
            dataJson, object : TypeToken<List<DefaultGroupedItem>>() {}.type
        )
        binding.linkageList.init(itemList)
    }
}