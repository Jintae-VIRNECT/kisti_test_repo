package com.virnect.process.application;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.process.application.content.ContentRestService;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.State;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.exception.ProcessServiceException;
import com.virnect.process.global.error.ErrorCode;

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
	public ResponseEntity<byte[]> contentDownloadForUUIDHandler(
		String contentUUID, String memberUUID, String workspaceUUID
	) {
		//1. contentUUID 정보가 없을때
		Process process = processRepository.findByContentUUIDAndStatus(contentUUID, State.CREATED)
			.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_PROCESS));

		//2. 현재 사용자에게 할당된 작업이 아닐때
		ownerValidCheck(memberUUID, process);

		//3. 현재 접속한 워크스페이스에 해당하는 작업이 아닐 때
		workspaceValidCheck(workspaceUUID, process);

		//4.대기 중이거나 마감 된 작업일 때
		conditionValidCheck(process);

		return contentRestService.contentDownloadForUUIDRequestHandler(contentUUID, memberUUID, workspaceUUID);
	}

	/**
	 * 타겟 데이터로 컨텐츠 다운로드
	 *
	 * @param targetData
	 * @param membearUUID
	 * @return
	 */
	public ResponseEntity<byte[]> contentDownloadForTargetHandler(
		String targetData, String memberUUID, String workspaceUUID
	) {
		//0. 타겟데이터 체크
		String encodedData = checkParameterEncoded(targetData);
		Process process = processRepository.findByTargetDataAndState(encodedData, State.CREATED).orElse(null);
		if (process == null) {
			process = processRepository.findByTargetDataAndState(
				toUpperCaseUrlEncodedString(encodedData), State.CREATED)
				.orElseThrow(() -> new ProcessServiceException(ErrorCode.ERR_NOT_FOUND_TARGET));
		}

		//1.현재 사용자에게 할당 된 작업이 아닐 때
		ownerValidCheck(memberUUID, process);
		//2. 현재 접속한 워크스페이스에 해당하는 작업이 아닐 때
		workspaceValidCheck(workspaceUUID, process);
		//3.대기 중이거나 마감 된 작업일 때
		conditionValidCheck(process);

		//작업 전환의 경우에는 타겟정보를 작업서버에 만들어놓기 때문에 contents 서버에 타겟정보가 없다. 그래서 타겟정보로 다운로드하는 것으로 요청하면 안된다.
		return contentRestService.contentDownloadForUUIDRequestHandler(
			process.getContentUUID(), memberUUID, workspaceUUID);
	}

	private String toUpperCaseUrlEncodedString(String urlString) {
		Pattern pattern = Pattern.compile("%[0-9A-Fa-f]{2}");
		StringBuffer sb = new StringBuffer();
		Matcher m = pattern.matcher(urlString);

		while (m.find()) {
			m.appendReplacement(sb, m.group().toUpperCase());
		}
		m.appendTail(sb);
		return sb.toString();
	}

	private void conditionValidCheck(Process process) {
		if (process.getConditions().equals(Conditions.WAIT) ||
			process.getConditions().equals(Conditions.FAILED) ||
			process.getConditions().equals(Conditions.SUCCESS) ||
			process.getConditions().equals(Conditions.FAULT)) {
			log.error(
				"[CONTENT DOWNLOAD][PROCESS CONDITION CHECK] process is not in progress. content uuid : [{}], process current condition : [{}],",
				process.getContentUUID(), process.getConditions()
			);
			throw new ProcessServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD_INVALID_CONDITION);
		}
	}

	private void workspaceValidCheck(String workspaceUUID, Process process) {
		if (!process.getWorkspaceUUID().equals(workspaceUUID)) {
			log.error(
				"[CONTENT DOWNLOAD][PROCESS WORKSPACE CHECK] process current workspace uuid : [{}], request workspace uuid : [{}]",
				process.getWorkspaceUUID(), workspaceUUID
			);
			throw new ProcessServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
	}

	/**
	 * 컨텐츠 다운로드 가능한 유저 체크
	 * 컨텐츠의 업로드 유저이거나, 작업의 학위작업 담당자여야 한다.
	 * @param memberUUID - 다운로드 요청 유저
	 * @param process - 다운로드 요청 작업
	 */
	private void ownerValidCheck(String memberUUID, Process process) {
		List<String> subTaskWorkerUUIDList = process.getSubProcessList()
			.stream()
			.map(SubProcess::getWorkerUUID)
			.collect(Collectors.toList());

		boolean anySubTaskWorkerUUID = subTaskWorkerUUIDList.stream().anyMatch(s -> s.equals(memberUUID));

		if (!anySubTaskWorkerUUID) {
			log.error(
				"[CONTENT DOWNLOAD][PROCESS WORKSPACE CHECK] subTask worker uuid list : [{}], request user uuid : [{}],",
				String.join(",", subTaskWorkerUUIDList), memberUUID
			);
			throw new ProcessServiceException(ErrorCode.ERR_CONTENT_DOWNLOAD);
		}
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
				log.info(">>>>>>>>>>>>>>>>>>> encodedData : {}", encodedData);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return encodedData;
	}
}
