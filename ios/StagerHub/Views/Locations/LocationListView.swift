import SwiftUI

struct LocationListView: View {
    @State private var locationList: [LocationResponse] = []
    @State private var isLoading = false
    @State private var errorMessage: String?
    @State private var showingAddSheet = false

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
}
