package com.school.ppmg.computer_equipment_store_system_client.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SessionCart implements Serializable {
    private final Map<Long, Integer> items = new HashMap<>(); // productId -> qty

    public Map<Long, Integer> getItems() {
        return items;
    }

    public void add(Long productId, int qty) {
        if (productId == null || qty <= 0) return;
        items.merge(productId, qty, Integer::sum);
    }

    public void setQty(Long productId, int qty) {
        if (productId == null) return;
        if (qty <= 0) items.remove(productId);
        else items.put(productId, qty);
    }

    public void remove(Long productId) {
        if (productId == null) return;
        items.remove(productId);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}