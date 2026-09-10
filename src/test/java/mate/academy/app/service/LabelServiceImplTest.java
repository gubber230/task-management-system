package mate.academy.app.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import mate.academy.app.dto.request.LabelRequestDto;
import mate.academy.app.dto.request.LabelUpdateRequestDto;
import mate.academy.app.dto.response.LabelResponseDto;
import mate.academy.app.exception.EntityNotFoundException;
import mate.academy.app.mapper.LabelMapper;
import mate.academy.app.model.Label;
import mate.academy.app.repository.LabelRepository;
import mate.academy.app.service.impl.LabelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class LabelServiceImplTest {

    @Mock
    private LabelRepository labelRepository;
    @Mock
    private LabelMapper labelMapper;

    @InjectMocks
    private LabelServiceImpl labelService;

    private Label label;
    private LabelResponseDto responseDto;
    private final Long labelId = 1L;

    @BeforeEach
    void setUp() {
        label = new Label();
        label.setId(labelId);
        label.setName("Bug");
        label.setColor("#FF0000");

        responseDto = new LabelResponseDto(labelId, "Bug", "#FF0000");
    }

    @Test
    void create_ValidRequestDto_ReturnsLabelResponseDto() {
        LabelRequestDto requestDto = new LabelRequestDto("Bug", "#FF0000");

        when(labelMapper.toModel(requestDto)).thenReturn(label);
        when(labelRepository.save(label)).thenReturn(label);
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        LabelResponseDto actual = labelService.create(requestDto);

        assertEquals(responseDto, actual);
        verify(labelRepository).save(label);
    }

    @Test
    void getALl_ValidPageable_ReturnsPageOfLabelResponseDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Label> labelPage = new PageImpl<>(List.of(label));

        when(labelRepository.findAll(pageable)).thenReturn(labelPage);
        when(labelMapper.toDto(label)).thenReturn(responseDto);

        Page<LabelResponseDto> actual = labelService.getALl(pageable);

        assertEquals(1, actual.getTotalElements());
        assertEquals(responseDto, actual.getContent().getFirst());
    }

    @Test
    void update_ValidRequestDto_Success() {
        LabelUpdateRequestDto updateRequestDto = new LabelUpdateRequestDto("Feature", "#00FF00");

        when(labelRepository.findById(labelId)).thenReturn(Optional.of(label));

        assertDoesNotThrow(() -> labelService.update(labelId, updateRequestDto));

        verify(labelMapper).update(label, updateRequestDto);
    }

    @Test
    void update_NotValidId_ThrowsEntityNotFoundException() {
        LabelUpdateRequestDto updateRequestDto = new LabelUpdateRequestDto("Feature", "#00FF00");

        when(labelRepository.findById(labelId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> labelService.update(labelId, updateRequestDto));
    }

    @Test
    void delete_ValidId_Success() {
        assertDoesNotThrow(() -> labelService.delete(labelId));

        verify(labelRepository).deleteById(labelId);
    }
}
