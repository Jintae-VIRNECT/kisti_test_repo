package com.virnect.data.dto.response;

import com.virnect.data.dao.SessionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@ApiModel
public class CompanyInfoResponse {
    @ApiModelProperty(value = "Company code", example = "0")
    private int companyCode;

    @ApiModelProperty(value = "Enable translation", position = 4, example = "false")
    private boolean translation;

    @ApiModelProperty(value = "Remote Session Type", position = 5, example = "PRIVATE")
    private SessionType sessionType;
}

/*

 "allowedRoomTypes":["PRIVATE", "OPEN", "PUBLIC"], // 허용 된 Room 타입
         "isTranslationAllowed" : true or false, //번역기능 허용 여부.
         "allowedLanguageCodes":[{"text" : "한국어", "code" : "ko-KR"}, {"text":"
         English", "code":"en-US"}...], //허용된 번역 언어.
         "allowedSttTypes":["STREAMING", "SYNC"], //허용된 STT 타입
         "participantType":"PUBLISHER" or "SUBSCRIBER", //지정 된 참가자 타입
         "allowedParticipantStreams":["VIDEO", "AUDIO"], // 참가자 허용 된 송출 스트림 타입 (참가자 타입이 "PUBLISHER" 일 경우)
         "leaderType" : "PUBLISHER" or "SUBSCRIBER", // 지정된 리더 타입
         "allowedLeaderStreams":["VIDEO", "AUDIO"] // 리더 허용 된 송출 스트림 타입 (리더 타입이 "PUBLISHER" 일 경우)*/
