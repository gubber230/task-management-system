package mate.academy.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.app.dto.request.LabelRequestDto;
import mate.academy.app.dto.request.LabelUpdateRequestDto;
import mate.academy.app.dto.response.LabelResponseDto;
import mate.academy.app.service.LabelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Labels", description = "Endpoints for managing labels")
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/labels")
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public LabelResponseDto create(@RequestBody @Valid LabelRequestDto requestDto) {
        return labelService.create(requestDto);
    }

    @GetMapping
    public Page<LabelResponseDto> getAll(Pageable pageable) {
        return labelService.getALl(pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void update(@PathVariable Long id,
                       @RequestBody @Valid LabelUpdateRequestDto updateRequestDto) {
        labelService.update(id, updateRequestDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        labelService.delete(id);
    }
}
