# Sinatra
Sinatra is a companion app for the Canberra MyWay+ network made by locals for locals. Our goal is to 
make it easier to find route and stop information, offering a fresh, streamlined experience alongside 
the official app.

While Sinatra doesn’t include ticketing or full routing features, we focus on delivering the key info 
you need, fast and frustration-free.

Made with 🩷 in Canberra, Australia.

## Technical Details
Sinatra is a Compose/Kotlin Multiplatform app that targets both Android and iOS. The majority of the
native implementation is centred around the maps UI, for which a cross platform abstraction has been
developed on top of Android's Google Maps and iOS's Apple Maps.

### API
The app uses two different APIs to provide content and address resolution respectively. Content (such
as routes, stops, timetables, etc) are provided by a statically generated API based on the publically
available GTFS data. The script for generating that can be found [here](https://github.com/emilymclean/gtfs-api).
This script also generates the custom byte format used for in-app navigation.

To resolve addresses, [Nominatim](https://nominatim.openstreetmap.org/ui/search.html) is used.

Both APIs are configured through Firebase Remote Config, although another implementation may
be provided.