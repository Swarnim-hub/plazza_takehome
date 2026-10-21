package com.plazza.ridehailing.controller;

import com.plazza.ridehailing.dto.request.AddCouponRequest;
import com.plazza.ridehailing.dto.response.CouponResponse;
import com.plazza.ridehailing.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@Tag(name = "Coupon Management", description = "Endpoints to create, delete, and list discount coupon codes")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    @Operation(summary = "Add a coupon code", description = "Creates a new percentage or flat discount coupon")
    public ResponseEntity<CouponResponse> addCoupon(@Valid @RequestBody AddCouponRequest request) {
        CouponResponse response = couponService.addCoupon(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete a coupon code", description = "Removes a coupon code from the system")
    public ResponseEntity<Void> deleteCoupon(@PathVariable String code) {
        couponService.deleteCoupon(code);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List all coupons", description = "Returns all available coupons")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }
}
