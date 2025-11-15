package com.rakeshgangwar.videokenassignment;

/**
 * DeveloperKey stores the YouTube Data API key required for YouTube player initialization.
 *
 * SECURITY WARNING:
 * This implementation hardcodes the API key directly in source code, which is NOT recommended
 * for production applications. The API key is:
 * - Visible in version control history
 * - Extractable from the compiled APK
 * - Subject to unauthorized use and quota exhaustion
 *
 * RECOMMENDED ALTERNATIVES:
 * 1. Store in BuildConfig using gradle.properties (excluded from version control)
 * 2. Use environment variables
 * 3. Retrieve from a secure backend service
 * 4. Use Android Keystore for sensitive data
 *
 * TO GET YOUR OWN API KEY:
 * 1. Visit https://console.cloud.google.com/
 * 2. Create or select a project
 * 3. Enable "YouTube Data API v3"
 * 4. Create credentials (API Key)
 * 5. Restrict the key to Android apps with your package name and SHA-1 fingerprint
 *
 * @author Rakesh Gangwar
 * @version 1.0
 */
public class DeveloperKey {
    /**
     * YouTube Data API key for player initialization.
     * WARNING: Replace this with your own API key before using the application.
     * This key should be kept confidential and not committed to public repositories.
     */
    public static final String DEVELOPER_KEY = "AIzaSyD8xAt3L0wvZ4_irVkGzoAhrjG7rCF_7cY";
}
