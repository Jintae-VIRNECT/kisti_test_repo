package com.virnect.process.domain;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ProgressManager {
    // CLOSED가 아니고 DELETED도 아닐 때의 상태
    private static final EnumMap<RateRange, EnumMap<Period, EnumMap<YesOrNo, Conditions>>> CREATED_OR_UPDATED_AND_NOT_CLOSED_AND_NOT_DELETED = new EnumMap<RateRange, EnumMap<Period, EnumMap<YesOrNo, Conditions>>>(RateRange.class) {
        {
            put(RateRange.MIN, new EnumMap<Period, EnumMap<YesOrNo, Conditions>>(Period.class) {
                {
                    put(Period.BEFORE, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.WAIT);
                        put(YesOrNo.NO, Conditions.WAIT);
                    }});
                    put(Period.BETWEEN, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.UNPROGRESSING);
                        put(YesOrNo.NO, Conditions.UNPROGRESSING);
                    }});
                    put(Period.AFTER, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.FAILED);
                        put(YesOrNo.NO, Conditions.FAILED);
                    }});
                }
            });
            put(RateRange.MIDDLE, new EnumMap<Period, EnumMap<YesOrNo, Conditions>>(Period.class) {
                {
                    put(Period.BEFORE, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.WAIT);
                        put(YesOrNo.NO, Conditions.WAIT);
                    }});
                    put(Period.BETWEEN, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.INCOMPLETED);
                        put(YesOrNo.NO, Conditions.PROGRESSING);
                    }});
                    put(Period.AFTER, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.FAILED);
                        put(YesOrNo.NO, Conditions.FAILED);
                    }});
                }
            });
            put(RateRange.MAX, new EnumMap<Period, EnumMap<YesOrNo, Conditions>>(Period.class) {
                {
                    put(Period.BEFORE, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.WAIT);
                        put(YesOrNo.NO, Conditions.WAIT);
                    }});
                    put(Period.BETWEEN, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.COMPLETED);
                        put(YesOrNo.NO, Conditions.INCOMPLETED);
                    }});
                    put(Period.AFTER, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.SUCCESS);
                        put(YesOrNo.NO, Conditions.FAULT);
                    }});
                }
            });
        }
    };

    // CLOSED이거나 DELETED일 때의 상태
    private static final EnumMap<RateRange, EnumMap<Period, EnumMap<YesOrNo, Conditions>>> NOT_CREATED_AND_NOT_UPDATED_AND_CLOSED_OR_DELETED = new EnumMap<RateRange, EnumMap<Period, EnumMap<YesOrNo, Conditions>>>(RateRange.class) {
        {
            put(RateRange.MIN, new EnumMap<Period, EnumMap<YesOrNo, Conditions>>(Period.class) {
                {
                    put(Period.BEFORE, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.FAILED);
                        put(YesOrNo.NO, Conditions.FAILED);
                    }});
                    put(Period.BETWEEN, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.FAILED);
                        put(YesOrNo.NO, Conditions.FAILED);
                    }});
                    put(Period.AFTER, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.FAILED);
                        put(YesOrNo.NO, Conditions.FAILED);
                    }});
                }
            });
            put(RateRange.MIDDLE, new EnumMap<Period, EnumMap<YesOrNo, Conditions>>(Period.class) {
                {
                    put(Period.BEFORE, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.FAILED);
                        put(YesOrNo.NO, Conditions.FAILED);
                    }});
                    put(Period.BETWEEN, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.FAILED);
                        put(YesOrNo.NO, Conditions.FAILED);
                    }});
                    put(Period.AFTER, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.FAILED);
                        put(YesOrNo.NO, Conditions.FAILED);
                    }});
                }
            });
            put(RateRange.MAX, new EnumMap<Period, EnumMap<YesOrNo, Conditions>>(Period.class) {
                {
                    put(Period.BEFORE, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.SUCCESS);
                        put(YesOrNo.NO, Conditions.FAULT);
                    }});
                    put(Period.BETWEEN, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.SUCCESS);
                        put(YesOrNo.NO, Conditions.FAULT);
                    }});
                    put(Period.AFTER, new EnumMap<YesOrNo, Conditions>(YesOrNo.class) {{
                        put(YesOrNo.YES, Conditions.SUCCESS);
                        put(YesOrNo.NO, Conditions.FAULT);
                    }});
                }
            });
        }
    };

    // TODO : Conditions, State가 UI상으로 유저에게 명확히 명시되어 보여져야 함. 이유는 공정생성과 컨텐츠의 상태가 엮여있기 때문.  공정 종료, 편집의 기능은 state가 closed가 아닐 때만 가능. 추가생성은 언제든 가능.

    /**
     * 상태를 룰에 의하여 종합 판단하여 결과를 도출
     * 공정생명주기와 공정률, 일정에 따른 룰을 상수로 정하였고.
     * 현재 공정의 생명주기에서의 생명주기상태와 현재 공정률 그리고 오늘이 일정상 어떤 상태인지에 따른 종합 판담함.
     *
     * @param startDate  - 시작일
     * @param endDate    - 종료일
     * @param rate       - 공정률
     * @param state      - 생명주기의 공정상태
     * @param reportedYN - 보고여부
     * @param result     - 작업정상여부
     * @return - 상태값
     */
    private static Conditions getConditions(LocalDateTime startDate, LocalDateTime endDate, int rate, State state, YesOrNo reportedYN, YesOrNo result) {
        // 적용조건(보고 또는 작업정상여부))
        YesOrNo yn = result;

        // 공정 State
        EnumMap<RateRange, EnumMap<Period, EnumMap<YesOrNo, Conditions>>> baseRule = null;
        if (state.equals(State.CREATED) || state.equals(State.UPDATED)) {
            // 정상공정 수행일 때
            baseRule = CREATED_OR_UPDATED_AND_NOT_CLOSED_AND_NOT_DELETED;
        } else if (state.equals(State.CLOSED) || state.equals(State.DELETED)) {
            // 공정이 종료되거나 삭제되었을 때
            baseRule = NOT_CREATED_AND_NOT_UPDATED_AND_CLOSED_OR_DELETED;
        }


        // 공정 RateRange
        RateRange rateRange;
        LocalDateTime nowTime = LocalDateTime.now();
        switch (rate) {
            case 0:
                // 공정률 0일 때
                rateRange = RateRange.MIN;
                break;
            case 100:
                // 공정률 100일 때
                rateRange = RateRange.MAX;
                break;
            default:
                // 공정률 1~99일 때
                rateRange = RateRange.MIDDLE;
        }


        // 공정 Period
        Period period;
        if (nowTime.isBefore(startDate)) {
            // 일정전
            period = Period.BEFORE;
        } else if (nowTime.isAfter(endDate)) {
            // 일정후
            period = Period.AFTER;
        } else {
            // 일정중
            period = Period.BETWEEN;
        }


        // 적용조건
        if (baseRule == CREATED_OR_UPDATED_AND_NOT_CLOSED_AND_NOT_DELETED && period == Period.BETWEEN) {
            yn = reportedYN;
        }

        // 종합 conditions
        assert baseRule != null;
        return baseRule.get(rateRange).get(period).get(yn);
    }

    public static Integer getAllProcessesTotalProgressRate(List<Process> processes) {
        AtomicInteger rate = new AtomicInteger(0);
        if (processes.size() > 0) {
            int count = 0;
            // 세부공정들의 공정률을 합산
            for (Process process : processes) {
                rate.set(process.getProgressRate() + rate.get());
                count++;
            }
            // 공정의 통계
            rate.set((int) Math.floor(count == 0 ? 0 : (double) rate.get() / (double) count));
        }
        return rate.get();
    }


    // 공정의 공정률
    public static Integer getProcessProgressRate(Process process) {
        AtomicInteger rate = new AtomicInteger(0);
        if (process.getSubProcessList().size() > 0) {
            int count = 0;
            // 세부공정들의 공정률을 합산
            for (SubProcess subProcess : process.getSubProcessList()) {
                rate.set(subProcess.getProgressRate() + rate.get());
                count++;
            }
            // 공정의 통계
            rate.set((int) Math.floor(count == 0 ? 0 : (double) rate.get() / (double) count));
        }
        return rate.get();
    }

    // 공정의 상태
    public static Conditions getProcessConditions(Process process) {
        // 초기화
        YesOrNo reportedYN = YesOrNo.YES;
        YesOrNo resultYN = YesOrNo.YES;

        // 세부공정들의 정보 확인
        for (SubProcess subProcess : process.getSubProcessList()) {
            // 보고하지 않은 세부공정이 하나라도 있다면 공정은 보고안됨.
            if (checkAllReportedIs(subProcess.getJobList()).equals(YesOrNo.NO)) {
                reportedYN = YesOrNo.NO;
            }
            // 결과가 하나라도 비정상이라면 공정은 비정상
            if (checkAllResult(subProcess.getJobList()).equals(Result.NOK)) {
                resultYN = YesOrNo.NO;
            }
        }

        return getConditions(process.getStartDate()
                , process.getEndDate()
                , getProcessProgressRate(process)
                , process.getState()
                , reportedYN
                , resultYN);
    }


    // 세부공정의 공정률
    public static Integer getSubProcessProgressRate(SubProcess subProcess) {
        AtomicInteger rate = new AtomicInteger(0);
        if (subProcess.getJobList().size() > 0) {
            int count = 0;
            // 세부공정들의 공정률을 합산
            for (Job job : subProcess.getJobList()) {
                rate.set(job.getProgressRate() + rate.get());
                count++;
            }
            // 세부공정들의 통계
            rate.set((int) Math.floor(count == 0 ? 0 : (double) rate.get() / (double) count));
        }

        return rate.get();
    }

    // 세부공정의 상태
    public static Conditions getSubProcessConditions(SubProcess subProcess) {
        return getConditions(subProcess.getStartDate()
                , subProcess.getEndDate()
                , getSubProcessProgressRate(subProcess)
                , subProcess.getProcess().getState()
                , checkAllReportedIs(subProcess.getJobList())
                , checkAllResult(subProcess.getJobList()));
    }


    // 작업의 공정률
    public static Integer getJobProgressRate(Job job) {
        AtomicInteger rate = new AtomicInteger(0);
        int count = 0;
        if (job.getReportList().size() > 0) {
            // 세부공정들의 공정률을 합산
            for (Report report : job.getReportList()) {
                rate.set(report.getProgressRate() + rate.get());
                count++;
            }
        }
        // 작업의 통계
        if (count < 1) {
            // job 하위에 세부작업이 없는 경우 1개의 세부작업을 완료한 것으로 간주 함. 단, result를 확인
            if (job.getResult() == Result.OK) {
                rate.set(100);
            }
        } else {
            rate.set((int) Math.floor(count == 0 ? 0 : (double) rate.get() / (double) count));
        }

        return rate.get();
    }

    // 리포트의 공정률
    public static Integer getReportProgressRate(Report report) {
        AtomicInteger rate = new AtomicInteger(0);
        if (report.getItemList().size() > 0) {
            int ok = 0, count = 0;
            // 세부공정들의 공정률을 합산
            for (Item item : report.getItemList()) {
                if (item.getResult() == Result.OK) {
                    ok++;
                }
                count++;
            }
            // 리포트의 통계
            rate.set((int) Math.floor((double) ok / (double) count * 100));
        }

        return rate.get();
    }

    // 작업의 상태
    public static Conditions getJobConditions(Job job) {
        // 초기화
        YesOrNo reportedYN = job.getIsReported();
        YesOrNo resultYN = YesOrNo.YES;

        // 세부공정들의 정보 확인
        for (Report report : job.getReportList()) {
            // 결과가 하나라도 비정상이라면 공정은 비정상
            for (Item item : report.getItemList()) {
                if (item.getResult().equals(Result.NOK)) {
                    resultYN = YesOrNo.NO;
                }
            }
        }

        return getConditions(job.getSubProcess().getStartDate()
                , job.getSubProcess().getEndDate()
                , getJobProgressRate(job)
                , job.getSubProcess().getProcess().getState()
                , reportedYN
                , resultYN);
    }

    // 공정 보고가 모두 되었는지 여부
    public static YesOrNo checkAllReportedIs(List<Job> jobs) {
        YesOrNo returnYN = YesOrNo.YES;
        for (Job job : jobs) {
            if (job.getIsReported().equals(YesOrNo.NO)) {
                returnYN = YesOrNo.NO;
            }
        }
        return returnYN;
    }


    public static YesOrNo checkAllResult(List<Job> jobs) {
        Result result = Result.OK;
        Result reportResult = Result.OK;
        // 하위의 작업 아이템 결과가 하나라도 NOK라면 세부공정의 결과는 비정상
        for (Job job : jobs) {
            for (Report report : job.getReportList()) {
                for (Item item : report.getItemList()) {
                    if (Optional.of(item).map(Item::getResult).orElseGet(() -> Result.NOK).equals(Result.NOK)) {
                        reportResult = Result.NOK;
                    }
                }
            }
        }

        result = reportResult == Result.NOK ? Result.NOK : result;

        return result.equals(Result.NOK) ? YesOrNo.NO : YesOrNo.YES;
    }
}
