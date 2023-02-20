package io.mosip.kernel.smsserviceprovider.sns.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ResponseMetaData {
    private Object HTTPHeaders;
    private String HTTPStatusCode;
    private String RequestId;
    private Integer RetryAttempts;
}
