package com.wallet.instrument_service.core.api;

import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.service.InstrumentImportService;
import com.wallet.instrument_service.core.service.InstrumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/instruments")
@RequiredArgsConstructor
public class InstrumentController {

    private final InstrumentService instrumentService;
    private final InstrumentImportService importService;

    @PostMapping
    public ResponseEntity<InstrumentResponse> createInstrument(@RequestBody @Valid InstrumentRequest request){
        InstrumentResponse response = instrumentService.createInstrument(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{instrumentId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<InstrumentRequest>> getAllInstruments() {
        List<InstrumentRequest> instruments = instrumentService.getAllInstruments();
        return ResponseEntity.ok(instruments);
    }

    @PostMapping("/import")
    public ResponseEntity<Void> fetchInstruments(@RequestParam SortBy sort,
                                                 @RequestParam OrderDir order,
                                                 @RequestParam @Min(1) @Max(1000) int limit){

        importService.fetchInstruments(sort, order, limit);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteInstruments(){
        instrumentService.deleteInstruments();
        return ResponseEntity.ok().build();
    }
}

