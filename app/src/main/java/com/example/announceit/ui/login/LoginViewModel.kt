package com.example.announceit.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.announceit.data.repository.AnnounceItRepository
import com.example.announceit.util.Routes
import com.example.announceit.util.UiEvent
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AnnounceItRepository) :
    ViewModel() {

    var user by mutableStateOf("")
        private set

    var userError by mutableStateOf(false)
        private set

    var userErrorMsg by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var passwordError by mutableStateOf(false)
        private set

    var passwordErrorMsg by mutableStateOf("")
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private val auth = FirebaseAuth.getInstance()

    init {
        if (userLoggedIn())
            viewModelScope.launch {
                _uiEvent.send(UiEvent.Navigate(Routes.ANNOUNCEMENTS_THREAD))
            }
    }

    fun onEvent(event: LoginScreenEvent) {
        when (event) {
            is LoginScreenEvent.OnUserChange -> {
                user = event.user
                userError = false
                userErrorMsg = ""
            }

            is LoginScreenEvent.OnPasswordChange -> {
                password = event.password
                passwordError = false
                passwordErrorMsg = ""
            }

            is LoginScreenEvent.OnLoginButtonClick -> login()
        }
    }

    private fun login() {
        if (user.isBlank()) {
            userError = true
            userErrorMsg = "Fill the field, please!"
            return
        }
        if (password.isBlank()) {
            passwordError = true
            passwordErrorMsg = "Fill the field, please!"
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = auth.signInWithEmailAndPassword(user, password).await()
                if (userLoggedIn()) {
                    repository.fetchUserType(user)
                    repository.saveUserCredentials(user, password)
                    password = ""
                    _uiEvent.send(UiEvent.Navigate(Routes.ANNOUNCEMENTS_THREAD))
                } else _uiEvent.send(UiEvent.ShowSnackbar(message = "Authorization error!"))
            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowSnackbar(message = "Authorization error!"))
            }
        }
    }

    fun userLoggedIn() = auth.currentUser != null
}