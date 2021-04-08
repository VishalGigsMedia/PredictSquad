package com.predict_squad

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.predict_squad.common_helper.ConstantHelper
import com.predict_squad.common_helper.DefaultHelper
import com.predict_squad.common_helper.InputParams
import com.predict_squad.retrofit.APIService
import com.predict_squad.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateVersionRepository {

    var matchListModel: UpdateApplicationModel? = null

    fun checkVersion(
        context: Context, apiService: APIService, versionCode: String
    ): MutableLiveData<UpdateApplicationModel> {
        val mutableLiveData: MutableLiveData<UpdateApplicationModel> = MutableLiveData()

        if (DefaultHelper.isOnline()) {
            val inputParams = InputParams()
            inputParams.version_code = DefaultHelper.encrypt(versionCode)
            apiService.checkVersion(inputParams).enqueue(object : Callback<UpdateApplicationModel> {
                override fun onResponse(
                    call: Call<UpdateApplicationModel>, response: Response<UpdateApplicationModel>
                ) {
                    try {
                        matchListModel = response.body()
                        mutableLiveData.value = matchListModel
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<UpdateApplicationModel>, t: Throwable) {
                    matchListModel = UpdateApplicationModel(null, 0, t.toString(), ConstantHelper.apiFailed)
                }
            })
        } else {
            matchListModel = UpdateApplicationModel(null, 0, context.getString(R.string.no_internet), ConstantHelper.noInternet)
        }
        return mutableLiveData
    }

}