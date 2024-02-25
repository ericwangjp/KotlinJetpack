package com.example.kotlinjetpack.activity.linkageScroll

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.databinding.ActivityNestListBinding
import com.example.kotlinjetpack.model.CommonSimpleModel
import com.example.kotlinjetpack.model.GroupModel

class NestListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNestListBinding
    private var dataList: MutableList<CommonSimpleModel> = arrayListOf()
    private var groupDataList: MutableList<GroupModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNestListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        for (i in 0..15) {
            dataList.add(CommonSimpleModel(i, "标题：$i"))
        }
        for (i in 0..15) {
            groupDataList.add(GroupModel(i, "标题：$i"))
        }
//        简单列表
//        binding.tvCategoryList.linear().setup {
//            addType<CommonSimpleModel>(R.layout.common_rcv_item)
//            onCreate {
//                getBinding<CommonRcvItemBinding>().tvContent.text = "初始值"
//            }
//
//            onBind {
//                getBinding<CommonRcvItemBinding>().tvContent.text = getModel<CommonSimpleModel>().title
//            }
//        }.models = dataList

//        嵌套列表
        binding.tvCategoryList.linear().setup {
            addType<GroupModel>(R.layout.group_rcv_item)
            addType<CommonSimpleModel>(R.layout.common_rcv_item)

            singleExpandMode = true
            onExpand {
//                子布局的最大位置（包含父位置）
                Log.e("展开位置: ", "1-->${this.layoutPosition} - $it")
//                父位置
                Log.e("展开位置: ", "22-->${this.bindingAdapterPosition} - $it")
            }

            onBind {
                binding.btnOne.setOnClickListener {
                    this.expand(true)
                }
                binding.btnTwo.setOnClickListener {
                    this.collapse()
                }
            }

            R.id.item_root.onFastClick {
                when (itemViewType) {
                    R.layout.group_rcv_item -> {
                        expandOrCollapse() // 展开或者折叠
                    }
                    R.layout.common_rcv_item -> {
                        val parentPosition = findParentPosition()
                        Log.e("展开位置2: ", "2-->${parentPosition}")
                        if (parentPosition != -1) {
                            val childModel = getModel<GroupModel>(parentPosition)
//                            在所有item中的实际位置（包含父位置）
                            Log.e("展开位置3: ", "3-->${layoutPosition}")
                            this.itemView.isSelected = true
                        }

                    }
                }
            }
        }.models = groupDataList
    }
}