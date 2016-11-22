import com.musicocracy.fpgk.model.net.IpUtils;

import org.junit.Test;

import static junit.framework.Assert.*;

public class Ipv4ToBase36Tests {
    @Test
    public void Base36IpAddress_SerializeDeserialize_SameReturns() {
        String expected = "127.0.0.1";
        String base36 = IpUtils.ipAddressToBase36(expected);
        String actual = IpUtils.base36ToIpAddress(base36);

        assertEquals(expected, actual);
    }
}
