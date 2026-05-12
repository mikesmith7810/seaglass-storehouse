import SwiftUI

struct AddLocationView: View {
    @Environment(\.dismiss) private var dismiss

    private let existingLocation: LocationResponse?

    @State private var name: String
    @State private var isSubmitting = false
    @State private var errorMessage: String?

    init(location: LocationResponse? = nil) {
        existingLocation = location
        _name = State(initialValue: location?.name ?? "")
    }

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("Name", text: $name)
                }

                if let errorMessage {
                    Section {
                        Text(errorMessage)
                            .foregroundStyle(.red)
                    }
                }
            }
            .navigationTitle(existingLocation == nil ? "Add Location" : "Edit Location")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") { Task { await save() } }
                        .disabled(name.trimmingCharacters(in: .whitespaces).isEmpty || isSubmitting)
                }
            }
        }
    }

    private func save() async {
        isSubmitting = true
        let request = LocationRequest(name: name.trimmingCharacters(in: .whitespaces), photoUrl: nil)
        do {
            if let existing = existingLocation {
                _ = try await APIClient.shared.updateLocation(id: existing.id, request)
            } else {
                _ = try await APIClient.shared.createLocation(request)
            }
            dismiss()
        } catch {
            errorMessage = error.localizedDescription
            isSubmitting = false
        }
    }
}
