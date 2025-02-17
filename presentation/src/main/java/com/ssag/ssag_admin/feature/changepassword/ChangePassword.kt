package com.ssag.ssag_admin.feature.changepassword

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ssag.ssag_admin.base.observeWithLifecycle
import com.ssag.ssag_admin.ui.theme.Blue700
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
@Composable
fun ChangePassword(
    navController: NavController,
    changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val changePasswordContainer = changePasswordViewModel.container
    val changePasswordState = changePasswordContainer.stateFlow.collectAsState().value
    val changePasswordSideEffect = changePasswordContainer.sideEffectFlow

    val coroutineScope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ChangePasswordTopBarContent(navController = navController)
        }
    ) {
        ChangePasswordContent(
            state = changePasswordState,
            doOnChangePasswordClick = {
                coroutineScope.launch {
                    changePasswordViewModel.changePassword()
                }
            },
            doOnCurrentPasswordInput = {
                changePasswordViewModel.inputCurrentPassword(it)
            },
            doOnNewPasswordInput = {
                changePasswordViewModel.inputNewPassword(it)
            },
            doOnConfirmPasswordInput = {
                changePasswordViewModel.inputConfirmPassword(it)
            }
        )

        changePasswordSideEffect.observeWithLifecycle {
            when (it) {
                is ChangePasswordSideEffect.ChangePasswordFail -> {
                    val changePasswordFailComment = "비밀번호 변경을 실패했습니다."
                    scaffoldState.snackbarHostState.showSnackbar(changePasswordFailComment)
                }

                is ChangePasswordSideEffect.NotDoneInput -> {
                    val notDoneInputComment = "모든 정보를 입력해주세요"
                    scaffoldState.snackbarHostState.showSnackbar(notDoneInputComment)
                }

                is ChangePasswordSideEffect.ChangePasswordSuccess -> {
                    val successLoginComment = "비밀번호를 변경하였습니다."
                    navController.popBackStack()
                    scaffoldState.snackbarHostState.showSnackbar(successLoginComment)
                }
            }
        }
    }
}

@Composable
fun ChangePasswordTopBarContent(navController: NavController) {
    TopAppBar(
        title = { Text(text = "비밀번호 변경") },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(Icons.Filled.ArrowBack, "back")
            }
        },
        backgroundColor = MaterialTheme.colors.primaryVariant,
        contentColor = Color.White,
        elevation = 12.dp
    )
}

@Composable
fun ChangePasswordContent(
    state: ChangePasswordState,
    doOnChangePasswordClick: () -> Unit,
    doOnCurrentPasswordInput: (String) -> Unit,
    doOnNewPasswordInput: (String) -> Unit,
    doOnConfirmPasswordInput: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        val currentPasswordComment = "현재 비밀번호를 입력해주세요"
        ChangePasswordTextField(
            textValue = state.currentPassword,
            doOnValueChange = doOnCurrentPasswordInput,
            labelText = currentPasswordComment
        )

        val newPasswordComment = "새로운 비밀번호를 입력해주세요"
        ChangePasswordTextField(
            textValue = state.newPassword,
            doOnValueChange = doOnNewPasswordInput,
            labelText = newPasswordComment
        )

        val confirmPasswordText = "비밀번호를 한번 더 입력해주세요"
        ChangePasswordTextField(
            textValue = state.confirmPassword,
            doOnValueChange = doOnConfirmPasswordInput,
            labelText = confirmPasswordText,
            isLastTextField = true
        )

        ChangePasswordButton(
            isLoading = state.isLoading,
            doOnChangePasswordClick = doOnChangePasswordClick
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun ChangePasswordTextField(
    textValue: String,
    doOnValueChange: (String) -> Unit,
    labelText: String,
    isError: Boolean = false,
    isLastTextField: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = textValue,
        onValueChange = doOnValueChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            textColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = if (isLastTextField) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                if (isLastTextField) {
                    focusManager.clearFocus()
                } else {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            }
        ),
        visualTransformation = PasswordVisualTransformation(),
        label = {
            Text(text = labelText)
        },
        isError = isError
    )
}

@Composable
fun ChangePasswordButton(
    isLoading: Boolean,
    doOnChangePasswordClick: () -> Unit
) {
    val buttonText = if (isLoading) "로딩중" else "변경하기"
    val buttonColor = if (isLoading) Color.Gray else Blue700
    Button(
        onClick = doOnChangePasswordClick,
        colors = buttonColors(backgroundColor = buttonColor),
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .size(250.dp, 40.dp)
    ) {
        Text(text = buttonText)
    }
}

@Preview
@Composable
fun ChangePasswordContentPreview() {
    ChangePasswordContent(
        state = ChangePasswordState.initial(),
        doOnConfirmPasswordInput = {},
        doOnNewPasswordInput = {},
        doOnCurrentPasswordInput = {},
        doOnChangePasswordClick = {}
    )
}