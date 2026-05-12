import Foundation

// Simulator: localhost works as-is.
// Physical device on the same network: replace with your Mac's local IP, e.g. "http://192.168.1.x:8080"
enum Config {
    static let apiBaseURL = "http://localhost:8080"
}
