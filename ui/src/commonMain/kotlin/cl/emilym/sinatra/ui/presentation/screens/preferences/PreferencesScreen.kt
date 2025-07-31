package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.widgets.ContentLinkWidget
import cl.emilym.sinatra.ui.widgets.NavigatorBackButton
import org.koin.compose.koinInject

abstract class PreferencesScreen: Screen {
    @get:Composable
    abstract val title: String

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = { NavigatorBackButton() }
                )
            }
        ) { innerPadding ->
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding),
                ) {
                    val preferencesRepository = koinInject<PreferencesRepository>()
                    val scope = rememberCoroutineScope()

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 1.rdp),
                        verticalArrangement = Arrangement.spacedBy(2.rdp)
                    ) {
                        Preferences()
                    }
                    options().nullIfEmpty()?.let {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            for (link in it) {
                                ContentLinkWidget(link, Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    abstract fun ColumnScope.Preferences()

    @Composable
    open fun options(): List<ContentLink> { return emptyList() }

}