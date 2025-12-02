package com.example.applinktesting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.applinktesting.ui.theme.AppLinkTestingTheme

class MainActivity : ComponentActivity() {

    private val TAG = "MagicLinkApp"

    // State to hold the result of the deep link for the Composable to display
    private var deepLinkStatus = mutableStateOf("Ready to receive magic link...")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initial check for a deep link when the activity is first created
        handleIntent(intent)

        setContent {
            AppLinkTestingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeepLinkStatusDisplay(deepLinkStatus.value)
                }
            }
        }
    }

    /**
     * Called when the activity is already running (e.g., in launchMode="singleTop")
     * and a new deep link is clicked. This is crucial for handling subsequent deep links.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Set the new intent and process it
        setIntent(intent)
        handleIntent(intent)
    }

    /**
     * Checks if the Intent is a view action (a deep link) and forwards the data.
     */
    private fun handleIntent(intent: Intent?) {
        // We only care about Intents that signal a view action (i.e., opening a link)
        if (intent?.action == Intent.ACTION_VIEW) {
            val data: Uri? = intent.data
            handleDeepLink(data)
        }
    }

    /**
     * Extracts and processes the deep link data, updating the UI state.
     * This receives both HTTPS (App Link) and Custom URI (Fallback) schemes.
     */
    private fun handleDeepLink(data: Uri?) {
        if (data != null) {
            // data will contain the full URI (e.g., https://app.yourdomain.com/auth?token=abc-123
            // or yourappscheme://auth?token=abc-123)

            val token = data.getQueryParameter("token")

            if (token != null) {
                // SUCCESS! The token was received.
                Log.d(TAG, "✅ Received magic link token from URI: ${data.scheme}://")
                deepLinkStatus.value = "✅ Login Token Received:\n$token"

                // ----------------------------------------------------
                // ** Your Authentication Logic Goes Here: **
                // ----------------------------------------------------

            } else {
                Log.w(TAG, "⚠️ Deep link received, but 'token' parameter is missing. Full URI: $data")
                deepLinkStatus.value = "⚠️ Error: Token parameter is missing."
            }
        }
    }
}

@Composable
fun DeepLinkStatusDisplay(status: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.titleMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// A simple preview is included but is not the focus of this deep link logic.
// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//     AppLinkTestingTheme {
//         DeepLinkStatusDisplay("Ready to test deep links...")
//     }
// }