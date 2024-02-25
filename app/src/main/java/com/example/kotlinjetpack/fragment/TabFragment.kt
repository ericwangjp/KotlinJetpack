package com.example.kotlinjetpack.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.activity.*
import com.example.kotlinjetpack.activity.flowCoroutine.FlowLearnActivity
import com.example.kotlinjetpack.activity.nestScroll.NestedScrollParentBehaviorHomeActivity
import com.example.kotlinjetpack.databinding.FragmentTabBinding
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TabFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentTabBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTabBinding.inflate(layoutInflater, container, false)
        binding.tvTabFragment.text = param1
        binding.btnNestedScroll.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    NestedScrollParentBehaviorHomeActivity::class.java
                )
            )
        }
        binding.btnCommon.setOnClickListener {
            startActivity(Intent(requireContext(), CommonTestListOneActivity::class.java))
        }
        binding.btnCommonTestOne.setOnClickListener {
            startActivity(Intent(requireContext(), CommonTestListTwoActivity::class.java))
        }
        binding.btnFlowLearn.setOnClickListener {
            startActivity(Intent(requireContext(), FlowLearnListActivity::class.java))
        }
        binding.btnCustomView.setOnClickListener {
            startActivity(Intent(requireContext(), CustomViewListActivity::class.java))
        }
        setBadgeView()
        initData()
        return binding.root
    }

    private fun setBadgeView() {
        // 在视图树变化
        binding.tvTabFragment.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            @SuppressLint("UnsafeOptInUsageError")
            override fun onGlobalLayout() {
                BadgeDrawable.create(requireContext()).apply {
                    badgeGravity = BadgeDrawable.TOP_END
                    number = 6
                    backgroundColor = ContextCompat.getColor(requireContext(), R.color.purple_200)
                    isVisible = true
                    BadgeUtils.attachBadgeDrawable(this, binding.tvTabFragment)
                }
                binding.tvTabFragment.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun initData() {
        val uri: Uri =
            Uri.parse("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F1114%2F113020142315%2F201130142315-1-1200.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655369304&t=f27501d87333909271d1113c725a11af")
//            Uri.parse("https://aa.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F1114%2F113020142315%2F201130142315-1-1200.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655369304&t=f27501d87333909271d1113c725a11af")
        binding.sdvImg.postDelayed(2000) {
            binding.sdvImg.setImageURI(uri, this)
        }
        val controller = Fresco.newDraweeControllerBuilder().apply {
            this.setUri(uri)
            tapToRetryEnabled = true
            oldController = binding.sdvImg.controller
        }.build()
        binding.sdvImg.controller = controller
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TabFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = TabFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}