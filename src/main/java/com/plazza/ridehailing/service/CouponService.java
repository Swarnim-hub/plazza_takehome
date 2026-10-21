package com.plazza.ridehailing.service;

import com.plazza.ridehailing.dto.request.AddCouponRequest;
import com.plazza.ridehailing.dto.response.CouponResponse;
import com.plazza.ridehailing.exception.ConflictException;
import com.plazza.ridehailing.exception.ResourceNotFoundException;
import com.plazza.ridehailing.model.Coupon;
import com.plazza.ridehailing.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public CouponResponse addCoupon(AddCouponRequest request) {
        String code = request.getCode().trim().toUpperCase();
        if (couponRepository.existsByCode(code)) {
            throw new ConflictException("Coupon code already exists: " + code);
        }

        Coupon coupon = new Coupon(
                code,
                request.getDiscountType(),
                request.getDiscountValue(),
                request.getMinimumFare(),
                request.getMaxDiscount()
        );

        Coupon saved = couponRepository.save(coupon);
        return CouponResponse.from(saved);
    }

    public void deleteCoupon(String code) {
        String normalizedCode = code.trim().toUpperCase();
        if (!couponRepository.deleteByCode(normalizedCode)) {
            throw new ResourceNotFoundException("Coupon not found: " + normalizedCode);
        }
    }

    public Coupon getValidCoupon(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        return couponRepository.findByCode(code)
                .filter(Coupon::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Valid coupon not found for code: " + code));
    }

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponResponse::from)
                .toList();
    }
}
