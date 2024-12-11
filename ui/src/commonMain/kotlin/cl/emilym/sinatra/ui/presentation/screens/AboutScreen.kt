package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.compose.units.rdp
import com.mikepenz.markdown.m3.Markdown
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigation_bar_about
import sinatra.ui.generated.resources.about_app_description
import sinatra.ui.generated.resources.in_app_icon

class AboutScreen: Screen {
    override val key: ScreenKey = "about"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.navigation_bar_about)) }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ) {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(Res.drawable.in_app_icon),
                        contentDescription = "An image of a bus",
                        modifier = Modifier.padding(horizontal = 1.rdp).widthIn(max = 150.dp)
                    )
                }
                Markdown(
                    stringResource(Res.string.about_app_description),
                    modifier = Modifier.padding(horizontal = 1.rdp)
                )
            }
        }
    }
}