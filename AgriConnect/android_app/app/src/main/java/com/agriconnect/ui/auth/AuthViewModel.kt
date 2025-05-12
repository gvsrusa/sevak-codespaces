package com.agriconnect.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agriconnect.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserData(
    val userId: String,
    val username: String?,
    val email: String?,
    val profilePictureUrl: String?
)

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class SignInState(
    val isSignInInProgress: Boolean = false,
    val signInError: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository // Injected AuthRepository
) : ViewModel() {

    private val _signInState = MutableStateFlow(SignInState())
    val signInState: StateFlow<SignInState> = _signInState.asStateFlow()

    private val _userState = MutableStateFlow<UserData?>(null)
    val userState: StateFlow<UserData?> = _userState.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        viewModelScope.launch {
            if (result.data != null) {
                _userState.update { result.data }
                _signInState.update {
                    it.copy(
                        isSignInInProgress = false,
                        signInError = null
                    )
                }
            } else {
                _signInState.update {
                    it.copy(
                        isSignInInProgress = false,
                        signInError = result.errorMessage
                    )
                }
                // Ensure user state is cleared on error
                _userState.update { null }
            }
        }
    }

    // Placeholder for a method that might initiate sign-in,
    // which would set isSignInInProgress to true.
    // Not strictly required by current tests but good for completeness.
    fun initiateSignIn() {
        _signInState.update { it.copy(isSignInInProgress = true, signInError = null) }
        // Actual sign-in logic with repository would go here,
        // and its result would eventually call onSignInResult.
        // For example:
        // viewModelScope.launch {
        //     repository.signInWithGoogleIntent(intent).collect { result ->
        //         onSignInResult(result)
        //     }
        // }
    }

    fun resetState() {
        _signInState.update { SignInState() }
        _userState.update { null }
    }
}