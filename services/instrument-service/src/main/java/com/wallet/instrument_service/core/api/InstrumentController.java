package com.wallet.instrument_service.core.api;

import com.wallet.instrument_service.core.api.dto.InstrumentRequest;
import com.wallet.instrument_service.core.api.dto.InstrumentResponse;
import com.wallet.instrument_service.core.api.enums.OrderDir;
import com.wallet.instrument_service.core.api.enums.SortBy;
import com.wallet.instrument_service.core.persistence.enums.Market;
import com.wallet.instrument_service.core.service.InstrumentImportService;
import com.wallet.instrument_service.core.service.InstrumentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<InstrumentResponse>> getAllInstruments(
            @PageableDefault(size = 50, sort = "ticker", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(instrumentService.getInstruments(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<InstrumentResponse>> search(
            @RequestParam @NotBlank String query) {
        return ResponseEntity.ok(instrumentService.search(query));
    }

    @PostMapping("/import")
    public ResponseEntity<Void> fetchInstruments(@RequestParam(defaultValue = "STOCKS") Market market,
                                                 @RequestParam(defaultValue = "TICKER") SortBy sort,
                                                 @RequestParam(defaultValue = "ASC") OrderDir order,
                                                 @RequestParam(defaultValue = "100") @Min(1) @Max(1000) int limit){

        importService.fetchInstruments(market, sort, order, limit);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteInstruments(){
        instrumentService.deleteInstruments();
        return ResponseEntity.ok().build();
    }
}

