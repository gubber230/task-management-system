package mate.academy.app.service.impl;

import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.request.LabelRequestDto;
import mate.academy.app.dto.request.LabelUpdateRequestDto;
import mate.academy.app.dto.response.LabelResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.mapper.LabelMapper;
import mate.academy.app.model.Label;
import mate.academy.app.repository.LabelRepository;
import mate.academy.app.service.LabelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelResponseDto create(LabelRequestDto requestDto) {
        Label model = labelMapper.toModel(requestDto);
        return labelMapper.toDto(labelRepository.save(model));
    }

    @Override
    public Page<LabelResponseDto> getALl(Pageable pageable) {
        return labelRepository.findAll(pageable)
                .map(labelMapper::toDto);
    }

    @Override
    public void update(Long labelId, LabelUpdateRequestDto updateRequestDto) {
        Label label = labelRepository.findById(labelId).orElseThrow(
                () -> new EntityNotFoundException("No label with id: " + labelId)
        );
        labelMapper.update(label, updateRequestDto);
    }

    @Override
    public void delete(Long labelId) {
        labelRepository.deleteById(labelId);
    }
}
