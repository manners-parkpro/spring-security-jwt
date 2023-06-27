package com.practice.growth.controller.board;

import com.practice.growth.domain.dto.ApiResult;
import com.practice.growth.domain.dto.NoticeDto;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.service.NoticeService;
import com.practice.growth.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/board/notice/")
public class NoticeController {

    private final NoticeService service;
    private final SecurityService securityService;

    @RequestMapping({"new", "edit/{id}"})
    public ModelAndView edit(@PathVariable(name = "id", required = false) Long id) throws NotFoundException {
        ModelAndView modelAndView = new ModelAndView("board/notice/editor");

        if (id != null)
            modelAndView.addObject("dto", service.getNotice(id));

        return modelAndView;
    }

    @RequestMapping("ajax/save")
    @ResponseBody
    public ApiResult<Long> save(@RequestBody NoticeDto dto) throws NotFoundException {
        ApiResult<Long> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);

        try {
            Long id = service.createOrModifyNotice(dto, securityService.getLoginUser());
            result.setData(id);
        } catch (NotFoundException e) {
            result.setCode(ApiResult.RESULT_CODE_NOT_FOUND);
        }

        return result;
    }
}
