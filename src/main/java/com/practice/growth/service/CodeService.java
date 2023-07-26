package com.practice.growth.service;

import com.practice.growth.configurations.component.CodeCache;
import com.practice.growth.domain.dto.CodeDto;
import com.practice.growth.domain.entity.Account;
import com.practice.growth.domain.entity.Code;
import com.practice.growth.domain.types.ModifyType;
import com.practice.growth.domain.types.YNType;
import com.practice.growth.exception.AlreadyEntity;
import com.practice.growth.exception.InvalidRequiredParameter;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.exception.RequiredParamNonException;
import com.practice.growth.repository.CodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Service
public class CodeService {

    private final CodeRepository repository;
    //private final CodeCache codeCache;

    public static String QUESTION_CD = "QUESTION_CD";

    @Transactional
    public Long createOrModifyCode(CodeDto dto, Account account, ModifyType modifyType) throws NotFoundException, InvalidRequiredParameter, AlreadyEntity {
        Code code;

        if (dto.getCode() == null)
            throw new InvalidRequiredParameter();

        Optional<Code> findCode;
        if (ModifyType.Register.equals(modifyType)) {
            findCode = repository.findByCodeIgnoreCase(dto.getCode());
            if (findCode.isPresent())
                throw new AlreadyEntity("AlreadyEntity Code");
        } else {
            findCode = repository.findByCodeIgnoreCase(dto.getCode());

            if (findCode.isEmpty())
                throw new NotFoundException("Code is Not found: " + dto.getCode().toUpperCase(), NotFoundException.CODE_NOT_FOUND);
        }

        if (findCode.isPresent())
            code = findCode.get();
        else {
            code = new Code();
            if ("code".equals(dto.getCodeType())) {
                //부모 메뉴가 있는지 여부
                if (dto.getGroup() != null && dto.getGroup().getCode() != null) {
                    Optional<Code> findGroup = repository.findByCodeIgnoreCase(dto.getGroup().getCode());
                    if (findGroup.isEmpty())
                        throw new NotFoundException("Group Code is Not found: " + dto.getGroup().getCode().toUpperCase(), NotFoundException.GROUP_CODE_NOT_FOUND);

                    Code group = findGroup.get();
                    group.addChildren(code);
                }
            }
        }

        //코드가 변경되는 경우가 있음
        code.setCode(dto.getCode().toUpperCase());
        code.setValue(dto.getValue().toUpperCase());
        code.setDescription(dto.getDescription());
        code.setActiveYn(dto.getActiveYn());
        code.setLastModifier(account);

        repository.save(code);

        //codeCache.reload();

        return code.getId();
    }

    /**
     * 코드 갖고 오기
     *
     * @return
     */
    public List<CodeDto> getCodeHierarchy() {
        List<Code> codeGroups = repository.findByGroupIsNullAndActiveYn(YNType.Y, Sort.by(Sort.Direction.ASC, "sortOrder"));
        List<CodeDto> codeList = new ArrayList<>();

        codeGroups.stream().forEach(c -> codeList.add(new CodeDto(c, true)));

        return codeList;
    }

    /**
     * 코드 갖고 오기
     *
     * @return
     */
    public List<CodeDto> getCodeGroup() {
        List<Code> codeGroups = repository.findByGroupIsNull(Sort.by(Sort.Direction.ASC, "sortOrder"));
        List<CodeDto> codeList = new ArrayList<>();

        codeGroups.stream().forEach(c -> codeList.add(new CodeDto(c)));

        return codeList;
    }

    /**
     * 하위 코드 갖고 오기
     *
     * @return
     */
    public List<CodeDto> getCodes(String groupCode) throws NotFoundException {
        List<Code> codeGroups = repository.findByGroup_CodeIgnoreCaseAndActiveYn(groupCode, YNType.Y, Sort.by(Sort.Direction.ASC, "sortOrder"));
        List<CodeDto> codeList = new ArrayList<>();

        codeGroups.stream().forEach(c -> codeList.add(new CodeDto(c, true)));

        return codeList;
    }

    /**
     * 코드정보 갖고오기
     *
     * @param code
     * @return
     * @throws NotFoundException
     */
    public CodeDto getCode(String code) throws NotFoundException {
        Optional<Code> findCode = repository.findByCodeIgnoreCase(code);
        if (findCode.isEmpty())
            throw new NotFoundException(NotFoundException.CODE_NOT_FOUND);

        return new CodeDto(findCode.get());
    }

    public Code getCodeEntity(String code) throws NotFoundException {
        Optional<Code> findCode = repository.findByCodeIgnoreCase(code);
        if (findCode.isEmpty())
            throw new NotFoundException(NotFoundException.CODE_NOT_FOUND);

        return findCode.get();
    }

    @Transactional
    public void removeById(Long id) throws NotFoundException, RequiredParamNonException {
        if (id == null)
            throw new RequiredParamNonException("RequiredParamNonException");

        Optional<Code> optCode = repository.findById(id);
        if (optCode.isEmpty())
            throw new NotFoundException("Code is not found", NotFoundException.CODE_NOT_FOUND);

        Code code = optCode.get();
        code.setActiveYn(YNType.N);

        repository.save(code);
    }
}
