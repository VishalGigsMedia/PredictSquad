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
import com.predict_squad.MainActivity
import com.predict_squad.R
import com.predict_squad.common_helper.BundleKey
import com.predict_squad.common_helper.OnCurrentFragmentVisibleListener
import com.predict_squad.databinding.FragmentHomeBinding

class MatchDetailsParent : Fragment() {

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
        val bundle = arguments
        if (bundle != null) {
            val matchId = bundle.getString(BundleKey.MatchId.toString()).toString()
            val matchType = bundle.getString(BundleKey.MatchType.toString()).toString()
            val toolbarTitle = bundle.getString(BundleKey.ToolbarTitle.toString()).toString()

            callback?.onSetToolbarTitle(true, MatchDetailsParent::class.java.simpleName, toolbarTitle)
            setAdapter(matchId, matchType)
        }


    }

    fun setOnCurrentFragmentVisibleListener(activity: MainActivity) {
        callback = activity
    }

    private fun setAdapter(matchId: String, matchType: String) {

        mBinding?.viewPager?.offscreenPageLimit = 1
        val adapter = ViewPagerAdapter(childFragmentManager, context as FragmentActivity, matchId, matchType)
        mBinding?.viewPager?.adapter = adapter
        mBinding?.tabLayout?.setupWithViewPager(mBinding?.viewPager)
        mBinding?.viewPager?.currentItem = 0
        adapter.notifyDataSetChanged()
    }


    class ViewPagerAdapter(fragmentManager: FragmentManager, val context: Context, private val matchId: String, val matchType: String) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = MatchDetailFragment(matchId, matchType)
                1 -> fragment = TeamDetailFragment(matchId, matchType)
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            var title: String? = null
            when (position) {
                0 -> title = context.getString(R.string.match_details)
                1 -> title = context.getString(R.string.team_details)
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