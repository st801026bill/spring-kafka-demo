package com.bill.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PositionMessageReqDto {
	/*
	 * 	String -> position-message，Consumer 會做處理
	 * 	Integer -> Success
	 */
	@NotBlank(message = "ID為必輸入欄位")
    @Schema(description = "是否處理例外", required = true, example = "1")
    @Size(max = 100, message = "ID欄位長度過長")
    @JsonProperty("ID")
	private String id;

	/*
	 *	retry : Consumer會收到exception，每隔五秒重試一次，最多執行3次
	 *	other : Success
	 */
	@NotBlank(message = "Message為必輸入欄位")
    @Schema(description = "是否處理例外", required = true, example = "1")
    @Size(max = 100, message = "Message欄位長度過長")
    @JsonProperty("Message")
	private String message;
}
