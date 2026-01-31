package com.aura.photography.service.booking.impl;

import com.aura.photography.dto.request.BookingDTO;
import com.aura.photography.dto.request.WeddingDTO;
import com.aura.photography.dto.response.CommonResponse;
import com.aura.photography.model.Booking;
import com.aura.photography.model.WeddingDetails;
import com.aura.photography.repository.WeddingDetailsRepository;
import com.aura.photography.service.booking.BookingService;
import com.aura.photography.service.booking.WeddingPlanningService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.aura.photography.util.common.CommonVarStore.*;

/**
 * @Author: kasun
 * @Package: com.aura.photography.service.booking.impl
 * @Class: WeddingPlanningServiceImpl
 * @Created on: 1/30/2026 at 10:04 PM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeddingPlanningServiceImpl implements WeddingPlanningService {

    private final BookingService bookingService;
    private final WeddingDetailsRepository weddingDetailsRepository;


    @Override
    public ResponseEntity<CommonResponse> setBooking(WeddingDTO weddingDTO) {
        try {
            BookingDTO bookingDTO = new BookingDTO();
            bookingDTO.setBookingDate(LocalDateTime.now());
            String serviceType = "";
            if(weddingDTO.getPackageId() == 1)
                serviceType = GOLD;
            else if(weddingDTO.getPackageId() == 2)
                serviceType = PLATINUM;
            else if(weddingDTO.getPackageId() == 3)
                serviceType = BESPOKE;
            bookingDTO.setService(serviceType);
            bookingDTO.setCreatedBy(weddingDTO.getCreatedBy());

            weddingDetailsRepository.save(getWeddingDetails(weddingDTO, bookingService.createBooking(bookingDTO)));

            log.debug("Wedding details saved successfully");
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "Booking set successfully", null, null), HttpStatus.OK);

        }catch (Exception ex){
            log.error("Error in setBooking: ", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to set booking", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static @NonNull WeddingDetails getWeddingDetails(WeddingDTO weddingDTO, Booking booking) {
        WeddingDetails weddingDetails = new WeddingDetails();
        weddingDetails.setBooking(booking);
        weddingDetails.setWeddingDate(weddingDTO.getWeddingDate());
        weddingDetails.setEmail(weddingDTO.getEmail());
        weddingDetails.setPackageId(weddingDTO.getPackageId());
        weddingDetails.setPartner1FullName(weddingDTO.getPartner1FullName());
        weddingDetails.setPartner2FullName(weddingDTO.getPartner2FullName());
        weddingDetails.setVision(weddingDTO.getVision());
        weddingDetails.setNumberOfGuests(weddingDTO.getNumberOfGuests());
        return weddingDetails;
    }
}
