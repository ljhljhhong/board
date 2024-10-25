
package com.example.board.service;

import com.example.board.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    CommentDTO saveComment(CommentDTO commentDTO);
    List<CommentDTO> getCommentsByBoardId(Long boardId);
    void deleteComment(Long commentId);
}
