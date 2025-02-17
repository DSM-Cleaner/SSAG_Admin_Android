package com.ssag.ssag_admin.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.TextFieldDefaults.textFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ssag.ssag_admin.R
import com.ssag.ssag_admin.base.observeWithLifecycle
import com.ssag.ssag_admin.ui.navigation.AppNavigationItem
import com.ssag.ssag_admin.ui.theme.Blue700
import com.ssag.ssag_admin.ui.theme.Purple700
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
@Composable
fun Login(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {
    val loginContainer = loginViewModel.container
    val loginState = loginContainer.stateFlow.collectAsState().value
    val loginSideEffect = loginContainer.sideEffectFlow
    val coroutineScope = rememberCoroutineScope()

    val scaffoldState = rememberScaffoldState()

    LaunchedEffect(Unit) {
        loginViewModel.checkNeedLogin()
    }

    Scaffold(scaffoldState = scaffoldState) {
        LoginContent(
            loginState = loginState,
            doOnNameInput = { name -> loginViewModel.inputName(name) },
            doOnPasswordInput = { password -> loginViewModel.inputPassword(password) },
            doOnLoginButtonClick = {
                coroutineScope.launch {
                    loginViewModel.login()
                }
            },
            doOnStartCheckClick = {
                navController.navigate(AppNavigationItem.CheckClean.route + "/${loginState.isManTeacher}")
            },
            doOnChangePasswordClick = {
                navController.navigate(AppNavigationItem.ChangePassword.route)
            },
            doOnLogoutClick = {
                coroutineScope.launch {
                    loginViewModel.logout()
                }
            }
        )

        loginSideEffect.observeWithLifecycle {
            when (it) {
                is LoginSideEffect.FailedLogin -> {
                    scaffoldState.snackbarHostState.showSnackbar("로그인을 실패했습니다.")
                }
                is LoginSideEffect.FailedAutoLogin -> {
                    scaffoldState.snackbarHostState.showSnackbar("로그인이 필요합니다.")
                }
            }
        }
    }


}

@Composable
fun LoginContent(
    loginState: LoginState,
    doOnNameInput: (String) -> Unit,
    doOnPasswordInput: (String) -> Unit,
    doOnLoginButtonClick: () -> Unit,
    doOnStartCheckClick: () -> Unit,
    doOnChangePasswordClick: () -> Unit,
    doOnLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Blue700)
    ) {
        LoginTitle()
        LoginLayout(
            loginState = loginState,
            doOnNameInput = doOnNameInput,
            doOnPasswordInput = doOnPasswordInput,
            doOnLoginButtonClick = doOnLoginButtonClick,
            doOnStartCheckClick = doOnStartCheckClick,
            doOnChangePasswordClick = doOnChangePasswordClick,
            doOnLogoutClick = doOnLogoutClick
        )
    }
}

@Composable
fun LoginTitle() {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_login_image),
            contentDescription = "login_image"
        )
        val title = "쓰윽싹"
        val comment = "청소 검사를 간편하게"
        Column(horizontalAlignment = Alignment.End) {
            Text(text = title, color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text(text = comment, color = Color.White, fontSize = 17.sp)
        }
    }
}

@Composable
fun LoginLayout(
    loginState: LoginState,
    doOnNameInput: (String) -> Unit,
    doOnPasswordInput: (String) -> Unit,
    doOnLoginButtonClick: () -> Unit,
    doOnStartCheckClick: () -> Unit,
    doOnChangePasswordClick: () -> Unit,
    doOnLogoutClick: () -> Unit
) {
    if (loginState.hasLogin) {
        StartCleanLayout(
            loginState = loginState,
            doOnStartCheckClick = doOnStartCheckClick,
            doOnChangePasswordClick = doOnChangePasswordClick,
            doOnLogoutClick = doOnLogoutClick
        )
    } else {
        NeedLoginLayout(
            loginState = loginState,
            doOnPasswordInput = doOnPasswordInput,
            doOnLoginButtonClick = doOnLoginButtonClick,
            doOnNameInput = doOnNameInput
        )
    }
}

@Composable
fun StartCleanLayout(
    loginState: LoginState,
    doOnStartCheckClick: () -> Unit,
    doOnChangePasswordClick: () -> Unit,
    doOnLogoutClick: () -> Unit
) {
    val loginStateComment = "현재 로그인 되어있는 선생님"
    val floorText = floorText(loginState.startFloor)
    val buttonText = "청소검사 시작하기"
    LoginColumn {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = floorText,
                fontSize = 17.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(40.dp, 0.dp)
                    .fillMaxWidth()
            )
            Text(
                text = loginStateComment,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(40.dp, 10.dp)
                    .fillMaxWidth()
            )
            TeacherCardView(teacherName = loginState.teacherName)
            Spacer(modifier = Modifier.height(30.dp))
            ChangePasswordButton(doOnChangePasswordClick = doOnChangePasswordClick)
            Spacer(modifier = Modifier.height(10.dp))
            LogoutButton(doOnLogoutClick = doOnLogoutClick)
        }

        Spacer(modifier = Modifier.height(130.dp))

        LoginButton(
            buttonText = buttonText,
            loginState = loginState,
            doOnLoginButtonClick = doOnStartCheckClick
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ChangePasswordButton(doOnChangePasswordClick: () -> Unit) {
    val changePasswordText = "비밀번호 변경하기"
    Text(text = changePasswordText, modifier = Modifier
        .clickable { doOnChangePasswordClick() }
        .padding(12.dp)
    )
}

@Composable
fun LogoutButton(doOnLogoutClick: () -> Unit) {
    val logoutText = "로그아웃"
    Text(text = logoutText, color = Color.Red, modifier = Modifier
        .clickable { doOnLogoutClick() }
        .padding(12.dp)
    )
}

@Composable
fun TeacherCardView(teacherName: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(40.dp, 0.dp)
            .height(50.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Blue700)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            val teacherText = "사감선생님"
            Text(
                text = teacherName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = teacherText, color = Color.White)
        }
    }
}

private fun floorText(startFloor: Int): String {
    return if (startFloor <= 3) {
        "2층 / 3층 검사"
    } else {
        "4층 / 5층 검사"
    }
}

@Composable
fun NeedLoginLayout(
    loginState: LoginState,
    doOnNameInput: (String) -> Unit,
    doOnPasswordInput: (String) -> Unit,
    doOnLoginButtonClick: () -> Unit
) {
    val nameLabel = "성함을 입력해 주세요"
    val passwordLabel = "비밀번호를 입력해 주세요"
    LoginColumn {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NameTextField(
                nameText = loginState.name,
                labelText = nameLabel,
                doOnNameInput = doOnNameInput
            )
            Spacer(modifier = Modifier.height(10.dp))
            PasswordTextField(
                passwordText = loginState.password,
                doOnPasswordInput = doOnPasswordInput,
                labelText = passwordLabel,
                doOnInputDone = doOnLoginButtonClick
            )
        }
        Spacer(modifier = Modifier.height(190.dp))
        val buttonText = "로그인"
        LoginButton(
            buttonText = buttonText,
            loginState = loginState,
            doOnLoginButtonClick = doOnLoginButtonClick
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun NameTextField(
    nameText: String,
    labelText: String,
    doOnNameInput: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = nameText, onValueChange = doOnNameInput,
        colors = textFieldColors(
            backgroundColor = Color.White,
            textColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),
        label = { Text(text = labelText) }
    )
}

@Composable
fun PasswordTextField(
    passwordText: String,
    labelText: String,
    doOnPasswordInput: (String) -> Unit,
    doOnInputDone: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = passwordText,
        onValueChange = doOnPasswordInput,
        colors = textFieldColors(
            backgroundColor = Color.White,
            textColor = Color.Black
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                doOnInputDone()
                focusManager.clearFocus()
            }
        ),
        visualTransformation = PasswordVisualTransformation(),
        label = { Text(text = labelText) }
    )
}

@Composable
fun LoginButton(buttonText: String, loginState: LoginState, doOnLoginButtonClick: () -> Unit) {
    val text = if (loginState.isLoading) "로딩중" else buttonText
    val color = if (loginState.isLoading) Color.Gray else Purple700
    Button(
        onClick = doOnLoginButtonClick,
        colors = buttonColors(
            backgroundColor = color,
            contentColor = Color.White
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(15.dp))
            .size(250.dp, 40.dp)
    ) {
        Text(text = text)
    }
}

@Composable
fun LoginColumn(contents: @Composable () -> Unit) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
            .background(
                Color.White
            )
    ) {
        contents()
    }
}

@Preview
@Composable
fun LoginContentPreview() {
    LoginContent(
        loginState = LoginState(
            true,
            "박창수",
            isManTeacher = false,
            isLoading = false,
            5,
            "",
            ""
        ),
        doOnPasswordInput = {},
        doOnLoginButtonClick = {},
        doOnStartCheckClick = {},
        doOnChangePasswordClick = {},
        doOnLogoutClick = {},
        doOnNameInput = {}
    )
}

@Preview
@Composable
fun StartCleanLayoutPreview() {
    StartCleanLayout(
        doOnStartCheckClick = {},
        loginState = LoginState(
            true,
            "박창수",
            isManTeacher = false,
            isLoading = false,
            5,
            "",
            ""
        ),
        doOnChangePasswordClick = {},
        doOnLogoutClick = {}
    )
}