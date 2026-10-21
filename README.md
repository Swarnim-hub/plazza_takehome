# Ride Hailing Platform Backend Service

A production-grade, extensible backend service for an on-demand ride-hailing platform (like Uber), built in **Java 21/23** with **Spring Boot 3**.

---

## 🚀 Quick Start

### Prerequisites
- **Java 21 or 23**
- **Gradle 8.x** (or pre-installed wrapper)

### Build and Run
```bash
# Run all unit and integration tests
gradle test

# Run the Spring Boot application
gradle bootRun
```

The application starts on port `8080`.

### 📖 Interactive Swagger UI
Once running, navigate to:
👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

The interactive Swagger UI exposes all endpoints with documentation, schemas, and live execution.

---

## 🛠️ Architecture & Design Decisions

### 1. Layered Modular Architecture
```
com.plazza.ridehailing
├── config       # OpenAPI Swagger configuration
├── controller   # REST endpoints with Bean validation (@Valid)
├── dto          # Request / Response DTOs decoupling domain internals
├── exception    # Centralized @RestControllerAdvice with structured HTTP status codes
├── model        # Rich domain entities & Value Objects (User, Driver, Ride, Coupon, Location)
├── repository   # Thread-safe in-memory data store interfaces and ConcurrentHashMap implementations
├── service      # Core business rules, lifecycle orchestration, and transactional safety
│   ├── matching # Strategy Pattern for driver matching (Nearest, Highest-Rated)
│   └── pricing  # Strategy Pattern for tiered pricing, coupons, and surge policies
└── util         # Pre-seeded demo dataset for live verification
```

### 2. Strategy Pattern for Driver Matching
- Switchable driver matching algorithms without touching booking orchestration:
  - `NearestDriverMatchingStrategy`: Prioritizes drivers with minimum distance to the pickup coordinate.
  - `HighestRatedDriverMatchingStrategy`: Prioritizes drivers with the highest rating within the search radius.
- Pluggable `DriverMatchingStrategyRegistry` enables per-request or platform-wide selection.

### 3. Tiered Pricing Engine & Free Vehicle Upgrade
- **Tiered Distance Rules:**
  - First 2 km: ₹10 / km
  - 3 to 5 km: ₹8 / km
  - 6 km and beyond: ₹5 / km
  - Minimum Fare: ₹50.0 (Hatchback base)
- **Car Types & Multipliers:**
  - `HATCHBACK`: 1.0x rate multiplier, minimum fare ₹50.0.
  - `SEDAN`: 1.5x rate multiplier, minimum fare ₹75.0.
- **Non-Negotiable Free Upgrade Rule:**
  - If a user requests a `HATCHBACK` but no hatchback driver is available within the radius, the system searches for available `SEDAN` drivers.
  - When assigned, the ride is flagged with `isUpgraded = true`, and the billing engine charges the **Hatchback rate** (1.0x multiplier and ₹50 min fare) at zero extra cost to the rider.

### 4. Pluggable Surge Pricing Policy
- Implements `SurgePricingPolicy` interface with dynamic multiplier injection (`POST /api/v1/rides/surge`), seamlessly multiplying gross fare before discounts.

### 5. Coupon Engine
- Supports both `PERCENTAGE` and `FLAT_AMOUNT` discount types.
- Validates active status, minimum ride fare thresholds, and maximum discount caps (`maxDiscount`).

### 6. Concurrency & Race-Condition Safety
- When multiple users attempt to book the exact same driver simultaneously, atomic check-and-assign operations (`tryAssignDriver`) prevent double-booking.
- Evaluated and verified via multithreaded test `testConcurrentBookingSafety` utilizing `ExecutorService` and `CountDownLatch`.

---

## 📋 Assumptions

1. **Distance Metric:** 
   - Coordinates are modeled as 2D spatial points `(latitude, longitude)`. Distance in kilometers is computed using Euclidean distance $\sqrt{\Delta x^2 + \Delta y^2}$ rounded to 2 decimal places. Real-world GPS coordinates map directly.
2. **Cancellation Fee:**
   - A flat cancellation fee of ₹20 is assessed when a rider cancels an active booked ride (`POST /api/v1/rides/{id}/cancel`), and the assigned driver is immediately released back to `AVAILABLE`.
3. **Storage:**
   - In-memory storage backed by `ConcurrentHashMap` with synchronized state transitions. Repositories are designed behind clean interfaces (`UserRepository`, `DriverRepository`, `RideRepository`, `CouponRepository`), making switching to Spring Data JPA/PostgreSQL a 5-minute configuration swap.

---

## 🧪 Automated Test Suite

A comprehensive test suite with 100% pass rate covers all mandatory and bonus requirements:

1. **`TieredPricingEngineTest`:**
   - ✅ Tier 1 calculation ($d \le 2$ km)
   - ✅ Tier 2 calculation ($2 < d \le 5$ km)
   - ✅ Tier 3 calculation ($d > 5$ km)
   - ✅ Minimum ride price enforcement (₹50 floor)
   - ✅ Car type rate differentiation (Hatchback vs Sedan)
   - ✅ **Non-Negotiable: Free Upgrade case** (User asks Hatchback, assigned Sedan, billed at Hatchback price)
   - ✅ Percentage coupon discount with cap
   - ✅ Flat amount coupon discount
   - ✅ Coupon minimum fare threshold rejection
   - ✅ Pluggable dynamic surge multiplier

2. **`RideServiceTest`:**
   - ✅ Successful ride booking with nearest driver in radius
   - ✅ Free upgrade to Sedan when Hatchback unavailable
   - ✅ Highest-rated driver matching strategy
   - ✅ Driver unavailable exception when out of radius
   - ✅ End ride lifecycle, dropoff location update, driver release, and total rides increment
   - ✅ Ride cancellation & cancellation fee policy
   - ✅ **Concurrency test**: Multi-threaded race condition where 2 riders book 1 driver simultaneously.

Run all tests:
```bash
gradle test
```

---

## 📦 Pre-Seeded Sample Data (Instant Live Testing)

Upon startup, the application pre-seeds realistic data:

| Entity | ID | Attributes | Notes |
| :--- | :--- | :--- | :--- |
| **User** | `USR-101` | Aarav Sharma (`9876543210`) | Location: `(0.0, 0.0)` |
| **User** | `USR-102` | Pooja Patel (`9876543211`) | Location: `(2.0, 2.0)` |
| **Driver** | `DRV-201` | Suresh Kumar (`9123456780`) | `HATCHBACK`, Location `(0.0, 1.0)`, Rating `4.6` |
| **Driver** | `DRV-202` | Manish Rao (`9123456781`) | `HATCHBACK`, Location `(0.0, 3.0)`, Rating `4.9` |
| **Driver** | `DRV-203` | Rajesh Reddy (`9123456782`) | `SEDAN`, Location `(0.0, 1.5)`, Rating `4.95` |
| **Driver** | `DRV-204` | Vikram Singh (`9123456783`) | `SEDAN`, Location `(0.0, 15.0)` (Out of 5km radius) |
| **Coupon** | `WELCOME50` | 50% off up to ₹100, min fare ₹50 |
| **Coupon** | `FLAT30` | Flat ₹30 off, min fare ₹50 |
| **Coupon** | `SAVE20` | 20% off up to ₹50, min fare ₹50 |

---

## 🌐 Key API Endpoints Summary

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/users` | Register a new user |
| `GET` | `/api/v1/users/{id}` | Get user profile |
| `POST` | `/api/v1/drivers` | Register a new driver with car type & location |
| `PUT` | `/api/v1/drivers/{id}/location` | Update cab's real-time location |
| `GET` | `/api/v1/drivers?availableOnly=true` | List available drivers (optional `carType` filter) |
| `POST` | `/api/v1/coupons` | Add a coupon code |
| `DELETE` | `/api/v1/coupons/{code}` | Delete a coupon code |
| `POST` | `/api/v1/rides/book` | Book a ride within radius (with auto free upgrade) |
| `POST` | `/api/v1/rides/{id}/end` | End ride, calculate final tiered fare & release driver |
| `POST` | `/api/v1/rides/{id}/cancel` | Cancel ride and assess cancellation fee |
| `GET` | `/api/v1/rides/user/{userId}?status=...` | User ride history (all, ongoing, completed) |
| `GET` | `/api/v1/rides/driver/{driverId}?status=...` | Driver ride history (all, ongoing, completed) |
| `POST` | `/api/v1/rides/surge` | Update dynamic surge multiplier |

---

## 🔮 What I Would Do Differently With More Time

1. **Persistent Datastore & Geospatial Indexing:**
   - Replace in-memory storage with PostgreSQL and **PostGIS** extension (`ST_DWithin`, `ST_DistanceSphere`) to index driver locations with spatial QuadTrees/R-Trees for high-scale, sub-millisecond geofencing.
2. **Distributed Locks (Redis/Redisson):**
   - In a multi-instance microservice deployment, replace JVM-level locks with Redis distributed locks (`SET key val NX EX`) to guarantee driver assignment concurrency safety across multiple cluster nodes.
3. **WebSockets / Server-Sent Events (SSE):**
   - Stream driver location updates and ride status transitions (BOOKED -> ARRIVED -> ONGOING -> COMPLETED) to user and driver mobile frontends in real time.
4. **Driver Rating Submission:**
   - Endpoint for riders to rate drivers upon trip completion, dynamically updating driver moving-average ratings.
5. **Payment Gateway Integration:**
   - Webhook-based asynchronous payment capture (Stripe/Razorpay) upon trip completion.

---

## 🤖 Reflection on AI Collaboration

### What was Prompted For:
- Clear architectural patterns: Strategy pattern for tiered pricing, car types, and driver ranking.
- Scaffold generation for Spring Boot 3 boilerplate, DTOs, and OpenAPI configuration.
- Comprehensive test scenarios focusing on non-negotiable edge cases: tiered boundaries, free vehicle upgrades, coupon restrictions, and multi-threaded race conditions.

### What was Critically Reviewed, Rejected, or Rewritten:
1. **Free Upgrade Billing Logic:**
   - Initial AI suggestions attempted to assign a Sedan without explicitly decoupling the *physical vehicle type* from the *billing calculation type*.
   - **Correction:** I insisted on modeling `requestedCarType` vs `assignedCarType` and `upgradedFree = true` explicitly in `Ride` and `FareBreakdown`. The pricing engine checks this condition so that users upgraded to Sedan are always billed at the Hatchback rate multiplier and minimum fare floor.
2. **Concurrency Safety:**
   - Initial naive implementations used read-then-write checks across separate repository calls, creating a classic check-then-act race window.
   - **Correction:** Introduced synchronized atomic assignment (`tryAssignDriver`) in `DriverRepository` and verified it with a high-concurrency multi-threaded integration test (`CountDownLatch`).
3. **Distance Calculation:**
   - Normalized spatial coordinates into a clean value object `Location` with a dedicated `.distanceTo()` method and rounding precision to prevent floating-point discrepancies across tiered boundaries.
