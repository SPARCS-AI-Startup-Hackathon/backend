package com.backend.backend.personalstatement.controller;

import com.backend.backend.personalstatement.dto.request.PersonalStatementUpdate;
import com.backend.backend.personalstatement.dto.response.PersonalStatementResponse;
import com.backend.backend.personalstatement.service.PersonalStatementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PersonalStatementController {
    private final PersonalStatementService personalStatementService;

    @GetMapping("/get-ps")
    public ResponseEntity<List<PersonalStatementResponse>> getMembersPs() {
        return ResponseEntity.ok(personalStatementService.getMembersPs());
    }

    @GetMapping("/get-ps/{id}")
    public ResponseEntity<PersonalStatementResponse> getMemberPs(@PathVariable Long id) {
        return ResponseEntity.ok(personalStatementService.getMemberPs(id));
    }

    @PutMapping("/ps/{id}")
    public ResponseEntity<PersonalStatementResponse> update(@RequestBody @Valid PersonalStatementUpdate request,
                                                            @PathVariable Long id) {
        return ResponseEntity.ok(personalStatementService.update(request, id));
    }

    @DeleteMapping("/ps/{id}")
    public void delete(@PathVariable Long id) {
        personalStatementService.delete(id);
    }
}
