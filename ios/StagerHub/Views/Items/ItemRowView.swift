import SwiftUI

struct ItemRowView: View {
    let item: ItemResponse

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(item.description)
                .font(.brandBody(size: 16))
                .foregroundStyle(Color.brandText)
            HStack {
                Text(item.locationName)
                    .font(.brandBody(size: 13))
                    .foregroundStyle(Color.brandTextMuted)
                Spacer()
                Text(item.formattedPrice)
                    .font(.brandBody(size: 13))
                    .foregroundStyle(Color.brandTextMuted)
            }
        }
        .padding(.vertical, 4)
    }
}
