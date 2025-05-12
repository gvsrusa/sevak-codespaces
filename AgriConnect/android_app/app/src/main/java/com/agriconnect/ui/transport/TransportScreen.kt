package com.agriconnect.ui.transport

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.agriconnect.R
import com.agriconnect.ui.theme.AgriConnectTheme

@Composable
fun TransportScreen(
    viewModel: TransportViewModel = hiltViewModel(),
    onRequestTransportClicked: () -> Unit,
    onBrowseRequestsClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.transport_title)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onRequestTransportClicked) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.transport_request_new))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(id = R.string.transport_no_requests)) // Placeholder
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBrowseRequestsClicked) {
                    Text(stringResource(id = R.string.transport_browse_available))
                }
                // TODO: Display list of transport requests or "No requests" message
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransportScreenPreview() {
    AgriConnectTheme {
        TransportScreen(onRequestTransportClicked = {}, onBrowseRequestsClicked = {})
    }
}