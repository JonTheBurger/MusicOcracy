package domain.net;

import com.musicocracy.fpgk.domain.net.NetworkUtils;

import org.junit.Test;

import static junit.framework.Assert.*;

public class NetworkUtilsTests {
    @Test
    public void Base36IpAddress_SerializeDeserialize_SameReturns() {
        String[] expected = {
                "192.168.0.100",
                "174.60.75.1",
                "127.0.0.1",
                "0.0.0.0",
                "255.255.255.255",
        };

        for (int i = 0; i < expected.length; i++) {
            String base36 = NetworkUtils.ipAddressToBase36(expected[i]);
            String actual = NetworkUtils.base36ToIpAddress(base36);

            assertEquals(expected[i], actual);
        }
    }
}
