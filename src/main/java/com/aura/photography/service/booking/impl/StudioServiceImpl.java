package com.aura.photography.service.booking.impl;

import com.aura.photography.dto.request.BookingDTO;
import com.aura.photography.dto.request.StudioDTO;
import com.aura.photography.dto.response.CommonResponse;
import com.aura.photography.model.Booking;
import com.aura.photography.model.PaymentSlip;
import com.aura.photography.model.StudioBooking;
import com.aura.photography.repository.PaymentSlipRepository;
import com.aura.photography.repository.StudioBookingRepository;
import com.aura.photography.service.booking.BookingService;
import com.aura.photography.service.booking.StudioService;
import com.aura.photography.util.common.DateConverter;
import com.aura.photography.util.enums.BookingStatus;
import com.aura.photography.util.enums.PaymentStatus;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

import static com.aura.photography.util.common.CommonVarStore.*;

/**
 * @Author: kasun
 * @Package: com.aura.photography.service.booking.impl
 * @Class: StudioServiceImpl
 * @Created on: 2/1/2026 at 9:37 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StudioServiceImpl implements StudioService {

    private final BookingService bookingService;
    private final StudioBookingRepository studioBookingRepository;
    private final PaymentSlipRepository paymentSlipRepository;
    
    @Value("${upload.dir:uploads/payment-slips}")
    private String uploadDir;

    @Override
    public ResponseEntity<CommonResponse> setStudioBooking(StudioDTO studioDTO) {
        try {
            BookingDTO bookingDTO = new BookingDTO();
            bookingDTO.setBookingDate(studioDTO.getBookingDate());
            bookingDTO.setService(STUDIO_SESSION);
            bookingDTO.setCreatedBy(studioDTO.getCreatedBy());
            bookingDTO.setPaymentType(studioDTO.getPaymentType());
            bookingDTO.setPaymentAmount(studioDTO.getPaymentAmount());
            
            // If payment slip is uploaded, set status as PENDING for admin verification
            // Otherwise, use the payment status based on fullPayment flag
            boolean hasPaymentSlip = studioDTO.getFileBase64() != null && !studioDTO.getFileBase64().isEmpty();
            if (hasPaymentSlip) {
                bookingDTO.setPaymentStatus(PaymentStatus.PENDING);
            } else {
                bookingDTO.setPaymentStatus(studioDTO.getFullPayment() ? PaymentStatus.PAID : PaymentStatus.PARTIALLY_PAID);
            }

            Booking booking = bookingService.createBooking(bookingDTO);
            studioBookingRepository.save(getStudioBooking(studioDTO, booking));
            
            // Handle payment slip if provided
            if (hasPaymentSlip) {
                try {
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    
                    String fileExtension = studioDTO.getFileName().substring(studioDTO.getFileName().lastIndexOf("."));
                    String uniqueFilename = "booking_slip_" + booking.getId() + "_" + System.currentTimeMillis() + fileExtension;
                    Path filePath = uploadPath.resolve(uniqueFilename);
                    
                    byte[] fileBytes = Base64.getDecoder().decode(studioDTO.getFileBase64());
                    Files.write(filePath, fileBytes);
                    
                    PaymentSlip paymentSlip = new PaymentSlip(booking, filePath.toString(), studioDTO.getFileName());
                    paymentSlipRepository.save(paymentSlip);
                } catch (Exception e) {
                    log.error("Failed to save payment slip: ", e);
                }
            }

            log.debug("Studio booking saved successfully");
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "Studio Booking set Successfully", null, null), HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error in setStudioBooking: ", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to set studio booking", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<CommonResponse> getAvailableTimeSlots(int studioId, String bookingDate) {
        try {
            // Fetch booked time slots for the given studio and date
            List<StudioBooking> studioBookings = studioBookingRepository.findAllByBooking_BookingDateAndStudioIdAndBooking_StatusNotIn(
                    DateConverter.convertToLocalDate(bookingDate), studioId, List.of(BookingStatus.COMPLETED, BookingStatus.CANCELLED));

            if(studioBookings.isEmpty())
            {
                log.debug("All time slots are available");
                return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "All time slots are available", List.of(1, 2, 3, 4), null), HttpStatus.OK);
            }

            // Extract booked time slots
            List<String> bookedTimeSlots = studioBookings.stream()
                    .map(StudioBooking::getTimeSlot)
                    .flatMap(timeSlotStr -> Stream.of(timeSlotStr.split(",")))
                    .toList();

            // Determine available time slots
            List<String> allTimeSlots = List.of("1", "2", "3", "4");
            List<Integer> availableTimeSlots = allTimeSlots.stream()
                    .filter(slot -> !bookedTimeSlots.contains(slot))
                    .map(Integer::valueOf)
                    .toList();

            log.debug("Available time slots fetched successfully");
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "Available time slots fetched successfully", availableTimeSlots, null), HttpStatus.OK);

        } catch (Exception ex) {
            log.error("Error in getAvailableTimeSlots: ", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to fetch available time slots", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static @NonNull StudioBooking getStudioBooking(StudioDTO studioDTO, Booking booking) {
        StudioBooking studioBooking = new StudioBooking();
        studioBooking.setBooking(booking);
        //Map timeSlots [1, 2, 3] to timeSlot strings like "1,2,3"
        studioBooking.setTimeSlot(String.join(",", studioDTO.getTimeSlot().stream().map(String::valueOf).toArray(String[]::new)));
        studioBooking.setStudioId(studioDTO.getStudioId());
        return studioBooking;
    }
}
