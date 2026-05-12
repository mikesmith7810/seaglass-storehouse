import Foundation

struct ItemResponse: Codable, Identifiable, Hashable {
    let id: Int
    let description: String
    let categoryId: Int
    let categoryName: String
    let locationId: Int
    let locationName: String
    let price: Decimal
    let heightCm: Decimal?
    let widthCm: Decimal?
    let depthCm: Decimal?
    let photoUrl: String?
    let createdAt: String
    let updatedAt: String

    var formattedPrice: String {
        ItemResponse.priceFormatter.string(from: price as NSDecimalNumber) ?? "£\(price)"
    }

    private static let priceFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        formatter.numberStyle = .currency
        formatter.currencySymbol = "£"
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter
    }()
}

struct ItemRequest: Codable {
    let description: String
    let categoryId: Int
    let locationId: Int
    let price: Decimal
    let heightCm: Decimal?
    let widthCm: Decimal?
    let depthCm: Decimal?
}
