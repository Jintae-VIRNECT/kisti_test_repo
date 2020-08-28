package com.virnect.process.application;

import com.virnect.process.application.content.ContentRestService;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.error.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
    private final ContentRestService contentRestService;
    private final ProcessRepository processRepository;
    /**
     * 컨텐츠UUID로 컨텐츠 다운로드
     *
     * @param contentUUID
     * @param memberUUID
     * @return
     */
    public ResponseEntity<byte[]> contentDownloadForUUIDHandler(final String contentUUID, final String memberUUID) {

        return contentRestService.contentDownloadForUUIDRequestHandler(contentUUID, memberUUID);
    }

    /**
     * 타겟 데이터로 컨텐츠 다운로드
     *
     * @param targetData
     * @param memberUUID
     * @return
     */
    public ResponseEntity<byte[]> contentDownloadForTargetHandler(final String targetData, final String memberUUID) {

        Process process = Optional.ofNullable(this.processRepository.findByTargetDataAndState(checkParameterEncoded(targetData), State.CREATED))
                .orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_TARGET));

        return contentRestService.contentDownloadForUUIDRequestHandler(process.getContentUUID(), memberUUID);
    }

    /**
     * get방식에서 URLEncode된 값의 URLEncoding이 풀려서 오는 케이스를 체크.
     *
     * @param targetData
     * @return
     */
    protected String checkParameterEncoded(String targetData) {
        String encodedData = null;

        // 컨텐츠 -> 작업으로 복제하여 작업에서 생성된 타겟데이터
        if (targetData.contains("-")) {
            encodedData = targetData;
        }
        // 컨텐츠 -> 작업 전환시에는 타겟데이터가 인코딩 된 상태
        else {
            // 컨텐츠의 타겟데이터는 이미 원본 값이 URLEncoding된 값인데,
            // 실제 서버에서는 servlet container에서 decode하여 URLDecoding된 데이터가 들어오게 된다.
            log.info(">>>>>>>>>>>>>>>>>>> targetData : {}", targetData);

            // 이 와중에 query 파라미터로 받을 경우 '+'가 '공백'으로 리턴된다.
            // PathVariable로 받지 않는 이유는 decoding된 값에 '/'가 들어가는 경우가 있기 때문.
            if (targetData.contains(" ")) {
                // 임시방편으로 공백은 '+'로 치환한다. 더 좋은 방법이 있다면 수정하면 좋을 듯.
                targetData = targetData.replace(" ", "+");
            }
            log.info(">>>>>>>>>>>>>>>>>>> targetData : {}", targetData);

            try {
                // Database에 저장된 targetData는 URLEncoding된 값이므로 인코딩 해줌.
                encodedData = URLEncoder.encode(targetData, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return encodedData;
    }
}
