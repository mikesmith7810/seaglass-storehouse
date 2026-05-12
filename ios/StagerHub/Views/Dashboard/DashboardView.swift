import SwiftUI

struct DashboardView: View {
    @State private var itemList: [ItemResponse] = []
    @State private var locationCount: Int?
    @State private var isLoading = false
    @State private var errorMessage: String?
    @State private var searchText = ""
    @State private var showFurniture = false
    @State private var showLocations = false

    private var filteredItemList: [ItemResponse] {
        guard !searchText.isEmpty else { return itemList }
        return itemList.filter { $0.description.localizedCaseInsensitiveContains(searchText) }
    }

    var body: some View {
        List {
            if isLoading {
                ProgressView("Loading…")
                    .frame(maxWidth: .infinity, alignment: .center)
                    .listRowSeparator(.hidden)
                    .listRowBackground(Color.clear)
            } else if let errorMessage {
                ContentUnavailableView {
                    Label("Could not load data", systemImage: "exclamationmark.triangle")
                } description: {
                    Text(errorMessage)
                } actions: {
                    Button("Retry") { Task { await loadData() } }
                        .buttonStyle(.bordered)
                }
                .listRowSeparator(.hidden)
                .listRowBackground(Color.clear)
            } else {
                Section {
                    LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 12) {
                        Button { showFurniture = true } label: {
                            SummaryCard(title: "Furniture", count: itemList.count, systemImage: "tag")
                        }
                        .buttonStyle(.plain)

                        Button { showLocations = true } label: {
                            SummaryCard(title: "Locations", count: locationCount, systemImage: "mappin.and.ellipse")
                        }
                        .buttonStyle(.plain)
                    }
                    .listRowInsets(EdgeInsets(top: 12, leading: 16, bottom: 12, trailing: 16))
                    .listRowSeparator(.hidden)
                    .listRowBackground(Color.clear)
                }

                Section("Furniture") {
                    if filteredItemList.isEmpty {
                        Text(searchText.isEmpty ? "No items yet" : "No results for \"\(searchText)\"")
                            .foregroundStyle(Color.brandTextMuted)
                    } else {
                        ForEach(filteredItemList) { item in
                            NavigationLink(destination: ItemDetailView(item: item)) {
                                ItemRowView(item: item)
                            }
                        }
                    }
                }
            }
        }
        .listStyle(.insetGrouped)
        .scrollContentBackground(.hidden)
        .background(Color.brandBackground)
        .navigationTitle("")
        .toolbar(.hidden, for: .navigationBar)
        .safeAreaInset(edge: .top, spacing: 0) {
            HStack(alignment: .center) {
                Image("Logo")
                    .resizable()
                    .scaledToFit()
                    .frame(height: 70)

                Spacer()

                Button {
                    // Phase 3: initiate RFID scan
                } label: {
                    Text("Scan")
                        .font(.brandHeading(size: 20))
                        .foregroundStyle(Color.brandTeal)
                        .padding(.horizontal, 24)
                        .padding(.vertical, 10)
                        .background(Color.brandSand)
                        .clipShape(RoundedRectangle(cornerRadius: 8))
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(Color.brandSage.ignoresSafeArea())
        }
        .searchable(text: $searchText, placement: .navigationBarDrawer(displayMode: .always), prompt: "Search furniture…")
        .navigationDestination(isPresented: $showFurniture) { ItemListView() }
        .navigationDestination(isPresented: $showLocations) { LocationListView() }
        .task { await loadData() }
    }

    private func loadData() async {
        isLoading = true
        errorMessage = nil
        do {
            async let items = APIClient.shared.getItems()
            async let locations = APIClient.shared.getLocations()
            let (fetchedItems, fetchedLocations) = try await (items, locations)
            itemList = fetchedItems
            locationCount = fetchedLocations.count
        } catch {
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}

private struct SummaryCard: View {
    let title: String
    let count: Int?
    let systemImage: String

    var body: some View {
        VStack(spacing: 8) {
            Image(systemName: systemImage)
                .font(.title2)
                .foregroundStyle(Color.brandAqua)
            Text(count.map { "\($0)" } ?? "–")
                .font(.brandHeading(size: 34))
                .foregroundStyle(Color.brandTeal)
            Text(title)
                .font(.brandBody(size: 12))
                .foregroundStyle(Color.brandTextMuted)
        }
        .frame(maxWidth: .infinity)
        .padding(.vertical, 24)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.brandBorder, lineWidth: 1)
        )
    }
}
