package com.agriconnect.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.agriconnect.R
import com.agriconnect.ui.theme.AgriConnectTheme

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit // Callback for successful login
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.app_name)) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = R.dimen.padding_large), // Define in dimens.xml
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Welcome to AgriConnect!")
            Button(onClick = {
                // TODO: Trigger Google Sign-In flow from ViewModel
                // For now, simulate login success
                onLoginSuccess()
            }) {
                Text(stringResource(id = R.string.auth_login_with_google))
            }
            // TODO: Add loading indicator and error messages based on ViewModel state
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AgriConnectTheme {
        LoginScreen(onLoginSuccess = {})
    }
}