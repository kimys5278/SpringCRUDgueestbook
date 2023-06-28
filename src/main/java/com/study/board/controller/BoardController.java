package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {
    @Autowired
    private BoardService boardService;
    @GetMapping("/board/write") //localhosst 8080에 board/write에 접속
    public String boardWriteForm(){
        return "boardwrite"; //어떤 html 파일로 보내줄거니
    }
    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws  Exception{

        boardService.write(board,file);

        model.addAttribute("message","글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model, @PageableDefault(page =0,size=10,sort="id",
            direction = Sort.Direction.DESC) Pageable pageable,String searchKeyword) {


        Page<Board> list = null;
        if(searchKeyword == null){
            list = boardService.boardList(pageable);
        }else{
            list = boardService.boardSearchList(searchKeyword,pageable);
        }



        int nowPage = list.getPageable().getPageNumber()+1;
        int startPage = Math.max(nowPage - 4,1);
        int endPage = Math.min(nowPage + 5 ,list.getTotalPages());


        model.addAttribute("list", list);
        //list라는 이름으로 보낼건데,boardService에 boardList()메서드를 실행하면 리스트를 반환하는데
        //그 리스트를 넘긴다.

        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);

        return "boardlist";
    }

    @GetMapping("/board/view")
    public String boardView(Model model,Integer id){
        model.addAttribute("board",boardService.boardView(id));
        return "boardview";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id){

        boardService.boardDelete(id);
        return "redirect:/board/list";
    }
    //게시물 수정 처리
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id,
                              Model model){
        model.addAttribute("board",boardService.boardView(id));

        return "boardmodify";
    }

    @PostMapping("/board/update/{id}")
    public  String boardUpdate(@PathVariable("id")Integer id,Board board,Model model,MultipartFile file)throws  Exception{

        Board boardTemp = boardService.boardView(id);
        boardTemp.setTitle(board.getTitle());
        boardTemp.setContent(board.getContent());

        boardService.write(boardTemp,file);

        model.addAttribute("message","게시글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "message";
    }




}
