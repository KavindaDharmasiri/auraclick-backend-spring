package com.aura.photography.service.booking.impl;

import com.aura.photography.dto.request.BookingDTO;
import com.aura.photography.dto.request.StudioDTO;
import com.aura.photography.dto.response.CommonResponse;
import com.aura.photography.model.Booking;
import com.aura.photography.model.StudioBooking;
import com.aura.photography.repository.StudioBookingRepository;
import com.aura.photography.service.booking.BookingService;
import com.aura.photography.service.booking.StudioService;
import com.aura.photography.util.common.DateConverter;
import com.aura.photography.util.enums.BookingStatus;
import com.aura.photography.util.enums.PaymentStatus;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    @Override
    public ResponseEntity<CommonResponse> setStudioBooking(StudioDTO studioDTO) {
        try {
            BookingDTO bookingDTO = new BookingDTO();
            bookingDTO.setBookingDate(studioDTO.getBookingDate());
            bookingDTO.setService(STUDIO_SESSION);
            bookingDTO.setCreatedBy(studioDTO.getCreatedBy());
            bookingDTO.setPaymentType(studioDTO.getPaymentType());
            bookingDTO.setPaymentAmount(studioDTO.getPaymentAmount());
            bookingDTO.setPaymentStatus(studioDTO.getFullPayment() ? PaymentStatus.PAID : PaymentStatus.PARTIALLY_PAID);

            studioBookingRepository.save(getStudioBooking(studioDTO, bookingService.createBooking(bookingDTO)));

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
