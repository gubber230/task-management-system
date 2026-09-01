package mate.academy.app.service;

import mate.academy.app.dto.request.LabelRequestDto;
import mate.academy.app.dto.request.LabelUpdateRequestDto;
import mate.academy.app.dto.response.LabelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    LabelResponseDto create(LabelRequestDto requestDto);

    Page<LabelResponseDto> getALl(Pageable pageable);

    void update(Long labelId, LabelUpdateRequestDto updateRequestDto);

    void delete(Long labelId);
}
