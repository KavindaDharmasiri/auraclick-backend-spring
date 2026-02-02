package com.aura.photography.dto.response;

import lombok.Data;

/**
 * Dashboard metrics for admin view (current month vs previous month deltas)
 */
@Data
public class BookingDashboardMetricsDTO {
    // Current month values
    private long totalBookings;
    private long pendingBookings;
    private double revenue;

    // Percentage deltas vs previous month (prefixed with + or - and suffixed with %)
    private String totalBookingsChange;
    private String pendingBookingsChange;
    private String revenueChange;
}
