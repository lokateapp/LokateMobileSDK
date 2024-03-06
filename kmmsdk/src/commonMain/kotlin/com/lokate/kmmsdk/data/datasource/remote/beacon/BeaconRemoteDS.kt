package com.lokate.kmmsdk.data.datasource.remote.beacon

import com.lokate.kmmsdk.data.datasource.DSResult
import com.lokate.kmmsdk.data.datasource.remote.ApiResponse

class BeaconRemoteDS(private val api: BeaconAPI) {
    suspend fun fetchBeacons(appToken: String): DSResult {
        return api.getActiveBeacons(appToken).let {
            when (it) {
                is ApiResponse.Error.GenericError -> DSResult.Error(it.errorMessage.orEmpty(), it)
                is ApiResponse.Error.HttpError -> DSResult.Error("${it.code} + ${it.errorBody}", it)
                is ApiResponse.Error.SerializationError -> DSResult.Error(
                    it.errorMessage.orEmpty(),
                    it
                )

                is ApiResponse.Success -> DSResult.Success(it.body)
            }
        }
    }
}