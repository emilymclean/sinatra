package cl.emilym.betterbuscanberra.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        Scaffold { innerPadding ->
            Column(Modifier.padding(innerPadding)) {
                Text("Test")
            }
        }
    }
}