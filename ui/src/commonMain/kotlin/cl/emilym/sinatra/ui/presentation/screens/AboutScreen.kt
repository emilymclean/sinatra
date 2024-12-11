package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.ui.VersionInformation
import com.mikepenz.markdown.m3.Markdown
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.android.annotation.KoinViewModel
import org.koin.compose.viewmodel.koinViewModel
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.navigation_bar_about
import sinatra.ui.generated.resources.about_app_description
import sinatra.ui.generated.resources.about_app_version
import sinatra.ui.generated.resources.in_app_icon

@KoinViewModel
class AboutScreenViewModel(
    val versionInformation: VersionInformation,
): ViewModel()

class AboutScreen: Screen {
    override val key: ScreenKey = "about"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinViewModel<AboutScreenViewModel>()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.navigation_bar_about)) }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
            ) {
                Box(Modifier.height(1.rdp))
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

                Box(Modifier.weight(1f))

                Text(
                    stringResource(Res.string.about_app_version, viewModel.versionInformation.name, viewModel.versionInformation.number),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 1.rdp),
                    textAlign = TextAlign.Center
                )

                Box(Modifier.height(1.rdp))
            }
        }
    }
}