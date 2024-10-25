
package com.example.board.service;

import com.example.board.dto.CommentDTO;
import com.example.board.repository.CommentRepository;
import com.example.board.service.CommentService;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public CommentDTO saveComment(CommentDTO commentDTO) {
        // Convert DTO to Entity, save, and return DTO
        return null;  // Placeholder
    }

    @Override
    public List<CommentDTO> getCommentsByBoardId(Long boardId) {
        // Fetch comments by boardId
        return null;  // Placeholder
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
