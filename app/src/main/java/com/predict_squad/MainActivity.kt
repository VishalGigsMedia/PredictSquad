package com.predict_squad

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.predict_squad.common_helper.Application
import com.predict_squad.common_helper.BundleKey
import com.predict_squad.common_helper.ConstantHelper
import com.predict_squad.common_helper.DefaultHelper.decrypt
import com.predict_squad.common_helper.DefaultHelper.hideKeyboard
import com.predict_squad.common_helper.DefaultHelper.openFragment
import com.predict_squad.common_helper.DefaultHelper.showToast
import com.predict_squad.common_helper.OnCurrentFragmentVisibleListener
import com.predict_squad.databinding.ActivityMainBinding
import com.predict_squad.retrofit.APIService
import com.predict_squad.ui.about_us.AboutUsFragment
import com.predict_squad.ui.home.HomeFragment
import com.predict_squad.ui.home.MatchDetailFragment
import com.predict_squad.ui.home.MatchDetailsParent
import com.predict_squad.ui.privacy_policy.PrivacyPolicyFragment
import com.predict_squad.ui.terms_condition.TermsConditionFragment
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity(), OnCurrentFragmentVisibleListener {
    @Inject
    lateinit var apiService: APIService

    private lateinit var viewModel: UpdateVersionViewModel
    private var mBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding!!.root)
        init()
        backPressManager()
        manageClickEvents()
        notification()
    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(UpdateVersionViewModel::class.java)
        Application.instance?.getComponent()?.inject(this)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        setSupportActionBar(mBinding?.appBarMain?.toolbar)
        mBinding?.appBarMain?.toolbar?.setNavigationIcon(R.drawable.ic_menu_24)
        mBinding?.appBarMain?.fab?.setOnClickListener { view ->
            shareApplication()
        }

        mBinding?.appBarMain?.toolbar?.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                openDrawer()
            }
        }

        val applicationName = mBinding?.navView?.tvAppName?.text.toString() + " : " + getVersionName()
        mBinding?.navView?.tvAppName?.text = applicationName
    }

    private fun notification() {
        val intent = intent
        if (intent != null) {

            when (intent.getStringExtra(BundleKey.NotificationType.toString()).toString()) {
                ConstantHelper.cricketListing -> {
                    showMatchDetails(
                        intent.getStringExtra(BundleKey.MatchId.toString()).toString(), ConstantHelper.cricket
                    )
                }
                ConstantHelper.footballListing -> {
                    showMatchDetails(
                        intent.getStringExtra(BundleKey.MatchId.toString()).toString(), ConstantHelper.football
                    )
                }
                ConstantHelper.basketballListing -> {
                    showMatchDetails(
                        intent.getStringExtra(BundleKey.MatchId.toString()).toString(), ConstantHelper.basketball
                    )
                    /* openFragment(
                         this,
                         HomeFragment(
                             this,
                             intent.getStringExtra(BundleKey.MatchId.toString()).toString(),
                             ConstantHelper.basketball
                         ),
                         true
                     )*/

                }
                else -> {
                    openFragment(this, HomeFragment(), false)
                }
            }

        } else {
            openFragment(this, HomeFragment(), false)
        }

    }

    private fun showMatchDetails(matchId: String, matchType: String) {
        //println("matchId : $matchId  matchType : $matchType")
        openFragment(this, HomeFragment(), false)
        val matchDetailFragment = MatchDetailFragment(matchId, matchType)
        val bundle = Bundle()
        bundle.putString(BundleKey.MatchId.toString(), matchId)
        bundle.putString(BundleKey.MatchType.toString(), matchType)
        matchDetailFragment.arguments = bundle
        openFragment(this, matchDetailFragment, true)
    }

    @SuppressLint("WrongConstant")
    private fun openDrawer() {
        hideKeyboard(this, mBinding?.drawerLayout)
        if (mBinding?.drawerLayout?.isDrawerVisible(Gravity.START) == true) {
            mBinding?.drawerLayout?.closeDrawer(Gravity.START, true)
        } else {
            mBinding?.drawerLayout?.openDrawer(Gravity.START, true)
        }
    }

    private fun manageClickEvents() {
        mBinding?.navView?.tvHome?.setOnClickListener {
            openDrawer()
            openFragment(this, HomeFragment(), false)
        }
        mBinding?.navView?.tvAboutUs?.setOnClickListener {
            openDrawer()
            openFragment(this, AboutUsFragment(), true)
        }
        mBinding?.navView?.tvRateUs?.setOnClickListener {
            openDrawer()
            rateApp()
        }
        mBinding?.navView?.tvPrivacyPolicy?.setOnClickListener {
            openDrawer()
            openFragment(this, PrivacyPolicyFragment(), true)
        }
        mBinding?.navView?.tvTnc?.setOnClickListener {
            openDrawer()
            openFragment(this, TermsConditionFragment(), true)
        }
    }

    private fun backPressManager() {
        supportFragmentManager.addOnBackStackChangedListener {
            try {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    mBinding?.appBarMain?.toolbar?.setNavigationIcon(R.drawable.ic_back_24)
                    mBinding?.appBarMain?.fab?.visibility = View.GONE
                } else {
                    mBinding?.appBarMain?.toolbar?.setNavigationIcon(R.drawable.ic_menu_24)
                    mBinding?.appBarMain?.fab?.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment is HomeFragment) fragment.setOnCurrentFragmentVisibleListener(this)
        if (fragment is MatchDetailFragment) fragment.setOnCurrentFragmentVisibleListener(this)
        if (fragment is AboutUsFragment) fragment.setOnCurrentFragmentVisibleListener(this)
        if (fragment is PrivacyPolicyFragment) fragment.setOnCurrentFragmentVisibleListener(this)
        if (fragment is TermsConditionFragment) fragment.setOnCurrentFragmentVisibleListener(this)

    }

    override fun onSetToolbarTitle(show: Boolean, currentFragmentName: String, toolbarTitle: String) {
        when (currentFragmentName) {
            HomeFragment::class.java.simpleName -> {
                setToolbarName(getString(R.string.menu_home))
            }
            MatchDetailsParent::class.java.simpleName -> {
                setToolbarName(toolbarTitle)
            }
            MatchDetailFragment::class.java.simpleName -> {
                setToolbarName(getString(R.string.match_details))
            }
            AboutUsFragment::class.java.simpleName -> {
                setToolbarName(getString(R.string.menu_about_us))
            }
            PrivacyPolicyFragment::class.java.simpleName -> {
                setToolbarName(getString(R.string.menu_privacy_policy))
            }
            TermsConditionFragment::class.java.simpleName -> {
                setToolbarName(getString(R.string.menu_terms_condition))
            }
        }
    }

    private fun setToolbarName(titleName: String) {
        mBinding?.appBarMain?.toolbar?.title = titleName
    }

    private fun rateApp() {
        //val uri = Uri.parse("market://details?id=com.whatsapp")
        val uri = Uri.parse("market://details?id=$packageName")
        println("playStoreUrl: $uri")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
            showToast(this, "Impossible to find an application for the market")
        }
    }

    private fun shareApplication() {
        //val link = "https://play.google.com/store/apps/details?id=com.whatsapp"
        val link = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        val uri = applicationContext.getString(R.string.kindly_find_the_application) + "\n\n" + link
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, uri
        )
        startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)))
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            doubleBackToExit()
            Timer().schedule(2000) {
                doubleBackToExitPressedOnce = false
            }
        }
    }

    private fun doubleBackToExit() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        showToast(this, getString(R.string.plz_click_back_again_to_exit))
    }

    private fun getCurrentFragment(): String {
        return supportFragmentManager.findFragmentById(R.id.nav_host_fragment)?.javaClass?.simpleName.toString()
    }

    override fun onResume() {
        super.onResume()
        checkAppVersion()
    }

    private fun checkAppVersion() {
        viewModel.checkVersion(this, apiService, getVersionCode()).observe(this, { updateApplicationModel ->
            if (updateApplicationModel != null) {
                when (updateApplicationModel.status) {
                    ConstantHelper.success -> {
                        val checkVersion = decrypt(updateApplicationModel.data?.update_type.toString())
                        //println("checkVersion : $checkVersion")
                        //val checkVersion = "1"
                        if (checkVersion == "1") {
                            updateApplicationDialog(false)
                        } else if (checkVersion == "2") {
                            updateApplicationDialog(true)
                        }
                    }

                    ConstantHelper.failed -> {
                        showToast(this, decrypt(updateApplicationModel.message))
                    }

                    ConstantHelper.apiFailed -> {
                        showToast(this, decrypt(updateApplicationModel.message))
                    }
                    ConstantHelper.noInternet -> {
                        showToast(this, decrypt(updateApplicationModel.message))
                    }
                }
            }
        })
    }

    private fun getVersionCode(): String {
        var versionCode: Long = 0
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            // version = pInfo.versionName //Version Name
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                pInfo.versionCode.toLong()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionCode.toString()
    }


    private fun getVersionName(): String {
        var versionName: String = ""
        try {
            val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
            versionName = pInfo.versionName //Version Name

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return versionName.toString()
    }


    private fun updateApplicationDialog(compulsoryUpdate: Boolean) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.update_application_dialog)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window!!.attributes = lp

        val txtUpdateApplication = dialog.findViewById(R.id.tvUpdate) as TextView
        val txtSkipUpdate = dialog.findViewById(R.id.tvSkip) as TextView

        if (compulsoryUpdate) {
            txtSkipUpdate.visibility = View.GONE
        } else {
            txtSkipUpdate.visibility = View.VISIBLE
        }

        txtSkipUpdate.setOnClickListener {
            if (dialog.isShowing) {
                dialog.cancel()
            }
        }

        txtUpdateApplication.setOnClickListener {
            val playStoreUrl = "https://play.google.com/store/apps/details?id=$packageName"
            println("playStoreUrl : $playStoreUrl")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        dialog.show()
    }


}