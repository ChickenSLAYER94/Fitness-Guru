package com.example.fitnessguru

class MapData {
    var routes = ArrayList<Routes>()
}

class Routes {
    var legs = ArrayList<Legs>()
}

class Legs {
    var distance = Distance()
    var duration = Duration()
    var end_address = ""
    var start_address = ""
    var end_location =Location()
    var start_location = Location()
    var steps = ArrayList<Steps>()
}

class Duration {
    var text = ""
    var value = 0
}

class Distance {
    var text = ""
    var value = 0
}

class Location{
    var lat =""
    var lng =""
}

class Steps {
    var polyline = PolyLine()
}

class PolyLine {
    var points = ""
}