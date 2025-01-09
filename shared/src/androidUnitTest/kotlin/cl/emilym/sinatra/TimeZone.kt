package cl.emilym.sinatra

import kotlinx.datetime.UtcOffset
import kotlinx.datetime.asTimeZone

val timeZone = UtcOffset(hours = 11).asTimeZone()