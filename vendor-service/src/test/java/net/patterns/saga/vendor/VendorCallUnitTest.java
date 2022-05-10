package net.patterns.saga.vendor;

import lombok.extern.slf4j.Slf4j;
import net.patterns.saga.common.model.ItemSearchRequest;
import net.patterns.saga.vendor.service.VendorCall;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
class VendorCallUnitTest {

    @Autowired
    VendorCall vendorCall;

    @Test
    void contextLoads() {
        log.debug("Testing.....");
        var res = vendorCall.searchItems(ItemSearchRequest.builder().item("test").build());

        assertTrue(res.size() > 0);
        for (var item : res) {
            log.trace("Testing: {}", item);
            assertEquals("TEST", item.getVendor());
            assertEquals("test", item.getName());
            assertTrue(item.getPrice().intValue() > 0);
            assertTrue(Integer.parseInt(item.getVersion())<10);
        }
        log.debug("done");
    }

}
