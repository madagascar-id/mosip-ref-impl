package io.mosip.kernel.smsserviceprovider.sns.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import io.mosip.kernel.core.notification.exception.InvalidNumberException;
import io.mosip.kernel.smsserviceprovider.sns.impl.SMSServiceProviderImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ConfigFileApplicationContextInitializer.class, SmsServiceProviderTest.config.class,
		SMSServiceProviderImpl.class })
public class SmsServiceProviderTest {

	@Configuration
	static class config {

		@Bean
		public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
			propertySourcesPlaceholderConfigurer.setLocations(new ClassPathResource("application-local.properties"));
			return propertySourcesPlaceholderConfigurer;
		}
	}

	@Autowired
	SMSServiceProviderImpl service;

	@MockBean
	RestTemplate restTemplate;

	@Value("${mosip.kernel.sms.api}")
	String api;

	@Value("${mosip.kernel.sms.authkey}")
	String authkey;

	@Value("${mosip.kernel.sms.number.length}")
	String length;

	@Test
	public void sendSmsTest() {
	}

	@Test(expected = InvalidNumberException.class)
	public void invalidContactNumberTest() {
		service.sendSms("jsbchb", "hello your otp is 45373");
	}

	@Test(expected = InvalidNumberException.class)
	public void contactNumberMinimumThresholdTest() {
		service.sendSms("78978976", "hello your otp is 45373");
	}

	@Test(expected = InvalidNumberException.class)
	public void contactNumberMaximumThresholdTest() {
		service.sendSms("7897897458673484376", "hello your otp is 45373");
	}

	@Test
	public void validGateWayTest() {
		service.sendSms("8971662474", "helloTest");
	}

}