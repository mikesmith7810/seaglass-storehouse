import Foundation

final class APIClient {
    static let shared = APIClient()

    private let baseURL: String
    private let session: URLSession
    private let decoder: JSONDecoder
    private let encoder: JSONEncoder

    private init() {
        baseURL = Config.apiBaseURL
        session = .shared
        decoder = JSONDecoder()
        encoder = JSONEncoder()
    }

    // MARK: - Items

    func getItems() async throws -> [ItemResponse] {
        try await get("/api/items")
    }

    func getItem(id: Int) async throws -> ItemResponse {
        try await get("/api/items/\(id)")
    }

    func createItem(_ request: ItemRequest) async throws -> ItemResponse {
        try await post("/api/items", body: request)
    }

    func updateItem(id: Int, _ request: ItemRequest) async throws -> ItemResponse {
        try await put("/api/items/\(id)", body: request)
    }

    func deleteItem(id: Int) async throws {
        try await delete("/api/items/\(id)")
    }

    func searchItems(
        query: String? = nil,
        categoryId: Int? = nil,
        locationId: Int? = nil,
        minPrice: Decimal? = nil,
        maxPrice: Decimal? = nil
    ) async throws -> [ItemResponse] {
        var components = URLComponents(string: baseURL + "/api/items/search")!
        var queryItems: [URLQueryItem] = []
        if let query { queryItems.append(.init(name: "q", value: query)) }
        if let categoryId { queryItems.append(.init(name: "categoryId", value: "\(categoryId)")) }
        if let locationId { queryItems.append(.init(name: "locationId", value: "\(locationId)")) }
        if let minPrice { queryItems.append(.init(name: "minPrice", value: "\(minPrice)")) }
        if let maxPrice { queryItems.append(.init(name: "maxPrice", value: "\(maxPrice)")) }
        if !queryItems.isEmpty { components.queryItems = queryItems }
        guard let url = components.url else { throw APIError.invalidURL }
        return try await fetch(url: url)
    }

    // MARK: - Locations

    func getLocations() async throws -> [LocationResponse] {
        try await get("/api/locations")
    }

    func createLocation(_ request: LocationRequest) async throws -> LocationResponse {
        try await post("/api/locations", body: request)
    }

    func deleteLocation(id: Int) async throws {
        try await delete("/api/locations/\(id)")
    }

    func updateLocation(id: Int, _ request: LocationRequest) async throws -> LocationResponse {
        try await put("/api/locations/\(id)", body: request)
    }

    // MARK: - Categories

    func getCategories() async throws -> [CategoryResponse] {
        try await get("/api/categories")
    }

    func createCategory(_ request: CategoryRequest) async throws -> CategoryResponse {
        try await post("/api/categories", body: request)
    }

    func deleteCategory(id: Int) async throws {
        try await delete("/api/categories/\(id)")
    }

    // MARK: - Private helpers

    private func get<T: Decodable>(_ path: String) async throws -> T {
        guard let url = URL(string: baseURL + path) else { throw APIError.invalidURL }
        return try await fetch(url: url)
    }

    private func post<T: Decodable, B: Encodable>(_ path: String, body: B) async throws -> T {
        guard let url = URL(string: baseURL + path) else { throw APIError.invalidURL }
        return try await send(url: url, method: "POST", body: body)
    }

    private func put<T: Decodable, B: Encodable>(_ path: String, body: B) async throws -> T {
        guard let url = URL(string: baseURL + path) else { throw APIError.invalidURL }
        return try await send(url: url, method: "PUT", body: body)
    }

    private func delete(_ path: String) async throws {
        guard let url = URL(string: baseURL + path) else { throw APIError.invalidURL }
        var request = URLRequest(url: url)
        request.httpMethod = "DELETE"
        let (data, response) = try await session.data(for: request)
        try validate(response: response, data: data)
    }

    private func fetch<T: Decodable>(url: URL) async throws -> T {
        let (data, response) = try await session.data(from: url)
        try validate(response: response, data: data)
        do {
            return try decoder.decode(T.self, from: data)
        } catch {
            throw APIError.decodingError(error)
        }
    }

    private func send<T: Decodable, B: Encodable>(url: URL, method: String, body: B) async throws -> T {
        var request = URLRequest(url: url)
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = try encoder.encode(body)
        let (data, response) = try await session.data(for: request)
        try validate(response: response, data: data)
        do {
            return try decoder.decode(T.self, from: data)
        } catch {
            throw APIError.decodingError(error)
        }
    }

    private func validate(response: URLResponse, data: Data) throws {
        guard let httpResponse = response as? HTTPURLResponse else { return }
        guard (200..<300).contains(httpResponse.statusCode) else {
            let message = String(data: data, encoding: .utf8)
            throw APIError.httpError(statusCode: httpResponse.statusCode, message: message)
        }
    }
}
