import Foundation

struct LocationResponse: Codable, Identifiable, Hashable {
    let id: Int
    let name: String
    let photoUrl: String?
}

struct LocationRequest: Codable {
    let name: String
    let photoUrl: String?
}
