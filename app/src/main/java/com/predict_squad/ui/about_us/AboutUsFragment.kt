package com.predict_squad.ui.about_us

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.predict_squad.MainActivity
import com.predict_squad.common_helper.AppWebViewClients
import com.predict_squad.common_helper.OnCurrentFragmentVisibleListener
import com.predict_squad.R
import com.predict_squad.databinding.FragmentAboutUsBinding

class AboutUsFragment : Fragment() {

    private var callback: OnCurrentFragmentVisibleListener? = null

    private var mBinding: FragmentAboutUsBinding? = null
    private val binding get() = mBinding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAboutUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callback?.onSetToolbarTitle(true, AboutUsFragment::class.java.simpleName,"")

    }

    fun setOnCurrentFragmentVisibleListener(activity: MainActivity) {
        callback = activity
    }

    override fun onResume() {
        super.onResume()
        setWebViewData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setWebViewData() {
        val url = getString(R.string.about_us_url)
        mBinding?.webView?.settings?.javaScriptEnabled = true
        mBinding?.webView?.webViewClient = AppWebViewClients(mBinding?.clProgressBar?.clProgressBarParent!!)
        mBinding?.webView?.loadUrl(url)
    }
}