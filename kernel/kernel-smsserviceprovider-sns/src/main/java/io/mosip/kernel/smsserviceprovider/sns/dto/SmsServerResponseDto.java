package io.mosip.kernel.smsserviceprovider.sns.dto;

import lombok.Data;

/**
 * The DTO class for sms server response.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Data
public class SmsServerResponseDto {
	private String MessageId;
	private ResponseMetaData ResponseMetadata;
}
