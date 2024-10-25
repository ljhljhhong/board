package com.example.board.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class CommentDTO {
    private Long id;
    private String content;
    private Long boardId;
    private Long memberId;

    // getters and setters
}
