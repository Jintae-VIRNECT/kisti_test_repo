package com.virnect.data.global.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.obj.BufferStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.file.FileRepository;
import com.virnect.data.domain.file.FileConvertStatus;
import com.virnect.data.dto.UploadResult;
import com.virnect.data.dto.request.file.FileUploadRequest;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.util.obj2gltf.ConvertObjToGltf;
import com.virnect.data.infra.file.IFileManagementService;
import com.virnect.data.infra.utils.FileUtil;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTaskService {

	private final FileUtil fileUtil;
	private final ConvertObjToGltf convertObjToGltf;
	private final IFileManagementService fileManagementService;
	private final FileRepository fileRepository;
	private final ExecutorService executorService;

	public void covertObj2Gltf(
		FileUploadRequest fileUploadRequest
		, String objectName
		, Long gltfId
		, Long binId
	) {
		Callable<FileConvertStatus> callableConvert = () -> {
			File originalFile = fileUtil.convertUploadMultiFileToFile(fileUploadRequest.getFile(), objectName);
			if (originalFile == null) {
				return FileConvertStatus.CONVERTING_FAIL;
			}
			GltfAsset gltfAsset = convertObjToGltf.createGltfAsset(BufferStrategy.BUFFER_PER_FILE, originalFile);
			File gltfFile = convertObjToGltf.extractGltf(gltfAsset, objectName);
			File binFile = convertObjToGltf.extractBin(gltfAsset, objectName);
			FileConvertStatus convertResult = saveObjectFile(fileUploadRequest, gltfFile, binFile);
			FileUtils.deleteDirectory(new File(objectName));
			return convertResult;
		};
		Future<FileConvertStatus> submit = executorService.submit(callableConvert);
		com.virnect.data.domain.file.File gltf = fileRepository.getOne(gltfId);
		com.virnect.data.domain.file.File bin = fileRepository.getOne(binId);
		try {
			if (submit.get() == FileConvertStatus.CONVERTED) {
				updateFileConvertStatus(gltf, FileConvertStatus.CONVERTED);
				updateFileConvertStatus(bin, FileConvertStatus.CONVERTED);
				log.info("[Thread : Obj to glft Processing] Object file(.glft) upload success");
			} else {
				updateFileConvertStatus(gltf, FileConvertStatus.CONVERTING_FAIL);
				updateFileConvertStatus(bin, FileConvertStatus.CONVERTING_FAIL);
				log.info("[Thread : Obj to glft Processing] Object file(.glft) upload fail");
			}
		} catch (InterruptedException | ExecutionException e) {
			updateFileConvertStatus(gltf, FileConvertStatus.CONVERTING_FAIL);
			updateFileConvertStatus(bin, FileConvertStatus.CONVERTING_FAIL);
			log.info("[Thread : Obj to glft Processing] Object file(.glft) upload covertObj2Gltf exception");
			Thread.currentThread().interrupt();
		}
	}

	public FileConvertStatus saveObjectFile(
		FileUploadRequest fileUploadRequest,
		File gltfFile,
		File binFile
	) {
		FileConvertStatus result;
		try (
			InputStream gltfFileInputStream = new FileInputStream(gltfFile);
			InputStream binFileInputStream = new FileInputStream(binFile);
		) {
			ErrorCode gltfUploadResult = upload3dFileToStorage(
				gltfFileInputStream, fileUploadRequest, gltfFile.getName(), gltfFile.getName());
			ErrorCode binUploadResult = upload3dFileToStorage(
				binFileInputStream, fileUploadRequest, binFile.getName(), gltfFile.getName());
			if (gltfUploadResult == ErrorCode.ERR_SUCCESS && binUploadResult == ErrorCode.ERR_SUCCESS) {
				result = FileConvertStatus.CONVERTED;
			} else {
				result = FileConvertStatus.CONVERTING_FAIL;
			}
		} catch (Exception e) {
			log.info("[Thread : Obj to glft Processing] Object file(.glft) upload saveObjectFile exception");
			result = FileConvertStatus.CONVERTING_FAIL;
		}
		return result;
	}

	private ErrorCode upload3dFileToStorage(
		InputStream inputStream,
		FileUploadRequest objectFileUploadRequest,
		String objectName,
		String objectPath
	) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
		UploadResult uploadResult = fileManagementService.objectFileUpload(
			inputStream,
			fileUtil.generateDirPath(
				objectFileUploadRequest.getWorkspaceId(),
				objectFileUploadRequest.getSessionId()
			),
			objectFileUploadRequest.getFile().getOriginalFilename(),
			objectName,
			objectPath
		);
		return uploadResult.getErrorCode();
	}

	private void updateFileConvertStatus(
		com.virnect.data.domain.file.File file,
		FileConvertStatus fileConvertStatus
	) {
		file.setFileConvertStatus(fileConvertStatus);
		fileRepository.save(file);
	}

}
