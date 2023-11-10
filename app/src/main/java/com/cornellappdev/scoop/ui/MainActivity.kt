package com.cornellappdev.scoop.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cornellappdev.scoop.R
import com.cornellappdev.scoop.ui.login.AuthFlow
import com.cornellappdev.scoop.ui.login.AuthScreen
import com.cornellappdev.scoop.ui.theme.ScoopTheme
import com.cornellappdev.scoop.ui.viewmodel.LoginViewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {
            ProvideWindowInsets {
                ScoopTheme {
                    //MainScreen()
                    //OnboardingHolderView()
                    AuthFlow(initLoginModel())
                }
            }
        }
    }

    private fun initLoginModel(): LoginViewModel {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.gcp_id))
            .requestId()
            .requestProfile()
            .build()
        return LoginViewModel(GoogleSignIn.getClient(this, gso))
    }
}

