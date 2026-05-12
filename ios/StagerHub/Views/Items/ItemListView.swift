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
    @State private var itemToEdit: ItemResponse?
    @State private var deleteErrorMessage: String?
    @State private var showingDeleteError = false

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
                    .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                        Button(role: .destructive) {
                            Task { await deleteItem(item) }
                        } label: {
                            Label("Delete", systemImage: "trash")
                        }
                    }
                    .swipeActions(edge: .leading) {
                        Button { itemToEdit = item } label: {
                            Label("Edit", systemImage: "pencil")
                        }
                        .tint(Color.brandTeal)
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
        .sheet(item: $itemToEdit, onDismiss: { Task { await loadItems() } }) { item in
            AddItemView(item: item)
        }
        .alert("Could not delete item", isPresented: $showingDeleteError) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(deleteErrorMessage ?? "")
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

    private func deleteItem(_ item: ItemResponse) async {
        do {
            try await APIClient.shared.deleteItem(id: item.id)
            itemList.removeAll { $0.id == item.id }
        } catch {
            deleteErrorMessage = error.localizedDescription
            showingDeleteError = true
        }
    }
}
