package io.mosip.kernel.smsserviceprovider.sns.dto;

import lombok.Data;

@Data
public class SmsVendorRequestDto {
	private String phone_number;
	private String message;
}
