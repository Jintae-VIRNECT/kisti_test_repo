package com.virnect.service.dto.service.response;

import com.virnect.data.dao.SessionType;
import com.virnect.service.dto.LanguageCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.virnect.data.dao.SessionType.PRIVATE;

@Getter
@Setter
@ApiModel
public class CompanyInfoResponse {
    @ApiModelProperty(value = "Company code", example = "0")
    private int companyCode = 0;

    @ApiModelProperty(value = "Workspace Identifier", position = 1, example = "40f9bbee9d85dca7a34a0dd205aae718")
    private String workspaceId = "";

    @ApiModelProperty(value = "License Name", position = 2, example = "")
    private String licenseName = "";

    @ApiModelProperty(value = "Remote Session Type", position = 3, example = "PRIVATE")
    private SessionType sessionType = PRIVATE;

    @ApiModelProperty(value = "Enable server recording", position = 4, example = "false")
    private boolean recording = false;

    @ApiModelProperty(value = "Enable storage service", position = 5, example = "false")
    private boolean storage = false;

    @ApiModelProperty(value = "Enable text translation", position = 6, example = "false")
    private boolean translation = false;

    @ApiModelProperty(value = "Speech to text Type", position = 7, example = "false")
    private boolean sttSync = false;

    @ApiModelProperty(value = "Speech to text Type", position = 8, example = "false")
    private boolean sttStreaming = false;

    @ApiModelProperty(value = "Enable Text to speech", position = 9, example = "false")
    private boolean tts = false;

    @ApiModelProperty(
            value = "Translation Language codes",
            position = 10,
            dataType = "List"
    )
    @NotNull
    private List<LanguageCode> languageCodes = new ArrayList<>();

    @Override
    public String toString() {
        return "CompanyInfoResponse{" +
                "companyCode='" + companyCode + '\'' +
                ", workspaceId='" + workspaceId + '\'' +
                ", licenseName='" + licenseName + '\'' +
                ", sessionType='" + sessionType + '\'' +
                ", recording='" + recording + '\'' +
                ", storage='" + storage + '\'' +
                ", translation=" + translation +
                ", sttSync=" + sttSync +
                ", sttStreaming=" + sttStreaming +
                ", tts=" + tts +
                '}';
    }
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
