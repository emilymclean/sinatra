package cl.emilym.sinatra.ui.presentation.screens.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.ui.presentation.screens.ContentScreen
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.getKoin
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.about_app_version
import sinatra.ui.generated.resources.navigation_bar_more


class MoreScreen: ContentScreen(ContentRepository.MORE_ID) {
    override val key: ScreenKey = "more"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun RenderTopBar(content: RequestState<Content?>) {
        TopAppBar(
            title = { Text(stringResource(Res.string.navigation_bar_more)) },
            navigationIcon = { NavigationIcon() }
        )
    }

    @Composable
    override fun ColumnScope.PageContent(content: Content) {
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