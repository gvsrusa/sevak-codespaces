package com.agriconnect.ui.marketplace

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel = hiltViewModel(),
    onAddListingClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.marketplace_title)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddListingClicked) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.marketplace_add_listing))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Display list of produce listings or "No listings" message
            Text(stringResource(id = R.string.marketplace_no_listings))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MarketplaceScreenPreview() {
    AgriConnectTheme {
        MarketplaceScreen(onAddListingClicked = {})
    }
}