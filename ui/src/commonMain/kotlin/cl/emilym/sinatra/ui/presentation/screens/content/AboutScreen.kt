package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.ui.presentation.screens.content.InformationForDevelopersScreen
import cl.emilym.sinatra.ui.widgets.noRippleClickable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.in_app_icon
import sinatra.ui.generated.resources.navigation_bar_about
import sinatra.ui.generated.resources.semantics_app_icon


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
        val navigator = LocalNavigator.currentOrThrow
        var counter by remember { mutableStateOf(0) }

        Box(
            Modifier.noRippleClickable {
                counter++
                if (counter >= 10) navigator.push(InformationForDevelopersScreen())
            }.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painterResource(Res.drawable.in_app_icon),
                contentDescription = stringResource(Res.string.semantics_app_icon),
                modifier = Modifier
                    .padding(horizontal = 1.rdp)
                    .widthIn(max = 150.dp)
            )
        }

        RenderDynamicContent(content)
    }
}