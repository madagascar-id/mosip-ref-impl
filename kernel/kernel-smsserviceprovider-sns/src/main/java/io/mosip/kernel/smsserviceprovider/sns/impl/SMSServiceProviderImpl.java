/**
 * 
 */
package io.mosip.kernel.smsserviceprovider.sns.impl;

import io.mosip.kernel.smsserviceprovider.sns.dto.SmsServerResponseDto;
import io.mosip.kernel.smsserviceprovider.sns.dto.SmsVendorRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.kernel.core.notification.exception.InvalidNumberException;
import io.mosip.kernel.core.notification.model.SMSResponseDto;
import io.mosip.kernel.core.notification.spi.SMSServiceProvider;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.smsserviceprovider.sns.constant.SmsExceptionConstant;
import io.mosip.kernel.smsserviceprovider.sns.constant.SmsPropertyConstant;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * @author Ritesh Sinha
 * @since 1.0.0
 */
@Component
public class SMSServiceProviderImpl implements SMSServiceProvider {

	@Autowired
	RestTemplate restTemplate;

	@Value("${mosip.kernel.sms.enabled:false}")
	boolean smsEnabled;

	@Value("${mosip.kernel.sms.country.code}")
	String countryCode;

	@Value("${mosip.kernel.sms.number.length}")
	int numberLength;

	@Value("${mosip.kernel.sms.api}")
	String api;

	@Value("${mosip.kernel.sms.authkey:null}")
	String authkey;

	@Override
	public SMSResponseDto sendSms(String contactNumber, String message) {
		SMSResponseDto smsResponseDTO = new SMSResponseDto();
		validateInput(contactNumber);

		try {
			SmsVendorRequestDto requestDto = new SmsVendorRequestDto();
			requestDto.setPhone_number(countryCode + contactNumber);
			requestDto.setMessage(message);

			ResponseEntity<Object> entity =  restTemplate.exchange(api, HttpMethod.POST, setRequestHeader(requestDto, null), Object.class);
			System.out.println(entity.getBody().toString());
			LinkedHashMap<String, Object> smsServerResponseDto = (LinkedHashMap) entity.getBody();
			LinkedHashMap<String, Object> responseMetaData = (LinkedHashMap<String, Object>) smsServerResponseDto.get("ResponseMetadata");
			smsResponseDTO.setMessage("Message ID : " + smsServerResponseDto.get("MessageId") + ", HTTP Status Code : " + responseMetaData.get("HTTPStatusCode") + ", Request ID : " + responseMetaData.get("RequestId")+ ", Retry Attempts : " + responseMetaData.get("RetryAttempts"));
			smsResponseDTO.setStatus("success");
		} catch (HttpClientErrorException | HttpServerErrorException | IOException e) {
			throw new RuntimeException(e.getMessage());
		}

		return smsResponseDTO;
	}

	private void validateInput(String contactNumber) {
		if (!StringUtils.isNumeric(contactNumber) || contactNumber.length() < numberLength
				|| contactNumber.length() > numberLength) {
			throw new InvalidNumberException(SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorCode(),
					SmsExceptionConstant.SMS_INVALID_CONTACT_NUMBER.getErrorMessage() + numberLength
							+ SmsPropertyConstant.SUFFIX_MESSAGE.getProperty());
		}
	}

	private HttpEntity<Object> setRequestHeader(Object requestType, MediaType mediaType) throws IOException {
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(SmsPropertyConstant.AUTH_KEY.getProperty(), authkey);

		if (mediaType != null) {
			headers.add("Content-Type", mediaType.toString());
		}
		if (requestType != null) {
			try {
				HttpEntity<Object> httpEntity = (HttpEntity<Object>) requestType;
				HttpHeaders httpHeader = httpEntity.getHeaders();
				Iterator<String> iterator = httpHeader.keySet().iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					if (!(headers.containsKey("Content-Type") && key == "Content-Type"))
						headers.add(key, httpHeader.get(key).get(0));
				}
				return new HttpEntity<Object>(httpEntity.getBody(), headers);
			} catch (ClassCastException e) {
				return new HttpEntity<Object>(requestType, headers);
			}
		} else
			return new HttpEntity<Object>(headers);
	}

}