package com.ssag.data.local.datasource

interface AuthLocalDataSource {

    fun isTokenEmpty(): Boolean

    fun clearLocalData()
}