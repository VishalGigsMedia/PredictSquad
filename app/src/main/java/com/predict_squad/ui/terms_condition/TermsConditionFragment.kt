package com.predict_squad.ui.terms_condition

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.predict_squad.MainActivity
import com.predict_squad.R
import com.predict_squad.common_helper.AppWebViewClients
import com.predict_squad.common_helper.DefaultHelper.isOnline
import com.predict_squad.common_helper.OnCurrentFragmentVisibleListener
import com.predict_squad.databinding.FragmentTermsConditionBinding

class TermsConditionFragment : Fragment() {
    private var callback: OnCurrentFragmentVisibleListener? = null
    private var mBinding: FragmentTermsConditionBinding? = null
    private val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTermsConditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback?.onSetToolbarTitle(true, TermsConditionFragment::class.java.simpleName, "")


    }

    override fun onResume() {
        super.onResume()
        if (isOnline()) {
            setWebViewData()
        } else {
            setNoDataLayout(getString(R.string.no_internet))
        }
    }

    fun setOnCurrentFragmentVisibleListener(activity: MainActivity) {
        callback = activity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewData() {
        val url = getString(R.string.terms_condition_url)
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