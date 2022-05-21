package com.ssag.data.remote.datasource

import com.ssag.domain.feature.auth.entity.TeacherEntity
import com.ssag.domain.feature.auth.parameter.ChangePasswordParameter
import com.ssag.domain.feature.auth.parameter.LoginParameter

interface AuthRemoteDataSource {

    suspend fun login(loginParameter: LoginParameter): TeacherEntity

    suspend fun changePassword(changePasswordParameter: ChangePasswordParameter)
}