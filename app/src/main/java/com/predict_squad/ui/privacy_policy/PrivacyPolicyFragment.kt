package com.predict_squad.ui.privacy_policy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.predict_squad.MainActivity
import com.predict_squad.common_helper.AppWebViewClients
import com.predict_squad.common_helper.DefaultHelper.isOnline
import com.predict_squad.common_helper.OnCurrentFragmentVisibleListener
import com.predict_squad.R
import com.predict_squad.databinding.FragmentPrivacyPolicyBinding

class PrivacyPolicyFragment : Fragment() {

    private var callback: OnCurrentFragmentVisibleListener? = null

    private var mBinding: FragmentPrivacyPolicyBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    fun setOnCurrentFragmentVisibleListener(activity: MainActivity) {
        callback = activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback?.onSetToolbarTitle(true, PrivacyPolicyFragment::class.java.simpleName,"")

    }

    override fun onResume() {
        super.onResume()
        if (isOnline()) {
            setWebViewData()
        } else {
            setNoDataLayout(getString(R.string.no_internet))
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewData() {
        val url = getString(R.string.privacy_policy_url)
        mBinding?.webView?.settings?.javaScriptEnabled = true
        mBinding?.webView?.webViewClient = AppWebViewClients(mBinding?.clProgressBar?.clProgressBarParent!!)
        mBinding?.webView?.loadUrl(url)
    }

    private fun setNoDataLayout(msg: String) {
        if (msg.isNotEmpty()) {
            mBinding?.webView?.visibility = View.GONE
            mBinding?.clNoData?.tvNoDataLayout?.text = msg
            mBinding?.clNoData?.clNoDataParent?.visibility = View.VISIBLE
        }
    }
}