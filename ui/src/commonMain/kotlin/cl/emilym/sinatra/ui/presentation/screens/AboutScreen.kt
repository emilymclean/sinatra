package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.BuildInformation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.about_app_version
import sinatra.ui.generated.resources.in_app_icon
import sinatra.ui.generated.resources.navigation_bar_about


class AboutScreen: ContentScreen(ContentRepository.ABOUT_ID) {
    override val key: ScreenKey = "about"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun RenderTopBar(content: RequestState<Content?>) {
        TopAppBar(
            title = { Text(stringResource(Res.string.navigation_bar_about)) },
            navigationIcon = { NavigationIcon() }
        )
    }

    @Composable
    override fun ColumnScope.PageContent(content: Content) {
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

        RenderDynamicContent(content)

        Box(Modifier.height(1.rdp))
        Box(Modifier.weight(1f))

        val koin = getKoin()
        val buildInformation = remember { koin.get<BuildInformation>() }
        Text(
            stringResource(Res.string.about_app_version, buildInformation.versionName, buildInformation.versionNumber),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 1.rdp),
            textAlign = TextAlign.Center
        )
    }
}