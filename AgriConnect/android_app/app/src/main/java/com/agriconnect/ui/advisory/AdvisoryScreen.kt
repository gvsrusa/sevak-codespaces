package com.agriconnect.ui.advisory

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
fun AdvisoryScreen(
    viewModel: AdvisoryViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.advisory_title)) })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Display list of advisory content or "No content" message
            Text(stringResource(id = R.string.advisory_no_content))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdvisoryScreenPreview() {
    AgriConnectTheme {
        AdvisoryScreen()
    }
}