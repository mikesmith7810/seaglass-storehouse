import SwiftUI

struct ItemDetailView: View {
    let item: ItemResponse

    var body: some View {
        List {
            Section("Details") {
                DetailRow(label: "Location", value: item.locationName)
                DetailRow(label: "Category", value: item.categoryName)
                DetailRow(label: "Price",    value: item.formattedPrice)
            }

            if item.heightCm != nil || item.widthCm != nil || item.depthCm != nil {
                Section("Dimensions (cm)") {
                    if let h = item.heightCm { DetailRow(label: "Height", value: "\(h)") }
                    if let w = item.widthCm  { DetailRow(label: "Width",  value: "\(w)") }
                    if let d = item.depthCm  { DetailRow(label: "Depth",  value: "\(d)") }
                }
            }
        }
        .scrollContentBackground(.hidden)
        .background(Color.brandBackground)
        .navigationTitle(item.description)
        .navigationBarTitleDisplayMode(.inline)
    }
}

private struct DetailRow: View {
    let label: String
    let value: String

    var body: some View {
        HStack {
            Text(label)
                .font(.brandBody(size: 15))
                .foregroundStyle(Color.brandTextMuted)
            Spacer()
            Text(value)
                .font(.brandBody(size: 15))
                .foregroundStyle(Color.brandText)
        }
    }
}
