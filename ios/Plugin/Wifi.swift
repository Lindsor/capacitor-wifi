import Foundation

@objc public class Wifi: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
