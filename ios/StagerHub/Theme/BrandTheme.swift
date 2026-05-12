import SwiftUI

extension Color {
    static let brandAqua       = Color("BrandAqua")
    static let brandSage       = Color("BrandSage")
    static let brandSand       = Color("BrandSand")
    static let brandTeal       = Color("BrandTeal")
    static let brandBackground = Color("BrandBackground")
    static let brandText       = Color("BrandText")
    static let brandTextMuted  = Color("BrandTextMuted")
    static let brandBorder     = Color("BrandBorder")
}

// Custom font helpers.
// To activate: add the .ttf/.otf files to ios/StagerHub/Fonts/
// and declare them in project.yml under info.properties.UIAppFonts.
// Then replace the system font fallbacks below with .custom("PostScriptName", size: size).
extension Font {
    static func brandHeading(size: CGFloat) -> Font {
        .custom("SourceSans3-SemiBold", size: size)
    }

    static func brandBody(size: CGFloat) -> Font {
        .custom("SourceSans3-Regular", size: size)
    }

    static func brandScript(size: CGFloat) -> Font {
        .system(size: size, weight: .regular)    // replace with Buffalo PostScript name when file is added
    }
}
