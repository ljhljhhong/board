package com.example.board.controller;

import com.example.board.entity.UploadFile;
import com.example.board.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class MediaController {

	@Autowired
	MediaService mediaService;

	@Autowired
	ResourceLoader resourceLoader;

	// 파일 업로드: 이미지 또는 비디오
	@PostMapping("upload")
	public ResponseEntity<?> fileUpload(@RequestParam("file") MultipartFile file) {
		try {
			String contentType = file.getContentType();

			UploadFile uploadFile;
			if (contentType.startsWith("image")) {
				uploadFile = mediaService.storeImage(file);  // 이미지 저장 로직
				return ResponseEntity.ok().body("/media/image/" + uploadFile.getId());
			} else if (contentType.startsWith("video")) {
				uploadFile = mediaService.storeVideo(file);  // 비디오 저장 로직
				return ResponseEntity.ok().body("/media/video/" + uploadFile.getId());
			} else if (contentType.startsWith("audio")) {
				uploadFile = mediaService.storeAudio(file); // 음성 저장 로직
				return ResponseEntity.ok().body("/media/audio/" + uploadFile.getId());
			} else {
				return ResponseEntity.badRequest().body("지원되지 않는 파일 형식입니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}

	// 파일 다운로드: 이미지 또는 비디오
	@GetMapping("media/{fileType}/{fileId}")
	public ResponseEntity<?> serveFile(@PathVariable String fileType, @PathVariable Long fileId) {
		try {
			UploadFile uploadFile = mediaService.load(fileId);  // 파일 로드
			Resource resource = resourceLoader.getResource("file:" + uploadFile.getFilePath());

			if ("image".equals(fileType) && uploadFile.getContentType().startsWith("image")) {
				return ResponseEntity.ok().body(resource);  // 이미지 파일 반환
			} else if ("video".equals(fileType) && uploadFile.getContentType().startsWith("video")) {
				return ResponseEntity.ok().body(resource);  // 비디오 파일 반환
			} else if ("audio".equals(fileType) && uploadFile.getContentType().startsWith("audio")) {
				return ResponseEntity.ok().body(resource);
			} else {
				return ResponseEntity.badRequest().body("잘못된 파일 형식입니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
	}
}
