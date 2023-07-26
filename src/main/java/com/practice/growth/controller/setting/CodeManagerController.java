package com.practice.growth.controller.setting;

import com.practice.growth.domain.dto.ApiResult;
import com.practice.growth.domain.dto.CodeDto;
import com.practice.growth.domain.types.ModifyType;
import com.practice.growth.exception.AlreadyEntity;
import com.practice.growth.exception.InvalidRequiredParameter;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.exception.RequiredParamNonException;
import com.practice.growth.service.CodeService;
import com.practice.growth.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/setting/code-manager")
public class CodeManagerController {

    private final CodeService service;
    private final SecurityService securityService;

    @RequestMapping()
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("setting/code-manager");
        modelAndView.addObject("codes", service.getCodeGroup());
        return modelAndView;
    }

    @GetMapping("ajax/code/{groupCode}")
    public @ResponseBody ApiResult getCode(@PathVariable(value = "groupCode") String groupCode) {
        ApiResult<List<CodeDto>> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);

        try {
            List<CodeDto> dtos = service.getCodes(groupCode);
            result.setData(dtos);
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
            result.setCode(ApiResult.RESULT_CODE_NOT_FOUND);
        }

        return result;
    }

    @PostMapping("ajax/code/save")
    public @ResponseBody ApiResult save(@RequestBody CodeDto dto) {
        ApiResult<Long> result = new ApiResult<>(ApiResult.RESULT_CODE_OK);

        try {
            ModifyType modifyType = "new".equals(dto.getModifyType()) ? ModifyType.Register : ModifyType.Modify;
            Long id = service.createOrModifyCode(dto, securityService.getLoginUser(), modifyType);
            result.setData(id);
        } catch (NotFoundException e) {
            result.setCode(ApiResult.RESULT_CODE_NOT_FOUND);
        } catch (InvalidRequiredParameter e) {
            throw new RuntimeException(e);
        } catch (AlreadyEntity e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @PostMapping("ajax/code/remove/{id}")
    public @ResponseBody ApiResult remove(@PathVariable Long id) throws NotFoundException, RequiredParamNonException {
        ApiResult result = new ApiResult(ApiResult.RESULT_CODE_OK);
        service.removeById(id);
        return result;
    }
}
