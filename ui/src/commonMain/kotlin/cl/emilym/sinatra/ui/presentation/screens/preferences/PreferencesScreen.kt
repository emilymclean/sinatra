package cl.emilym.sinatra.ui.presentation.screens.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.ContentLink
import cl.emilym.sinatra.data.models.Time24HSetting
import cl.emilym.sinatra.data.repository.PreferencesRepository
import cl.emilym.sinatra.data.repository.StatefulPreferencesUnit
import cl.emilym.sinatra.data.repository.state
import cl.emilym.sinatra.nullIfEmpty
import cl.emilym.sinatra.ui.widgets.ContentLinkWidget
import cl.emilym.sinatra.ui.widgets.NavigatorBackButton
import org.koin.compose.getKoin
import org.koin.compose.koinInject

data class PreferencesCollection(
    val requiresWheelchair: StatefulPreferencesUnit<Boolean>,
    val requiresBikes: StatefulPreferencesUnit<Boolean>,
    val maximumWalkingTime: StatefulPreferencesUnit<Float>,
    val showAccessibilityIconsNavigation: StatefulPreferencesUnit<Boolean>,
    val metric: StatefulPreferencesUnit<Boolean>,
    val time24Hour: StatefulPreferencesUnit<Time24HSetting>
)

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
                        Preferences(
                            PreferencesCollection(
                                preferencesRepository.requiresWheelchair.state(scope),
                                preferencesRepository.requiresBikes.state(scope),
                                preferencesRepository.maximumWalkingTime.state(scope),
                                preferencesRepository.showAccessibilityIconsNavigation.state(scope),
                                preferencesRepository.metric.state(scope),
                                preferencesRepository.use24Hour.state(scope)
                            )
                        )
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
    abstract fun ColumnScope.Preferences(
        preferencesCollection: PreferencesCollection
    )

    @Composable
    open fun options(): List<ContentLink> { return emptyList() }

}