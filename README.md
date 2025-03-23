<p align="center">
    <a href="https://github.com/emilymclean/sinatra" rel="noopener">
        <img width=150px src="https://emilym.cl/assets/img/in_app_icon.png" alt="Sinatra Icon"/>
    </a>
    <h1 align="center">Sinatra</h1>
    <p align="center">
        Sinatra is a companion app for the Canberra MyWay+ network made by locals for locals.
    </p>
</p>

<div align="center">

[![Build App](https://github.com/emilymclean/sinatra/actions/workflows/build.yml/badge.svg)](https://github.com/emilymclean/sinatra/actions/workflows/build.yml)
[![Unit Test](https://github.com/emilymclean/sinatra/actions/workflows/test.yml/badge.svg)](https://github.com/emilymclean/sinatra/actions/workflows/test.yml)
[![Lint](https://github.com/emilymclean/sinatra/actions/workflows/lint.yml/badge.svg)](https://github.com/emilymclean/sinatra/actions/workflows/lint.yml)

</div>


<p align="center">
    <a href="https://play.google.com/store/apps/details?id=cl.emilym.sinatra" rel="noopener">
        <img height=50px src="https://emilym.cl/assets/img/googleplay.png" alt="Get it on Google Play"/>
    </a>
    <a href="https://apps.apple.com/us/app/sinatra-for-canberra/id6739419456" rel="noopener">
        <img height=50px src="https://emilym.cl/assets/img/appstore.svg" alt="Download on the App Store"/>
    </a>
</p>

Sinatra is a companion app for the Canberra MyWay+ network made by locals for locals. Our goal is to 
make it easier to find route and stop information, offering a fresh, streamlined experience alongside 
the official app.

While Sinatra doesn’t include ticketing or full routing features, we focus on delivering the key info 
you need, fast and frustration-free.

Made with 🩷 in Canberra, Australia.

## Running the App
### Prebuilt
Universal Android APKs are automatically built for both the develop and main branch, the latest of
which can be found [here](https://github.com/emilymclean/sinatra/releases/latest). The build named
"debug" automatically points at the development environment, while "release" points at production.
Note that release builds built on the develop branch may not be functional, while develop builds
may quickly become out of date.

### From Source
To build from source, the app requires a Google Maps API key on Android, as well 
as a Firebase project with Firebase Crashlytics and Remote Config enabled. The values the project 
uses can be found in `RemoteConfigRepository.kt`. The Firebase project should have four targets configured:
* `cl.emilym.sinatra` for Android and iOS
* `cl.emilym.sinatra.develop` for Android and iOS

Once you have these set up, the `setup-environment.sh` is able to complete the setup of your local
development environment, from which you can contribute to or build the app.

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

### Routing
Routing is calculated on device and uses a simple 
[Dijkstra algorithm](https://en.wikipedia.org/wiki/Dijkstra's_algorithm) backed by a stripped back 
[Fibonacci Heap](https://en.wikipedia.org/wiki/Fibonacci_heap) priority queue implementation (credit 
to [Keith Schwarz](https://keithschwarz.com/interesting/code/?dir=fibonacci-heap), on which the 
implementation is based). 

The algorithm explores a time-dependent graph that represents the entire transport network. In this 
implementation, each stop is represented by a single node and each pair of (route, heading) associated
with the stop is a unique node. From there, edges connect each stop by "transfer" (walking, biking, 
etc) and each (route, heading) associated with a stop into trips with information about the conditions 
under which the edge is active.

The router uses a custom byte format to store the graph on disk, and lazily deserializes nodes and
edges on demand. The specification for the byte format can be found [here](https://github.com/emilymclean/gtfs-api/blob/main/script/network-graph-format.md).
Essentially, the network graph is always stored in memory as an unstructured byte array, with only
some metadata being permanently deserialized. Node and edge objects are in fact facades with a
pointer to a position in the byte array and knowledge of how to fetch fields from the array. When
a field is requested, the necessary data is fetched, reconstructed, and then cached. 

Compared to a previously experimented format that used a more conventional protobuf format, this 
implementation proved to significantly reduce memory pressure.
