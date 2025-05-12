package com.agriconnect.ui.transport

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransportViewModel @Inject constructor(
    // Inject repositories here e.g. private val transportRepository: TransportRepository
) : ViewModel() {
    // TODO: Implement logic for posting and browsing transport requests
    // LiveData or StateFlow for UI state (requests, loading, error)
}