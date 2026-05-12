import Foundation

struct CategoryResponse: Codable, Identifiable, Hashable {
    let id: Int
    let name: String
}

struct CategoryRequest: Codable {
    let name: String
}
