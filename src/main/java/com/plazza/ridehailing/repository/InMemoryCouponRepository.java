package com.plazza.ridehailing.repository;

import com.plazza.ridehailing.model.Coupon;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCouponRepository implements CouponRepository {
    private final Map<String, Coupon> store = new ConcurrentHashMap<>();

    @Override
    public Coupon save(Coupon coupon) {
        if (coupon.getCode() != null) {
            String normalized = coupon.getCode().trim().toUpperCase();
            coupon.setCode(normalized);
            store.put(normalized, coupon);
        }
        return coupon;
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        if (code == null) return Optional.empty();
        return Optional.ofNullable(store.get(code.trim().toUpperCase()));
    }

    @Override
    public boolean deleteByCode(String code) {
        if (code == null) return false;
        return store.remove(code.trim().toUpperCase()) != null;
    }

    @Override
    public List<Coupon> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsByCode(String code) {
        if (code == null) return false;
        return store.containsKey(code.trim().toUpperCase());
    }

    @Override
    public void clear() {
        store.clear();
    }
}
