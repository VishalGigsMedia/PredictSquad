package com.predict_squad.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.predict_squad.common_helper.Application
import com.predict_squad.common_helper.BundleKey
import com.predict_squad.common_helper.ConstantHelper
import com.predict_squad.common_helper.DefaultHelper
import com.predict_squad.common_helper.DefaultHelper.decrypt
import com.predict_squad.common_helper.DefaultHelper.forceLogout
import com.predict_squad.common_helper.DefaultHelper.isOnline
import com.predict_squad.common_helper.DefaultHelper.showToast
import com.predict_squad.retrofit.APIService
import com.predict_squad.ui.home.adapter.CricketMatchListAdapter
import com.predict_squad.ui.home.model.MatchListModel
import com.predict_squad.ui.home.view_model.MatchListViewModel
import com.predict_squad.R
import com.predict_squad.databinding.FragmentMatchListBinding
import javax.inject.Inject

class CricketMatchListFragment : Fragment(), CricketMatchListAdapter.MatchListClickListener {
    @Inject
    lateinit var apiService: APIService

    private lateinit var layoutManager: LinearLayoutManager
    private var list: ArrayList<MatchListModel.Data.Match> = ArrayList()
    private var adapter: CricketMatchListAdapter? = null

    private var matchListClickListener: CricketMatchListAdapter.MatchListClickListener? = null

    private var offset = 0
    private var nextLimit = 20
    private var isLastPage: Boolean = false
    private var isLoading: Boolean = false

    private var fcmToken: String = ""

    private lateinit var viewModel: MatchListViewModel
    private var mBinding: FragmentMatchListBinding? = null
    private val binding get() = mBinding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        Application.instance?.getComponent()?.inject(this)
        viewModel = ViewModelProvider(this).get(MatchListViewModel::class.java)
        mBinding = FragmentMatchListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    @SuppressLint("SimpleDateFormat")
    private fun init() {
        this.matchListClickListener = this
        //println("isOnline : " + isOnline())

    }


    private fun swipeToRefresh() {
        mBinding?.srl?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                context as FragmentActivity, R.color.colorPrimary
            )
        )
        mBinding?.srl?.setColorSchemeColors(Color.WHITE)

        mBinding?.srl?.setOnRefreshListener {
            initRefreshData()
        }
    }

    private fun initRefreshData() {
        this.list.clear()
        this.offset = 0
        this.nextLimit = 20
        setAdapter()
        mBinding?.srl?.isRefreshing = false

        if (isOnline()) {
            showShimmerLayout()
            getFcmToken()
            addScrollListener()
        } else {
            hideShimmerLayout()
            setNoDataLayout(getString(R.string.no_internet))
        }

    }

    private fun showShimmerLayout() {
        hideNoDataLayout()
        mBinding?.shimmerFrameLayout?.startShimmer()
        mBinding?.shimmerFrameLayout?.visibility = View.VISIBLE
    }

    private fun hideShimmerLayout() {
        mBinding?.shimmerFrameLayout?.stopShimmer()
        mBinding?.shimmerFrameLayout?.visibility = View.GONE
    }

    private fun setAdapter() {
        layoutManager = LinearLayoutManager(context)
        mBinding?.rvHome?.layoutManager = layoutManager
        adapter = CricketMatchListAdapter(
            context as FragmentActivity, list, matchListClickListener as CricketMatchListFragment
        )
        mBinding?.rvHome?.adapter = adapter
        adapter?.notifyDataSetChanged()
    }


    override fun onMatchClick(id: String, matchType: String) {
        val matchDetailFragment = MatchDetailFragment()
        val bundle = Bundle()
        bundle.putString(BundleKey.MatchId.toString(), id)
        bundle.putString(BundleKey.MatchType.toString(), matchType)
        matchDetailFragment.arguments = bundle
        DefaultHelper.openFragment(context as FragmentActivity as FragmentActivity, matchDetailFragment, true)
    }

    override fun onShowErrorDialog() {
        showMatchDetailNotFound()
    }

    private fun showMatchDetailNotFound() {
        val wrappedContext = ContextThemeWrapper(context, R.style.ThemeOverlay_Demo_BottomSheetDialog)
        val dialog = BottomSheetDialog(wrappedContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.match_detial_not_available_dialog)
        dialog.window?.setGravity(Gravity.BOTTOM)
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.setLayout(width, height)
        val tvDismiss = dialog.findViewById<TextView>(R.id.tvDismiss)
        tvDismiss?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun getFcmToken(): String {
        return Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                // Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result.toString()
            this.fcmToken = token
            //println("fcmToken : $fcmToken")
            getMatchList(fcmToken, offset, nextLimit)

        }).toString()
    }

    private fun getMatchList(fcmToken: String, offset: Int, nextLimit: Int) {
        //showLoader()
        viewModel.getCricketMatchList(
            context as FragmentActivity, apiService, offset, nextLimit, fcmToken
        )?.observe(viewLifecycleOwner, { matchListModel ->
            //hideLoader()
            mBinding?.srl?.isRefreshing = false
            hideShimmerLayout()
            hideNoDataLayout()
            if (matchListModel != null) {

                if (matchListModel.force_logout != ConstantHelper.forceLogout) {
                    forceLogout(context as FragmentActivity, "")
                }

                when (matchListModel.status) {
                    ConstantHelper.success -> {
                        this.offset = 20
                        this.list = matchListModel.data?.match_list as ArrayList<MatchListModel.Data.Match>
                        adapter?.addData(this.list)
                        mBinding?.rvHome?.visibility = View.VISIBLE
                    }
                    ConstantHelper.failed -> {
                        setNoDataLayout(decrypt(matchListModel.message.toString()))
                    }
                    ConstantHelper.authorizationFailed -> {
                        setNoDataLayout(decrypt(matchListModel.message.toString()))
                    }
                    ConstantHelper.apiFailed -> {
                        showToast(context, decrypt(matchListModel.message.toString()))
                    }
                    ConstantHelper.noInternet -> {
                        setNoDataLayout(matchListModel.message.toString())
                    }
                    else -> {
                        setNoDataLayout(decrypt(matchListModel.message.toString()))
                    }
                }

            }
        })
    }


    private fun addScrollListener() {
        mBinding?.rvHome?.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

            override fun loadMoreItems() {
                isLoading = true
                //you have to call load more items to get more data
                getMoreMatchList(fcmToken, offset, nextLimit)
            }
        })
    }

    private fun getMoreMatchList(fcmToken: String, offset: Int, nextLimit: Int) {
        //print("loadMore : Loading More Data !!!.....")
        mBinding?.clProgressBar?.clProgressBarParent?.visibility = View.VISIBLE
        viewModel.getCricketMatchList(
            context as FragmentActivity, apiService, offset, nextLimit, fcmToken
        )?.observe(viewLifecycleOwner, { matchListModel ->
            mBinding?.clProgressBar?.clProgressBarParent?.visibility = View.GONE
            if (matchListModel != null) {
                if (matchListModel.force_logout != ConstantHelper.forceLogout) {
                    forceLogout(context as FragmentActivity as FragmentActivity, "")
                }

                when (matchListModel.status) {
                    ConstantHelper.success -> {
                        isLoading = false
                        this.offset += 20
                        adapter?.addData(matchListModel.data?.match_list as ArrayList<MatchListModel.Data.Match>)
                    }
                    ConstantHelper.failed -> {
                        showToast(context, decrypt(matchListModel.message.toString()))
                    }
                    ConstantHelper.apiFailed -> {
                        showToast(context, decrypt(matchListModel.message.toString()))
                    }
                    ConstantHelper.noInternet -> {
                        showToast(context, matchListModel.message.toString())
                    }
                    else -> {
                        showToast(context, decrypt(matchListModel.message.toString()))
                    }
                }
            }
        })
    }

    private fun setNoDataLayout(msg: String) {
        if (msg.isNotEmpty()) {
            mBinding?.rvHome?.visibility = View.GONE
            mBinding?.clNoData?.tvNoDataLayout?.text = decrypt(msg)
            mBinding?.clNoData?.clNoDataParent?.visibility = View.VISIBLE
        }
    }

    private fun hideNoDataLayout() {
        mBinding?.clNoData?.clNoDataParent?.visibility = View.GONE

    }

    private fun showLoader() {
        mBinding?.clProgressBar?.clProgressBarParent?.visibility = View.VISIBLE
    }

    private fun hideLoader() {
        mBinding?.clProgressBar?.clProgressBarParent?.visibility = View.GONE
    }


    override fun onPause() {
        mBinding?.shimmerFrameLayout?.stopShimmer()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isOnline()) {
            showShimmerLayout()
            swipeToRefresh()
            initRefreshData()
        } else {
            hideShimmerLayout()
            setNoDataLayout(getString(R.string.no_internet))
        }
    }


}