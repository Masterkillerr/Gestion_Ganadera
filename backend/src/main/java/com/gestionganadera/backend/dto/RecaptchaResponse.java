package com.gestionganadera.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class RecaptchaResponse {
    private boolean success;
    private double score;
    private String action;
    private String challenge_ts;
    private String hostname;
    @JsonProperty("error-codes")
    private List<String> errorCodes;
}
