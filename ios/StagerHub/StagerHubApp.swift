import SwiftUI

@main
struct StagerHubApp: App {
    init() {
        applyNavigationBarBranding()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }

    private func applyNavigationBarBranding() {
        let sage  = UIColor(named: "BrandSage")  ?? .systemBackground
        let teal  = UIColor(named: "BrandTeal")  ?? .label

        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = sage
        appearance.titleTextAttributes        = [.foregroundColor: teal]
        appearance.largeTitleTextAttributes   = [.foregroundColor: teal]
        appearance.shadowColor = .clear

        UINavigationBar.appearance().standardAppearance   = appearance
        UINavigationBar.appearance().scrollEdgeAppearance = appearance
        UINavigationBar.appearance().compactAppearance    = appearance
        UINavigationBar.appearance().tintColor = teal
    }
}
