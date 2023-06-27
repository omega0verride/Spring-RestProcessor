package org.indritbreti.restprocessordemo;

import org.indritbreti.restprocessor.FieldDetailsRegistry;
import org.indritbreti.restprocessordemo.API.DemoEntity.Product;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class TestProcessorFieldDetailsRegistry {

    @Test
    @Order(1)
    public void testLoadSerializedData() {
        FieldDetailsRegistry fieldDetailsRegistry = FieldDetailsRegistry.instance();
        fieldDetailsRegistry.lookup(Product.class);
    }
}
