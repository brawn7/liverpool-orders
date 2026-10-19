package com.liverpool.orders.infrastructure.adapter.in.web;

import com.liverpool.orders.domain.model.Order;
import com.liverpool.orders.domain.model.OrderFilter;
import com.liverpool.orders.domain.model.OrderStatus;
import com.liverpool.orders.domain.port.in.OrderUseCase;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.*;
import com.liverpool.orders.infrastructure.adapter.in.web.mapper.OrderWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Pedidos", description = "Gestión de pedidos de app y sitio web")
public class OrderController {

    private final OrderUseCase orderUseCase;

    public OrderController(OrderUseCase orderUseCase) {
        this.orderUseCase = orderUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear pedido", description = "El pedido nace en estatus CREATED")
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        Order created = orderUseCase.create(OrderWebMapper.toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(OrderWebMapper.toResponse(created));
    }

    @GetMapping
    @Operation(summary = "Buscar y listar pedidos", description = "q busca en folio, nombre y email del cliente y código de producto")
    public PageResponse<OrderResponse> search(
            @Parameter(description = "Término de búsqueda") @RequestParam(required = false) String q,
            @Parameter(description = "Filtro por estatus") @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(orderUseCase.search(new OrderFilter(q, status), page, size), OrderWebMapper::toResponse);
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Sugerencias para typeahead", description = "Respuesta ligera y limitada")
    public List<OrderSuggestionResponse> suggestions(@RequestParam(required = false) String q, @RequestParam(defaultValue = "8") @Min(1) @Max(20) int limit) {
        if (q == null || q.isBlank()) return List.of();
        return orderUseCase.search(new OrderFilter(q, null), 0, limit).content().stream().map(OrderWebMapper::toSuggestion).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle del pedido")
    public OrderResponse getById(@PathVariable String id) {
        return OrderWebMapper.toResponse(orderUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pedido", description = "Solo permitido en estatus CREATED")
    public OrderResponse update(@PathVariable String id, @Valid @RequestBody OrderRequest request) {
        return OrderWebMapper.toResponse(orderUseCase.update(id, OrderWebMapper.toCommand(request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Cambiar estatus del pedido", description = "CREATED -> PAID|CANCELLED, PAID -> SHIPPED|CANCELLED, SHIPPED -> DELIVERED")
    public OrderResponse changeStatus(@PathVariable String id, @Valid @RequestBody StatusUpdateRequest request) {
        return OrderWebMapper.toResponse(orderUseCase.changeStatus(id, request.status()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pedido")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        orderUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}