package com.oilcommerce.order.dto;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private String status;
    private String note;
    private String trackingNumber;
    private String carrier;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
}
