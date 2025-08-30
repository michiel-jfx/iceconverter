package nl.dotjava.javafx.support;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectivitySupport {
    private ConnectivitySupport() {
        // default empty constructor
    }

    private static final String IP_ADDRESS = "149.210.169.118";
    private static final int IP_PORT = 443;
    private static final int TIMEOUT_MS = 200;

    public static boolean internetAvailable() {
        return isSocketReachable();
    }

    private static boolean isSocketReachable() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(IP_ADDRESS, IP_PORT), TIMEOUT_MS);
            return true;
        } catch (Exception ignored) {
            // probably: "Network is unreachable"
        }
        return false;
    }
}
