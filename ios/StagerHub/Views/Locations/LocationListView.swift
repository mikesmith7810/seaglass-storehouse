import SwiftUI

struct LocationListView: View {
    @State private var locationList: [LocationResponse] = []
    @State private var isLoading = false
    @State private var errorMessage: String?
    @State private var showingAddSheet = false
    @State private var locationToEdit: LocationResponse?
    @State private var deleteErrorMessage: String?
    @State private var showingDeleteError = false

    var body: some View {
        Group {
            if isLoading {
                ProgressView("Loading…")
            } else if let errorMessage {
                ContentUnavailableView {
                    Label("Could not load locations", systemImage: "exclamationmark.triangle")
                } description: {
                    Text(errorMessage)
                } actions: {
                    Button("Retry") { Task { await loadLocations() } }
                        .buttonStyle(.bordered)
                }
            } else if locationList.isEmpty {
                ContentUnavailableView("No locations", systemImage: "mappin.slash")
            } else {
                List(locationList) { location in
                    NavigationLink(destination: ItemListView(locationFilter: location.id, title: location.name)) {
                        Text(location.name)
                            .font(.brandBody(size: 16))
                            .foregroundStyle(Color.brandText)
                    }
                    .swipeActions(edge: .trailing, allowsFullSwipe: true) {
                        Button(role: .destructive) {
                            Task { await deleteLocation(location) }
                        } label: {
                            Label("Delete", systemImage: "trash")
                        }
                    }
                    .swipeActions(edge: .leading) {
                        Button { locationToEdit = location } label: {
                            Label("Edit", systemImage: "pencil")
                        }
                        .tint(Color.brandTeal)
                    }
                }
                .scrollContentBackground(.hidden)
                .background(Color.brandBackground)
            }
        }
        .navigationTitle("Locations")
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                Button { showingAddSheet = true } label: {
                    Image(systemName: "plus")
                }
            }
        }
        .sheet(isPresented: $showingAddSheet, onDismiss: {
            Task { await loadLocations() }
        }) {
            AddLocationView()
        }
        .sheet(item: $locationToEdit, onDismiss: { Task { await loadLocations() } }) { location in
            AddLocationView(location: location)
        }
        .alert("Could not delete location", isPresented: $showingDeleteError) {
            Button("OK", role: .cancel) {}
        } message: {
            Text(deleteErrorMessage ?? "")
        }
        .task {
            await loadLocations()
        }
    }

    private func loadLocations() async {
        isLoading = true
        errorMessage = nil
        do {
            locationList = try await APIClient.shared.getLocations()
        } catch {
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }

    private func deleteLocation(_ location: LocationResponse) async {
        do {
            try await APIClient.shared.deleteLocation(id: location.id)
            locationList.removeAll { $0.id == location.id }
        } catch {
            deleteErrorMessage = error.localizedDescription
            showingDeleteError = true
        }
    }
}
