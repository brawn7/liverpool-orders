package com.liverpool.orders.infrastructure.adapter.in.web;

import com.liverpool.orders.domain.model.User;
import com.liverpool.orders.domain.port.in.UserUseCase;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.PageResponse;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.UserRequest;
import com.liverpool.orders.infrastructure.adapter.in.web.dto.UserResponse;
import com.liverpool.orders.infrastructure.adapter.in.web.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Alta, consulta, actualización y baja de usuarios")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping
    @Operation(summary = "Crear usuario")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User created = userUseCase.create(UserWebMapper.toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(UserWebMapper.toResponse(created));
    }

    @GetMapping
    @Operation(summary = "Listar usuarios (paginado)")
    public PageResponse<UserResponse> list(@RequestParam(defaultValue = "0") @Min(0) int page, @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return PageResponse.from(userUseCase.list(page, size), UserWebMapper::toResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por id")
    public UserResponse getById(@PathVariable String id) {
        return UserWebMapper.toResponse(userUseCase.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public UserResponse update(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        return UserWebMapper.toResponse(userUseCase.update(id, UserWebMapper.toCommand(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "No se permite si el usuario tiene pedidos")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}