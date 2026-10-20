import type { OrderStatus } from '../types/order';

export const STATUS_LABEL: Record<OrderStatus, string> = {
  CREATED: 'Creado',
  PAID: 'Pagado',
  SHIPPED: 'Enviado',
  DELIVERED: 'Entregado',
  CANCELLED: 'Cancelado',
};

export const NEXT_STATUSES: Record<OrderStatus, OrderStatus[]> = {
  CREATED: ['PAID', 'CANCELLED'],
  PAID: ['SHIPPED', 'CANCELLED'],
  SHIPPED: ['DELIVERED'],
  DELIVERED: [],
  CANCELLED: [],
};

export const STATUS_ACTION_LABEL: Partial<Record<OrderStatus, string>> = {
  PAID: 'Marcar como pagado',
  SHIPPED: 'Marcar como enviado',
  DELIVERED: 'Marcar como entregado',
  CANCELLED: 'Cancelar pedido',
};

/** Camino feliz del pedido, usado por la línea de tiempo del detalle. */
export const STATUS_FLOW: OrderStatus[] = ['CREATED', 'PAID', 'SHIPPED', 'DELIVERED'];
