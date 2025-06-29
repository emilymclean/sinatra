package cl.emilym.sinatra.ui.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cl.emilym.compose.requeststate.RequestState
import cl.emilym.compose.requeststate.RequestStateWidget
import cl.emilym.compose.requeststate.handle
import cl.emilym.compose.units.rdp
import cl.emilym.sinatra.data.models.Content
import cl.emilym.sinatra.data.models.ContentId
import cl.emilym.sinatra.data.repository.ContentRepository
import cl.emilym.sinatra.nullIfBlank
import cl.emilym.sinatra.ui.widgets.BackIcon
import cl.emilym.sinatra.ui.widgets.ContentLinkWidget
import cl.emilym.sinatra.ui.widgets.ListHint
import cl.emilym.sinatra.ui.widgets.NoResultsIcon
import cl.emilym.sinatra.ui.widgets.SinatraIconButton
import cl.emilym.sinatra.ui.widgets.collectAsStateWithLifecycle
import cl.emilym.sinatra.ui.widgets.createRequestStateFlow
import com.mikepenz.markdown.m3.Markdown
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.core.annotation.Factory
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.no_content_page

@Factory
class ContentViewModel(
    private val contentRepository: ContentRepository
): ScreenModel {

    val content = createRequestStateFlow<Content?>()

    fun init(id: ContentId) {
        retry(id)
    }

    fun retry(id: ContentId) {
        screenModelScope.launch {
            content.handle {
                contentRepository.content(id)
            }
        }
    }

}

open class ContentScreen(
    val id: ContentId
): Screen {
    override val key: ScreenKey = "content-$id"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<ContentViewModel>()
        val content by viewModel.content.collectAsStateWithLifecycle()

        LifecycleEffectOnce {
            viewModel.init(id)
        }

        Content(content)
    }

    @Composable
    protected open fun Content(content: RequestState<Content?>) {
        val viewModel = koinScreenModel<ContentViewModel>()
        Scaffold(
            topBar = {
                RenderTopBar(content)
            }
        ) { innerPadding ->
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RequestStateWidget(content, retry = { viewModel.retry(id) }) { content ->
                    when (content) {
                        null -> ListHint(
                            stringResource(Res.string.no_content_page),
                        ) {
                            NoResultsIcon()
                        }
                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState())
                                    .padding(innerPadding)
                            ) {
                                PageContent(content)

                                Box(Modifier.height(1.rdp))
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    protected open fun RenderTopBar(content: RequestState<Content?>) {

        when (content) {
            is RequestState.Success -> {
                TopAppBar(
                    title = { Text(content.value?.title ?: "Content") },
                    navigationIcon = { NavigationIcon() }
                )
            }
            else -> {
                TopAppBar(
                    title = {},
                    navigationIcon = { NavigationIcon() }
                )
            }
        }
    }

    @Composable
    protected fun NavigationIcon() {
        val navigator = LocalNavigator.currentOrThrow
        when {
            !navigator.canPop -> {}
            else -> {
                SinatraIconButton(
                    onClick = { navigator.pop() },
                    icon = { BackIcon() }
                )
            }
        }
    }

    @Composable
    protected open fun ColumnScope.PageContent(content: Content) {
        RenderDynamicContent(content)
    }

    @Composable
    protected fun ColumnScope.RenderDynamicContent(content: Content) {
        RenderBodyContent(content)
        RenderLinks(content)
    }

    @Composable
    protected open fun RenderBodyContent(content: Content) {
        content.content.nullIfBlank()?.let {
            Box(Modifier.height(1.rdp))
            Markdown(
                content.content,
                modifier = Modifier.padding(horizontal = 1.rdp)
            )
            Box(Modifier.height(1.rdp))
        }
    }

    @Composable
    protected open fun RenderLinks(content: Content) {
        Column(
            Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            for (link in content.links) {
                ContentLinkWidget(
                    link,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}