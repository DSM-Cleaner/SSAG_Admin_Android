package com.ssag.ssag_admin.feature.clean

import com.ssag.domain.feature.clean.entity.RoomStateEntity
import com.ssag.ssag_admin.base.Intent

sealed class CheckCleanIntent : Intent {

    data class SetRoomState(val roomState: RoomStateEntity) : CheckCleanIntent()

    object MoveToNextRoom : CheckCleanIntent()
    object MoveToBeforeRoom : CheckCleanIntent()
    data class MoveToRoom(val roomNumber: Int) : CheckCleanIntent()

    object ShowSelectRoomDialog : CheckCleanIntent()
    object DismissSelectRoomDialog : CheckCleanIntent()

    object SetDayIsPersonalCheckDay : CheckCleanIntent()
    object SetDayIsNotPersonalCheckDay : CheckCleanIntent()

    object SetTeacherIsMan : CheckCleanIntent()
    object SetTeacherIsWoman : CheckCleanIntent()

    object SetLightIsComplete : CheckCleanIntent()
    object SetLightIsNotComplete : CheckCleanIntent()

    object SetPlugIsComplete : CheckCleanIntent()
    object SetPlugIsNotComplete : CheckCleanIntent()

    object SetShoesAreComplete : CheckCleanIntent()
    object SetShoesAreNotComplete : CheckCleanIntent()

    data class SetStudentBedIsClean(val studentId: Long) : CheckCleanIntent()
    data class SetStudentBedIsNotClean(val studentId: Long) : CheckCleanIntent()
    data class SetStudentClotheIsClean(val studentId: Long) : CheckCleanIntent()
    data class SetStudentClotheIsNotClean(val studentId: Long) : CheckCleanIntent()

    data class SetPersonalPlaceIsNotComplete(val studentId: Long) : CheckCleanIntent()
    data class SetPersonalPlaceIsComplete(val studentId: Long) : CheckCleanIntent()
}