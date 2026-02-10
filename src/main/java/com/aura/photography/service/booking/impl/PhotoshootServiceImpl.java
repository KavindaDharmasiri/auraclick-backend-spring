package com.aura.photography.service.booking.impl;

import com.aura.photography.dto.request.BookingDTO;
import com.aura.photography.dto.request.PhotoshootDTO;
import com.aura.photography.dto.response.CommonResponse;
import com.aura.photography.model.Booking;
import com.aura.photography.model.PaymentSlip;
import com.aura.photography.model.PhotoshootBooking;
import com.aura.photography.repository.PaymentSlipRepository;
import com.aura.photography.repository.PhotoshootBookingRepository;
import com.aura.photography.service.booking.BookingService;
import com.aura.photography.service.booking.PhotoshootService;
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

import static com.aura.photography.util.common.CommonVarStore.*;

/**
 * @Author: kasun_t
 * @Package: com.aura.photography.service.booking.impl
 * @Class: PhotoshootServiceImpl
 * @Created on: 2/2/2026 at 12:10 PM
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoshootServiceImpl implements PhotoshootService {

    private final BookingService bookingService;
    private final PhotoshootBookingRepository photoshootBookingRepository;
    private final PaymentSlipRepository paymentSlipRepository;
    
    @Value("${upload.dir:uploads/payment-slips}")
    private String uploadDir;

    @Override
    public ResponseEntity<CommonResponse> setPhotoshootBooking(PhotoshootDTO photoshootDTO) {
        try {
            BookingDTO bookingDTO = new BookingDTO();
            bookingDTO.setBookingDate(photoshootDTO.getBookingDate());
            bookingDTO.setService(photoshootDTO.getServiceType()); // e.g., OS/SS/COM/EVT
            bookingDTO.setCreatedBy(photoshootDTO.getCreatedBy());
            bookingDTO.setPaymentType(photoshootDTO.getPaymentType());
            bookingDTO.setPaymentAmount(photoshootDTO.getPaymentAmount());
            
            // If payment slip is uploaded, set status as PENDING for admin verification
            // Otherwise, use the payment status based on fullPayment flag
            boolean hasPaymentSlip = photoshootDTO.getFileBase64() != null && !photoshootDTO.getFileBase64().isEmpty();
            if (hasPaymentSlip) {
                bookingDTO.setPaymentStatus(PaymentStatus.PENDING);
            } else {
                bookingDTO.setPaymentStatus(Boolean.TRUE.equals(photoshootDTO.getFullPayment()) ? PaymentStatus.PAID : PaymentStatus.PARTIALLY_PAID);
            }

            Booking booking = bookingService.createBooking(bookingDTO);
            photoshootBookingRepository.save(getPhotoshootBooking(photoshootDTO, booking));
            
            // Handle payment slip if provided
            if (hasPaymentSlip) {
                try {
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    
                    String fileExtension = photoshootDTO.getFileName().substring(photoshootDTO.getFileName().lastIndexOf("."));
                    String uniqueFilename = "booking_slip_" + booking.getId() + "_" + System.currentTimeMillis() + fileExtension;
                    Path filePath = uploadPath.resolve(uniqueFilename);
                    
                    byte[] fileBytes = Base64.getDecoder().decode(photoshootDTO.getFileBase64());
                    Files.write(filePath, fileBytes);
                    
                    PaymentSlip paymentSlip = new PaymentSlip(booking, filePath.toString(), photoshootDTO.getFileName());
                    paymentSlipRepository.save(paymentSlip);
                } catch (Exception e) {
                    log.error("Failed to save payment slip: ", e);
                }
            }

            log.debug("Photoshoot booking saved successfully");
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_SUCCESS, "Photoshoot Booking set Successfully", null, null), HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error in setPhotoshootBooking: ", ex);
            return new ResponseEntity<>(new CommonResponse(RESPONSE_CODE_FAILURE, "Failed to set photoshoot booking", null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static @NonNull PhotoshootBooking getPhotoshootBooking(PhotoshootDTO dto, Booking booking) {
        PhotoshootBooking ps = new PhotoshootBooking();
        ps.setBooking(booking);
        // store duration and location as codes/names for consistency
        ps.setDuration(dto.getDuration() != null ? dto.getDuration().getDescription() : null);
        ps.setLocation(dto.getLocation() != null ? dto.getLocation().name() : null);
        ps.setSubService(dto.getSubService());
        return ps;
    }
}
