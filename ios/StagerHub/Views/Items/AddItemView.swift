import SwiftUI

struct AddItemView: View {
    @Environment(\.dismiss) private var dismiss

    @State private var description = ""
    @State private var selectedCategoryId: Int? = nil
    @State private var selectedLocationId: Int? = nil
    @State private var priceText = ""
    @State private var heightText = ""
    @State private var widthText = ""
    @State private var depthText = ""

    @State private var categoryList: [CategoryResponse] = []
    @State private var locationList: [LocationResponse] = []
    @State private var isSubmitting = false
    @State private var errorMessage: String?

    var body: some View {
        NavigationStack {
            Form {
                Section("Details") {
                    TextField("Description", text: $description)

                    Picker("Category", selection: $selectedCategoryId) {
                        Text("Select…").tag(nil as Int?)
                        ForEach(categoryList) { category in
                            Text(category.name).tag(category.id as Int?)
                        }
                    }

                    Picker("Location", selection: $selectedLocationId) {
                        Text("Select…").tag(nil as Int?)
                        ForEach(locationList) { location in
                            Text(location.name).tag(location.id as Int?)
                        }
                    }

                    TextField("Price (£)", text: $priceText)
                        .keyboardType(.decimalPad)
                }

                Section("Dimensions (cm, optional)") {
                    TextField("Height", text: $heightText)
                        .keyboardType(.decimalPad)
                    TextField("Width", text: $widthText)
                        .keyboardType(.decimalPad)
                    TextField("Depth", text: $depthText)
                        .keyboardType(.decimalPad)
                }

                if let errorMessage {
                    Section {
                        Text(errorMessage)
                            .foregroundStyle(.red)
                    }
                }
            }
            .navigationTitle("Add Furniture")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") { Task { await save() } }
                        .disabled(!isFormValid || isSubmitting)
                }
            }
            .task { await loadPickerData() }
        }
    }

    private var isFormValid: Bool {
        !description.trimmingCharacters(in: .whitespaces).isEmpty
            && selectedCategoryId != nil
            && selectedLocationId != nil
            && Decimal(string: priceText) != nil
    }

    private func loadPickerData() async {
        do {
            async let categories = APIClient.shared.getCategories()
            async let locations = APIClient.shared.getLocations()
            let (cats, locs) = try await (categories, locations)
            categoryList = cats
            locationList = locs
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    private func save() async {
        guard let price = Decimal(string: priceText),
              let categoryId = selectedCategoryId,
              let locationId = selectedLocationId else { return }
        isSubmitting = true
        let request = ItemRequest(
            description: description.trimmingCharacters(in: .whitespaces),
            categoryId: categoryId,
            locationId: locationId,
            price: price,
            heightCm: Decimal(string: heightText),
            widthCm: Decimal(string: widthText),
            depthCm: Decimal(string: depthText)
        )
        do {
            _ = try await APIClient.shared.createItem(request)
            dismiss()
        } catch {
            errorMessage = error.localizedDescription
            isSubmitting = false
        }
    }
}
