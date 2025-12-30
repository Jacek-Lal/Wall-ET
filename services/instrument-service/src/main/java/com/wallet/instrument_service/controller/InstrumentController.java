package com.wallet.instrument_service.controller;

import com.wallet.instrument_service.dto.InstrumentDTO;
import com.wallet.instrument_service.service.InstrumentImportService;
import com.wallet.instrument_service.service.InstrumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;
    private final InstrumentImportService importService;

    @PostMapping
    public ResponseEntity<Void> createInstrument(@RequestBody @Valid InstrumentDTO request){
        instrumentService.createInstrument(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<InstrumentDTO>> getAllInstruments() {
        List<InstrumentDTO> instruments = instrumentService.getAllInstruments();
        return ResponseEntity.ok(instruments);
    }
    @PostMapping("/import")
    public ResponseEntity<Void> fetchInstruments(@RequestParam @NotBlank String sort,
                                                 @RequestParam @NotBlank String order,
                                                 @RequestParam @Min(1) @Max(1000) int limit){

        if (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))
            throw new IllegalArgumentException("Invalid order parameter. Use 'asc' or 'desc'");

        importService.fetchInstruments(sort, order, limit);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteInstruments(){
        instrumentService.deleteInstruments();
        return ResponseEntity.ok().build();
    }
}

