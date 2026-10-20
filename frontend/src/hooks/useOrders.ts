import { ordersApi } from '../api/orders';
import type { OrderSearchParams } from '../types/order';
import { useAsync } from './useAsync';

export const SUGGESTION_MIN_CHARS = 2;

export function useOrderSearch({ q, status, page, size }: OrderSearchParams) {
  return useAsync((signal) => ordersApi.search({ q, status, page, size }, signal), [q, status, page, size]);
}

export function useOrder(id: string | undefined) {
  return useAsync(id ? (signal) => ordersApi.getById(id, signal) : null, [id]);
}

export function useOrderSuggestions(term: string) {
  const trimmed = term.trim();
  return useAsync(
    trimmed.length >= SUGGESTION_MIN_CHARS ? (signal) => ordersApi.suggestions(trimmed, 8, signal) : null,
    [trimmed],
  );
}
