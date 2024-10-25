package com.example.board.controller;

import com.example.board.dto.MemberDTO;
import com.example.board.entity.Board;
import com.example.board.service.BoardService;
import com.example.board.dto.BoardDTO;
import com.example.board.dto.PageRequestDTO;
import com.example.board.dto.PageResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class BoardController {

    private final BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService){
        this.boardService = boardService;
    }

    // 메인 목록 화면으로 리다이렉트
    @GetMapping("/")
    public String main(){
        log.info("list");
        return "redirect:/login";
    }

    // 게시글 목록 화면
    @GetMapping("/list")
    public String list(PageRequestDTO pageRequestDTO, Model model, HttpSession session) {
        log.info("list pageRequestDTO:{}", pageRequestDTO);

        // 세션에서 로그인한 사용자 정보 가져오기
        MemberDTO member = (MemberDTO) session.getAttribute("member");

        // 사용자 정보가 있으면 모델에 추가
        if (member != null) {
            model.addAttribute("member", member); // 로그인한 사용자 정보를 모델에 추가
        }

        // 검색어 기본값 설정
        PageRequestDTO requestDTO = pageRequestDTO;
        requestDTO.setType(requestDTO.getType() == null ? "a" : requestDTO.getType());

        // 게시물 목록 로드
        PageResultDTO<BoardDTO, Board> result = boardService.getListV2(pageRequestDTO);
        model.addAttribute("result", result);
        model.addAttribute("request", requestDTO);

        return "/list"; // list.html 렌더링
    }


    // 게시글 신규 작성 화면
    @GetMapping("/write")
    public String write(@SessionAttribute(name = "member", required = false) MemberDTO member){
        log.info("write");
        if(member == null){
            return "redirect:/login";
        }
        return "/write";
    }

    // 게시글 신규 저장
    @PostMapping("/write")
    public String doWrite(BoardDTO dto, RedirectAttributes redirectAttributes,
                          @SessionAttribute(name = "member", required = false) MemberDTO member){
        log.info("doWrite dto:{}", dto);
        if(member == null){
            return "redirect:/login";
        }

        // 작성자 이메일을 세션에서 주입
        dto.setEmail(member.getEmail());
        Long id = boardService.register(dto);
        if(id != null && id > 0L){
            redirectAttributes.addFlashAttribute("msg", "등록 되었습니다");
        }
        return "redirect:/list";
    }

    // 게시글 상세 조회 화면
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model){
        log.info("detail");
        BoardDTO boardDTO = boardService.findById(id, true);
        if(boardDTO == null){
            return "redirect:/list";
        }
//        model.addAttribute("dto", boardDTO);

        model.addAttribute("dto", boardDTO);
        return "/detail";
    }

    // 게시글 수정 화면
    @GetMapping("/modify/{id}")
    public String modify(@PathVariable Long id, Model model,
                         @SessionAttribute(name = "member", required = false) MemberDTO member){
        log.info("modify");
        if(member == null){
            return "redirect:/login";
        }
        BoardDTO boardDTO = boardService.findById(id, false);
        if(boardDTO == null){
            return "redirect:/list";
        }
        model.addAttribute("dto", boardDTO);
        return "/modify";
    }

    // 게시글 수정 처리
    @PostMapping("/modify")
    public String modifySubmit(BoardDTO dto, RedirectAttributes redirectAttributes){
        log.info("update dto:{}", dto);
        BoardDTO updatedBoard = boardService.update(dto);
        if(updatedBoard == null || updatedBoard.getId() == null){
            return "redirect:/list";
        }
        return "redirect:/detail/" + dto.getId();
    }

    // 게시글 삭제 처리
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Object> remove(@RequestBody Map<String, Object> map){
        log.info("remove map:{}", map);
        Long id = Long.valueOf((int) map.get("id"));
        if(id == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("삭제를 하지 못했습니다.");
        }

        int result = boardService.remove(id);
        Map<String, Object> res = new HashMap<>();
        res.put("result", result);
        res.put("msg", result > 0 ? "삭제가 되었습니다." : "삭제를 하지 못했습니다.");
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
