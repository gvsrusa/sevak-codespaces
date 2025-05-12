package com.agriconnect.ui.prices

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
fun MarketPricesScreen(
    viewModel: MarketPricesViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.prices_title)) })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Display list of market prices or "No data" message
            Text(stringResource(id = R.string.prices_no_data))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MarketPricesScreenPreview() {
    AgriConnectTheme {
        MarketPricesScreen()
    }
}