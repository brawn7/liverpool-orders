import type { Order, OrderSearchParams, OrderStatus, OrderSuggestion, Page } from '../types/order';
import { request } from './client';

export const ordersApi = {
  search(params: OrderSearchParams, signal?: AbortSignal) {
    return request<Page<Order>>('/api/orders', {
      query: { q: params.q, status: params.status, page: params.page, size: params.size },
      signal,
    });
  },

  suggestions(q: string, limit = 8, signal?: AbortSignal) {
    return request<OrderSuggestion[]>('/api/orders/suggestions', { query: { q, limit }, signal });
  },

  getById(id: string, signal?: AbortSignal) {
    return request<Order>(`/api/orders/${encodeURIComponent(id)}`, { signal });
  },

  changeStatus(id: string, status: OrderStatus) {
    return request<Order>(`/api/orders/${encodeURIComponent(id)}/status`, {
      method: 'PATCH',
      body: { status },
    });
  },
};
