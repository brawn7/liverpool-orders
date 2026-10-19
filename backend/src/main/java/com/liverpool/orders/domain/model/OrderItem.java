package com.liverpool.orders.domain.model;

import com.liverpool.orders.domain.exception.BusinessRuleException;
import java.math.BigDecimal;

public record OrderItem(String productCode, int quantity, BigDecimal price) {

    public OrderItem {
        if (productCode == null || productCode.isBlank())
            throw new BusinessRuleException("El código de producto es obligatorio");
        if (quantity <= 0)
            throw new BusinessRuleException("La cantidad debe ser mayor a 0");
        if (price == null || price.signum() < 0)
            throw new BusinessRuleException("El precio no puede ser negativo");
    }

    public BigDecimal subtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}