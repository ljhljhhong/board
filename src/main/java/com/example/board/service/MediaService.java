package com.example.board.service;

import com.example.board.entity.UploadFile;
import com.example.board.repository.UploadFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MediaService {

	@Autowired
	UploadFileRepository uploadFileRepository;

	// 이미지와 비디오 파일 저장 경로 구분
	private final static Path imageLocation = Paths.get("C://upload", "image");
	private final static Path videoLocation = Paths.get("C://upload", "video");
	private final static Path audioLocation = Paths.get("C://upload", "audio");

	// 이미지 저장 로직
	public UploadFile storeImage(MultipartFile file) throws Exception {
		return store(file, imageLocation);
	}

	// 비디오 저장 로직
	public UploadFile storeVideo(MultipartFile file) throws Exception {
		return store(file, videoLocation);
	}

	// 음성 저장 로직
	public UploadFile storeAudio(MultipartFile file) throws Exception {
		return store(file, audioLocation);
	}

	// 공통 저장 로직
	private UploadFile store(MultipartFile file, Path location) throws Exception {
		try {
			if (file.isEmpty()) {
				throw new Exception("Failed to store empty file " + file.getOriginalFilename());
			}

			String saveFileName = fileSave(location.toString(), file); // 파일 저장
			UploadFile saveFile = UploadFile.builder()
					.fileName(file.getOriginalFilename())
					.saveFileName(saveFileName)
					.contentType(file.getContentType())
					.size(file.getResource().contentLength())
					.registerDate(LocalDateTime.now())
					.filePath(location.toString().replace(File.separatorChar, '/') + '/' + saveFileName)
					.build();

			uploadFileRepository.save(saveFile); // 파일 정보 DB에 저장
			return saveFile;

		} catch (IOException e) {
			throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
		}
	}

	// 파일 로드
	public UploadFile load(Long fileId) {
		return uploadFileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
	}

	// 실제 파일 저장 로직
	private String fileSave(String rootLocation, MultipartFile file) throws IOException {
		File uploadDir = new File(rootLocation);

		if (!uploadDir.exists()) {
			uploadDir.mkdirs(); // 저장 경로가 없으면 디렉토리 생성
		}

		// saveFileName 생성
		UUID uuid = UUID.randomUUID();
		String saveFileName = uuid.toString() + "_" + file.getOriginalFilename();
		File saveFile = new File(rootLocation, saveFileName);
		FileCopyUtils.copy(file.getBytes(), saveFile);

		return saveFileName;
	}
}
