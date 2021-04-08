package com.predict_squad.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.predict_squad.MainActivity
import com.predict_squad.common_helper.OnCurrentFragmentVisibleListener
import com.predict_squad.R
import com.predict_squad.databinding.FragmentHomeBinding

class HomeFragment() : Fragment() {

    private var mBinding: FragmentHomeBinding? = null
    private val binding get() = mBinding!!
    private var callback: OnCurrentFragmentVisibleListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback?.onSetToolbarTitle(true, HomeFragment::class.java.simpleName)
        setAdapter()
    }

    fun setOnCurrentFragmentVisibleListener(activity: MainActivity) {
        callback = activity
    }

    private fun setAdapter() {

        mBinding?.viewPager?.offscreenPageLimit = 1
        val adapter = ViewPagerAdapter(childFragmentManager, context as FragmentActivity)
        mBinding?.viewPager?.adapter = adapter
        mBinding?.tabLayout?.setupWithViewPager(mBinding?.viewPager)
        mBinding?.viewPager?.currentItem = 0


        mBinding?.viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })
        adapter.notifyDataSetChanged()
    }


    class ViewPagerAdapter(fragmentManager: FragmentManager, val context: Context) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = CricketMatchListFragment()
                1 -> fragment = FootballMatchListFragment()
                2 -> fragment = BasketballMatchListFragment()

            }
            return fragment!!
            // return MatchListFragment()

        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            var title: String? = null
            when (position) {
                0 -> title = context.getString(R.string.toolbar_cricket)
                1 -> title = context.getString(R.string.toolbar_football)
                2 -> title = context.getString(R.string.toolbar_basketball)
            }
            return title
        }

        override fun getItemPosition(`object`: Any): Int {
            return super.getItemPosition(`object`)

        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

    }

}