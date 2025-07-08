package cl.emilym.sinatra.ui.presentation.screens.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.ScreenKey
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.BuildInformation
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.data.repository.RemoteConfigRepository
import cl.emilym.sinatra.e
import cl.emilym.sinatra.network.apiUrl
import cl.emilym.sinatra.ui.presentation.screens.ContentScreen
import cl.emilym.sinatra.ui.widgets.NavigatorBackButton
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.information_for_developers_build_number
import sinatra.ui.generated.resources.information_for_developers_build_version
import sinatra.ui.generated.resources.information_for_developers_endpoint
import sinatra.ui.generated.resources.information_for_developers_nominatim_endpoint
import sinatra.ui.generated.resources.information_for_developers_title

class InformationForDevelopersScreen: ContentScreen(ContentRepository.INFORMATION_FOR_DEVELOPERS_ID) {
    override val key: ScreenKey = "information-for-developers"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(content: RequestState<Content?>) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.information_for_developers_title)) },
                    navigationIcon = { NavigatorBackButton() }
                )
            }
        ) { innerPadding ->
            val buildInformation = koinInject<BuildInformation>()
            val remoteConfigRepository = koinInject<RemoteConfigRepository>()

            // Bad practice but this isn't a real screen
            var nominatimUrl by remember { mutableStateOf<String?>(null) }
            LaunchedEffect(remoteConfigRepository) {
                try {
                    nominatimUrl = remoteConfigRepository.nominatimUrl()
                } catch (e: Exception) {
                    Napier.e(e)
                }
            }

            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = innerPadding
            ) {
                item {
                    InformationRow(
                        stringResource(Res.string.information_for_developers_build_version),
                        buildInformation.versionName
                    )
                }
                item {
                    InformationRow(
                        stringResource(Res.string.information_for_developers_build_number),
                        buildInformation.versionNumber
                    )
                }
                item {
                    InformationRow(
                        stringResource(Res.string.information_for_developers_endpoint),
                        apiUrl
                    )
                }
                item {
                    InformationRow(
                        stringResource(Res.string.information_for_developers_nominatim_endpoint),
                        "$nominatimUrl"
                    )
                }
                (content as? RequestState.Success)?.value?.let {
                    item {
                        Column {
                            Spacer(Modifier.height(1.rdp))
                            RenderLinks(it)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InformationRow(
        title: String,
        content: String
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(1.rdp),
                horizontalArrangement = Arrangement.spacedBy(1.rdp)
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(0.3f)
                )
                Text(
                    content,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.weight(0.7f)
                )
            }
            HorizontalDivider(Modifier.padding(horizontal = 1.rdp))
        }
    }
}