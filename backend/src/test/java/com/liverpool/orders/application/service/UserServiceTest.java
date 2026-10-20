package com.liverpool.orders.application.service;

import com.liverpool.orders.domain.exception.ConflictException;
import com.liverpool.orders.domain.exception.NotFoundException;
import com.liverpool.orders.domain.model.User;
import com.liverpool.orders.domain.port.in.UserCommand;
import com.liverpool.orders.domain.port.out.OrderRepositoryPort;
import com.liverpool.orders.domain.port.out.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepositoryPort users;
    @Mock OrderRepositoryPort orders;
    @InjectMocks UserService service;

    private final User existing = new User("u1", "Ana", "Ruiz", null,
        "ana@example.com", Instant.now(), Instant.now());

    @Test
    void rejectsDuplicateEmailIgnoringCaseAndSpaces() {
        when(users.findByEmail("ana@example.com")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.create(new UserCommand("Ana", "Ruiz", null, "  ANA@Example.com ")))
            .isInstanceOf(ConflictException.class);
        verify(users, never()).save(any());
    }

    @Test
    void updateAllowsKeepingOwnEmail() {
        when(users.findById("u1")).thenReturn(Optional.of(existing));
        when(users.findByEmail("ana@example.com")).thenReturn(Optional.of(existing));
        when(users.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User updated = service.update("u1", new UserCommand("Ana María", "Ruiz", null, "ana@example.com"));

        assertThat(updated.firstName()).isEqualTo("Ana María");
    }

    @Test
    void deleteFailsWhenUserHasOrders() {
        when(users.findById("u1")).thenReturn(Optional.of(existing));
        when(orders.existsByUserId("u1")).thenReturn(true);

        assertThatThrownBy(() -> service.delete("u1")).isInstanceOf(ConflictException.class);
        verify(users, never()).deleteById(any());
    }

    @Test
    void getByIdThrowsNotFound() {
        when(users.findById("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("x")).isInstanceOf(NotFoundException.class);
    }
}