package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.ui.widgets.ContentLinkWidget
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.app_out_of_date_content
import sinatra.ui.generated.resources.app_out_of_date_homepage_link
import sinatra.ui.generated.resources.app_out_of_date_homepage_title
import sinatra.ui.generated.resources.app_out_of_date_title
import sinatra.ui.generated.resources.in_app_icon
import sinatra.ui.generated.resources.semantics_app_icon

@Composable
fun AppOutOfDateScreen() {
    Scaffold { internalPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(internalPadding)
        ) {
            Column(
                Modifier.padding(vertical = 1.rdp),
                verticalArrangement = Arrangement.spacedBy(1.rdp)
            ) {
                Column(
                    Modifier.padding(horizontal = 1.rdp),
                    verticalArrangement = Arrangement.spacedBy(1.rdp)
                ) {
                    Box(
                        Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painterResource(Res.drawable.in_app_icon),
                            contentDescription = stringResource(Res.string.semantics_app_icon),
                            modifier = Modifier.padding(horizontal = 1.rdp).widthIn(max = 150.dp)
                        )
                    }
                    Text(
                        stringResource(Res.string.app_out_of_date_title),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        stringResource(Res.string.app_out_of_date_content)
                    )
                }

                Column(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    ContentLinkWidget(
                        ContentLink.external(
                            stringResource(Res.string.app_out_of_date_store_title),
                            stringResource(Res.string.app_out_of_date_store_link)
                        )!!,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ContentLinkWidget(
                        ContentLink.external(
                            stringResource(Res.string.app_out_of_date_homepage_title),
                            stringResource(Res.string.app_out_of_date_homepage_link)
                        )!!,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

internal expect val Res.string.app_out_of_date_store_title: StringResource
internal expect val Res.string.app_out_of_date_store_link: StringResource