package cn.cqautotest.sunnybeach.util

import com.blankj.utilcode.util.NetworkUtils

/**
 * author : A Lonely Cat
 * github : https://github.com/anjiemo/SunnyBeach
 * time   : 2026/01/21
 * desc   : A simple implementation of [NetworkUtils.OnNetworkStatusChangedListener].
 * This class provides empty implementations for all methods in the interface,
 * allowing subclasses to override only the methods they are interested in.
 *
 * It is designed to simplify the process of listening for network status changes
 * by reducing boilerplate code when only a subset of events (e.g., only WiFi connection,
 * or only network loss) is needed.
 *
 * Example usage:
 * ```kotlin
 * val listener = object : SimpleNetworkStatusChangedListener() {
 *     override fun onDisconnected(network: Network) {
 *         // Handle WiFi disconnected
 *     }
 *
 *     override fun onConnected(networkType: NetworkUtils.NetworkType) {
 *         // Handle network connected
 *     }
 * }
 * NetworkUtils.registerNetworkStatusChangedListener(listener)
 * ```
 */
open class SimpleNetworkStatusChangedListener : NetworkUtils.OnNetworkStatusChangedListener {
    override fun onDisconnected() {}

    override fun onConnected(networkType: NetworkUtils.NetworkType) {}
}