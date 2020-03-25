package com.virnect.process.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MonthlyStatisticsResponse {

    @ApiModelProperty(value = "일별 총계", notes = "일별 공정률 및 공정수의 총계")
    private List<DailyTotalResponse> dailyTotal;

    @Builder
    public MonthlyStatisticsResponse(List<DailyTotalResponse> dailyTotal) {
        this.dailyTotal = dailyTotal;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class DailyTotalResponse {
        @ApiModelProperty(value = "식별자", notes = "DB row 식별자. 차후 확장성을 위해....", example = "0")
        private long id;
        @ApiModelProperty(value = "공정률", notes = "그 달의 해당일에 총 공정률", position = 1, example = "23")
        private int totalRate;
        @ApiModelProperty(value = "공정수", notes = "그 달의 해당일에 총 공정수", position = 2, example = "10")
        private int totalProcesses;
        @ApiModelProperty(value = "날짜", notes = "그 달의 해당일 - timezone : UTC", position = 3, example = "2020-03-10")
        private LocalDate onDay;

        @Builder
        public DailyTotalResponse(long id, int totalRate, int totalProcesses, LocalDate onDay) {
            this.id = id;
            this.totalRate = totalRate;
            this.totalProcesses = totalProcesses;
            this.onDay = onDay;
        }
    }
}
