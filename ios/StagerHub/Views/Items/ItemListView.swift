import SwiftUI

struct ItemListView: View {
    let locationFilter: Int?
    let title: String

    init(locationFilter: Int? = nil, title: String = "Furniture") {
        self.locationFilter = locationFilter
        self.title = title
    }

    @State private var itemList: [ItemResponse] = []
    @State private var isLoading = false
    @State private var errorMessage: String?
    @State private var showingAddSheet = false

    var body: some View {
        Group {
            if isLoading {
                ProgressView("Loading…")
            } else if let errorMessage {
                ContentUnavailableView {
                    Label("Could not load items", systemImage: "exclamationmark.triangle")
                } description: {
                    Text(errorMessage)
                } actions: {
                    Button("Retry") { Task { await loadItems() } }
                        .buttonStyle(.bordered)
                }
            } else if itemList.isEmpty {
                ContentUnavailableView("No items", systemImage: "tray")
            } else {
                List(itemList) { item in
                    NavigationLink(destination: ItemDetailView(item: item)) {
                        ItemRowView(item: item)
                    }
                }
                .scrollContentBackground(.hidden)
                .background(Color.brandBackground)
            }
        }
        .navigationTitle(title)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button { showingAddSheet = true } label: {
                    Image(systemName: "plus")
                }
            }
        }
        .sheet(isPresented: $showingAddSheet, onDismiss: {
            Task { await loadItems() }
        }) {
            AddItemView()
        }
        .task {
            await loadItems()
        }
    }

    private func loadItems() async {
        isLoading = true
        errorMessage = nil
        do {
            if let locationFilter {
                itemList = try await APIClient.shared.searchItems(locationId: locationFilter)
            } else {
                itemList = try await APIClient.shared.getItems()
            }
        } catch {
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}
