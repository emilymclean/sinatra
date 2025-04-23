package cl.emilym.sinatra.ui.presentation.screens

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import sinatra.ui.generated.resources.Res
import sinatra.ui.generated.resources.app_out_of_date_android_link
import sinatra.ui.generated.resources.app_out_of_date_android_title

internal actual val Res.string.app_out_of_date_store_title: StringResource
    get() = Res.string.app_out_of_date_android_title
internal actual val Res.string.app_out_of_date_store_link: StringResource
    get() = Res.string.app_out_of_date_android_link