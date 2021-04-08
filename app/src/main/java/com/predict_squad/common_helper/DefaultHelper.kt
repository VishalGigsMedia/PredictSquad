package com.predict_squad.common_helper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.predict_squad.MainActivity
import com.predict_squad.BuildConfig
import com.predict_squad.R
import org.apache.commons.codec.binary.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object DefaultHelper {

    fun getManufacturer(): String {
        return Build.MANUFACTURER.toString()
    }

    fun getBrand(): String {
        return Build.BRAND.toString()
    }

    fun getDeviceModel(): String {
        return Build.MODEL.toString()
    }

    fun getBuildVersion(): String {
        return Build.VERSION.RELEASE
    }


    fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    fun getVersionCode(): String {
        return BuildConfig.VERSION_CODE.toString()
    }

    fun getCpu(): String {
        return Build.CPU_ABI
    }

    fun getDisplay(): String {
        return Build.DISPLAY
    }


    fun getDeviceType(): String {
        return "phone"
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context?): String {
        return Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    }

    //to get network operator name
    fun getCarrierName(context: Context?): String {
        val tm = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.networkOperatorName.toString()
    }


    fun showToast(context: Context?, message: String, duration: Int = Toast.LENGTH_LONG) {
        var mToast: Toast? = null
        if (message.isNotEmpty()) {
            mToast?.cancel()
            mToast = Toast.makeText(context, message, duration)
            mToast!!.show()
        }
    }

    fun hideKeyboard(context: Activity?, view: View?) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    fun decrypt(plainText: String): String {
        return try {
            val base64 = Base64()
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val secretKey = SecretKeySpec(ConstantHelper.secretKey.toByteArray(charset("UTF-8")), "AES")
            val initializeVectorKey = IvParameterSpec(
                ConstantHelper.initializeVectorKey.toByteArray(charset("UTF-8")), 0, cipher.blockSize
            )
            cipher.init(Cipher.DECRYPT_MODE, secretKey, initializeVectorKey)
            //decrypt
            val text = cipher.doFinal(base64.decode(plainText.trim().toByteArray()))
            //println("Decrypt text : " + String(text))
            String(text)
        } catch (e: Exception) {
            plainText
        }
    }

    fun encrypt(plainText: String): String {
        val base64 = Base64()
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val secretKey = SecretKeySpec(ConstantHelper.secretKey.toByteArray(charset("UTF-8")), "AES")
        val initializeVectorKey = IvParameterSpec(
            ConstantHelper.initializeVectorKey.toByteArray(charset("UTF-8")), 0, cipher.blockSize
        )

        //encrypt
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, initializeVectorKey)
        val encryptedCipherBytes = base64.encode(cipher.doFinal(plainText.trim().toByteArray()))
        return String(encryptedCipherBytes)
    }

    fun forceLogout(context: FragmentActivity, msg: String) {
        if (msg.isNotEmpty() && msg != "null") {
            val msgValue = decrypt(msg)
            showToast(context, msgValue)
        }
        val preferenceHelper = PreferenceHelper(context)
        preferenceHelper.setClear()

        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
        context.finish()
    }


    fun openFragment(context: FragmentActivity, fragment: Fragment, addToBackStack: Boolean) {
        if (addToBackStack) {
            try {
                context.supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(MainActivity::class.java.simpleName).commit()
            } catch (ile: IllegalStateException) {
                context.supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).addToBackStack(MainActivity::class.java.simpleName).commitAllowingStateLoss()
            }
        } else {
            try {
                context.supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit()
            } catch (ile: IllegalStateException) {
                context.supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commitAllowingStateLoss()
            }
        }
    }

    fun isOnline(): Boolean {
        val haveConnectedWifi = false
        val haveConnectedMobile = false
        val cm = Application.instance?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val netInfo = cm.activeNetwork
        if (netInfo != null) {
            val nc = cm.getNetworkCapabilities(netInfo)

            return (nc?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) != null || nc?.hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            ) != null)
        }

        return haveConnectedWifi || haveConnectedMobile
    }

}