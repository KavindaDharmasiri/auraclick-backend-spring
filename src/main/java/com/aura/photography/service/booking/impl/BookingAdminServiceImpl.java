package com.aura.photography.service.booking.impl;

import com.aura.photography.dto.request.BookingAdminDTO;
import com.aura.photography.dto.response.BookingTableDTO;
import com.aura.photography.dto.response.BookingDashboardMetricsDTO;
import com.aura.photography.dto.response.CommonResponse;
import com.aura.photography.model.Booking;
import com.aura.photography.model.PhotoshootBooking;
import com.aura.photography.model.StudioBooking;
import com.aura.photography.model.User;
import com.aura.photography.repository.BookingRepository;
import com.aura.photography.repository.PhotoshootBookingRepository;
import com.aura.photography.repository.StudioBookingRepository;
import com.aura.photography.service.booking.BookingAdminService;
import com.aura.photography.util.enums.BookingStatus;
import com.aura.photography.util.enums.PaymentStatus;
import com.aura.photography.util.enums.TimeSlot;
import com.aura.photography.dto.request.BookingStatusUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.aura.photography.util.common.CommonVarStore.RESPONSE_CODE_FAILURE;
import static com.aura.photography.util.common.CommonVarStore.RESPONSE_CODE_SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingAdminServiceImpl implements BookingAdminService {

    private final BookingRepository bookingRepository;
    private final PhotoshootBookingRepository photoshootBookingRepository;
    private final StudioBookingRepository studioBookingRepository;
    private final com.aura.photography.repository.PaymentSlipRepository paymentSlipRepository;

    @Override
    public ResponseEntity<CommonResponse> getBookingTable(BookingAdminDTO request) {
        try {
            // Sort latest first: use createdAt DESC, with id DESC as a stable tie-breaker
            Pageable pageable = PageRequest.of(
                    Math.max(request.getPageNumber(), 0),
                    Math.max(request.getPageSize(), 1),
                    Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
            );

            Specification<Booking> spec = (root, query, cb) -> cb.conjunction();
            if (request.getBookingType() != null && !request.getBookingType().isBlank()) {
                spec = spec.and(applyBookingType(request.getBookingType()));
            }
            if (request.getFromDate() != null || request.getToDate() != null) {
                spec = spec.and(applyDateRange(request.getFromDate(), request.getToDate()));
            }
            if (request.getBookingStatus() != null) {
                spec = spec.and(applyStatus(request.getBookingStatus()));
            }

            Page<Booking> page = bookingRepository.findAll(spec, pageable);

            List<BookingTableDTO> rows = page.getContent().stream()
                    .map(this::toBookingTableDTO)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(new CommonResponse(
                    RESPONSE_CODE_SUCCESS,
                    "Booking table fetched successfully",
                    rows,
                    (int) page.getTotalElements()
            ), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getBookingTable", ex);
            return new ResponseEntity<>(new CommonResponse(
                    RESPONSE_CODE_FAILURE,
                    "Failed to fetch booking table",
                    null,
                    null
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<CommonResponse> getDashboardMetrics() {
        try {
            YearMonth currentYm = YearMonth.now();
            YearMonth previousYm = currentYm.minusMonths(1);

            LocalDate curStart = currentYm.atDay(1);
            LocalDate curEnd = currentYm.atEndOfMonth();
            LocalDate prevStart = previousYm.atDay(1);
            LocalDate prevEnd = previousYm.atEndOfMonth();

            long curTotal = bookingRepository.countByBookingDateBetween(curStart, curEnd);
            long prevTotal = bookingRepository.countByBookingDateBetween(prevStart, prevEnd);

            long curPending = bookingRepository.countByBookingDateBetweenAndStatus(curStart, curEnd, BookingStatus.PENDING);
            long prevPending = bookingRepository.countByBookingDateBetweenAndStatus(prevStart, prevEnd, BookingStatus.PENDING);

            Double curRevenueObj = bookingRepository.sumRevenueByBookingDateRange(curStart, curEnd);
            Double prevRevenueObj = bookingRepository.sumRevenueByBookingDateRange(prevStart, prevEnd);
            double curRevenue = curRevenueObj != null ? curRevenueObj : 0.0;
            double prevRevenue = prevRevenueObj != null ? prevRevenueObj : 0.0;

            BookingDashboardMetricsDTO dto = new BookingDashboardMetricsDTO();
            dto.setTotalBookings(curTotal);
            dto.setPendingBookings(curPending);
            dto.setRevenue(round2(curRevenue));
            dto.setTotalBookingsChange(formatDeltaPercent(prevTotal, curTotal));
            dto.setPendingBookingsChange(formatDeltaPercent(prevPending, curPending));
            dto.setRevenueChange(formatDeltaPercent(prevRevenue, curRevenue));

            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "Metrics fetched successfully", dto, null), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in getDashboardMetrics", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to fetch metrics", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static String formatDeltaPercent(double prev, double curr) {
        if (prev == 0) {
            if (curr == 0) return "0%";
            return "+100%"; // treat any growth from 0 as +100%
        }
        double change = ((curr - prev) / prev) * 100.0;
        String sign = change > 0 ? "+" : ""; // negative already has '-'
        return sign + String.format(java.util.Locale.US, "%.1f%%", change);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private Specification<Booking> applyBookingType(String bookingType) {
        return (root, query, cb) -> {
            // join to Service, then to BookingTypes
            Join<com.aura.photography.model.Booking, com.aura.photography.model.Service> serviceJoin = root.join("service", JoinType.INNER);
            Join<Object, Object> btJoin = serviceJoin.join("bookingTypes", JoinType.INNER);
            String val = bookingType.trim();
            return cb.or(
                    cb.equal(cb.upper(btJoin.get("code")), val.toUpperCase()),
                    cb.equal(cb.upper(btJoin.get("name")), val.toUpperCase())
            );
        };
    }

    private Specification<Booking> applyDateRange(LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get("bookingDate"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("bookingDate"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("bookingDate"), to);
            } else {
                return cb.conjunction();
            }
        };
    }

    private Specification<Booking> applyStatus(BookingStatus status) {
        return (root, query, cb) -> status != null ? cb.equal(root.get("status"), status) : cb.conjunction();
    }

    private BookingTableDTO toBookingTableDTO(Booking b) {
        BookingTableDTO dto = new BookingTableDTO();
        dto.setBookingId(b.getId());
        dto.setBookingDate(b.getBookingDate() != null ? b.getBookingDate().toString() : null);

        com.aura.photography.model.User u = b.getUser();
        if (u != null) {
            String first = Objects.toString(u.getFirstName(), "").trim();
            String last = Objects.toString(u.getLastName(), "").trim();
            String name = (first + " " + last).trim();
            dto.setCustomerName(name.isEmpty() ? u.getEmail() : name);
        }

        if (b.getService() != null) {
            dto.setService(b.getService().getName() != null ? b.getService().getName() : b.getService().getCode());
        }

        dto.setBookingStatus(b.getStatus());

        // Duration is applicable for Photoshoot bookings; fetch if exists
        Optional<PhotoshootBooking> byBookingId = photoshootBookingRepository.findByBooking_Id(b.getId());
        if(byBookingId.isPresent()) {
            dto.setDuration(byBookingId.get().getDuration());
        } else {
            Optional<StudioBooking> byBookingId1 = studioBookingRepository.findByBooking_Id(b.getId());
            if (byBookingId1.isPresent()) {
                String tsRaw = byBookingId1.get().getTimeSlot();
                List<String> timeSlots = (tsRaw == null || tsRaw.trim().isEmpty()) ? List.of()
                        : List.of(tsRaw.trim().split(","));

                // Map codes ["1","2"] -> ["08:00 AM - 11:00 AM", "11:00 AM - 13:00 PM"]
                String durationJoined = timeSlots.stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(BookingAdminServiceImpl::resolveTimeSlotDesc)
                        .collect(Collectors.joining(" | "));

                if (!durationJoined.isEmpty()) {
                    dto.setDuration(durationJoined);
                }
            }else {
                dto.setDuration(null);
            }
        }

        dto.setPaymentStatus(b.getPaymentStatus() != null ? b.getPaymentStatus().getDescription() : null);
        
        // Initialize payment slip fields
        dto.setHasPaymentSlip(false);
        dto.setPaymentSlipId(null);
        
        // Check if payment slip exists
        paymentSlipRepository.findByBooking(b).ifPresent(slip -> {
            dto.setHasPaymentSlip(true);
            dto.setPaymentSlipId(slip.getId());
        });
        
        return dto;
    }

    @Override
    public ResponseEntity<CommonResponse> updateStatuses(BookingStatusUpdateDTO request) {
        try {
            if (request == null || request.getBookingId() == null) {
                return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "bookingId is required", null, null), HttpStatus.BAD_REQUEST);
            }

            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + request.getBookingId()));

            boolean changed = false;
            if (request.getBookingStatus() != null) {
                booking.setStatus(request.getBookingStatus());
                changed = true;
            }
            if (request.getPaymentStatus() != null) {
                booking.setPaymentStatus(request.getPaymentStatus());
                changed = true;
            }

            if (changed) {
                bookingRepository.save(booking);
            }

            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "Statuses updated successfully", null, null), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in updateStatuses", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to update statuses", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static String resolveTimeSlotDesc(String code) {
        for (TimeSlot ts : TimeSlot.values()) {
            if (ts.getCode().equalsIgnoreCase(code)) {
                return ts.getDescription();
            }
        }
        // Fallback: return the raw code if unknown
        return code;
    }

    @Override
    public ResponseEntity<?> getPaymentSlip(Long slipId) {
        try {
            var slip = paymentSlipRepository.findById(slipId)
                    .orElseThrow(() -> new IllegalArgumentException("Payment slip not found"));
            
            Path filePath = Paths.get(slip.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "File not found", null, null), HttpStatus.NOT_FOUND);
            }
            
            String contentType = "application/octet-stream";
            String filename = slip.getFileName();
            if (filename != null) {
                if (filename.toLowerCase().endsWith(".pdf")) contentType = "application/pdf";
                else if (filename.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif)")) contentType = "image/" + filename.substring(filename.lastIndexOf('.') + 1);
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception ex) {
            log.error("Error getting payment slip", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to retrieve payment slip", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<CommonResponse> approvePaymentSlip(Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
            
            booking.setPaymentStatus(PaymentStatus.PAID);
            bookingRepository.save(booking);
            
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "Payment slip approved", null, null), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error approving payment slip", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to approve payment slip", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<CommonResponse> rejectPaymentSlip(Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
            
            booking.setPaymentStatus(PaymentStatus.UNPAID);
            bookingRepository.save(booking);
            
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "Payment slip rejected", null, null), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error rejecting payment slip", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to reject payment slip", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
