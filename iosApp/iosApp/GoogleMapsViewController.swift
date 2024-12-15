import SwiftUI
import GoogleMaps

struct GoogleMapsUIViewRepresentable: UIViewRepresentable {
    typealias UIViewType = GMSMapView
    
    func makeUIView(context: Context) -> UIViewType {
        let options = GMSMapViewOptions()
        options.camera = GMSCameraPosition.camera(withLatitude: 12.952636, longitude: 77.653059, zoom: 10.0)

        let mapView = GMSMapView(options: options)

        // Creates a marker in the center of the map.
        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: 12.952636, longitude: 77.653059)
        marker.title = "Indiranagar"
        marker.snippet = "Bengaluru"
        marker.map = mapView
        
        return mapView
    }
    
    func updateUIView(_ uiView: UIViewType, context: Context) { }
    
}
