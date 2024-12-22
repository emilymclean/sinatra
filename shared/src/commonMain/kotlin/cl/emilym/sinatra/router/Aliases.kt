package cl.emilym.sinatra.router

import cl.emilym.gtfs.networkgraph.Node

typealias NodeIndex = Int
typealias StopRouteNodeIndex = NodeIndex
typealias StopNodeIndex = NodeIndex
typealias ServiceIndex = Int
typealias RouteIndex = Int
typealias HeadingIndex = Int
// StopIndex and StopNodeIndex are always equivalent
typealias StopIndex = Int
typealias EpochSeconds = Long
typealias StopRouteNode = Node
typealias StopNode = Node