package com.practice.growth.configurations.component;

import com.practice.growth.domain.dto.CodeDto;
import com.practice.growth.exception.NotFoundException;
import com.practice.growth.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CodeCache extends ConcurrentHashMap<String, List<CodeDto>> {

    private final CodeService codeService;

    @PostConstruct
    public void init() {
        reload();
    }

    /**
     * 갱신
     */
    public void reload() {
        this.clear();

        List<CodeDto> groups = codeService.getCodeHierarchy();
        for (CodeDto g : groups) {
            this.put(g.getCode(), g.getChildren());
        }
    }

    public String getValue(String groupCode, String code) throws NotFoundException {
        if (this.containsKey(groupCode)) {
            Optional<CodeDto> findCode = this.get(groupCode).stream().filter(c -> code.equals(c.getCode())).findFirst();
            if (findCode.isEmpty()) throw new NotFoundException(NotFoundException.CODE_NOT_FOUND);
            return findCode.get().getValue();
        } else {
            throw new NotFoundException(NotFoundException.GROUP_CODE_NOT_FOUND);
        }
    }
}
