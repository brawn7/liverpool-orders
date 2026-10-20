import { useDebounce } from '../../hooks/useDebounce';
import { SUGGESTION_MIN_CHARS, useOrderSuggestions } from '../../hooks/useOrders';
import type { OrderSuggestion } from '../../types/order';
import { formatCurrency } from '../../utils/format';
import { Highlight } from '../Highlight/Highlight';
import { StatusBadge } from '../StatusBadge/StatusBadge';
import { Typeahead } from '../Typeahead/Typeahead';
import styles from './OrderSearch.module.css';

interface OrderSearchProps {
  value: string;
  onValueChange: (value: string) => void;
  onFilter: (term: string) => void;
}

export function OrderSearch({ value, onValueChange, onFilter }: OrderSearchProps) {
  const debounced = useDebounce(value, 300);
  const { data, loading, error } = useOrderSuggestions(debounced);
  const typing = value.trim() !== debounced.trim();

  return (
    <Typeahead<OrderSuggestion>
      id="order-search"
      label="Buscar pedidos"
      placeholder="Folio, cliente, email o código de producto"
      value={value}
      onValueChange={onValueChange}
      items={typing ? [] : (data ?? [])}
      loading={typing || loading}
      minChars={SUGGESTION_MIN_CHARS}
      getKey={(s) => s.id}
      onSelect={(s) => {
        onValueChange(s.orderNumber);
        onFilter(s.orderNumber);
      }}
      onSubmit={onFilter}
      errorMessage={error ? 'No se pudieron cargar las sugerencias. Intenta de nuevo.' : undefined}
      emptyMessage={(q) => (
        <>
          No se encontraron pedidos que coincidan con <strong>«{q}»</strong>
        </>
      )}
      hint={
        <>
          Selecciona un pedido para filtrarlo, o presiona <kbd>Enter</kbd> para filtrar por el texto
        </>
      }
      renderItem={(s, q) => (
        <div className={styles.suggestion}>
          <div className={styles.main}>
            <span className={styles.folio}>
              <Highlight text={s.orderNumber} query={q} />
            </span>
            <span className={styles.customer}>
              <Highlight text={s.customerName} query={q} />
            </span>
            <span className={styles.email}>
              <Highlight text={s.customerEmail} query={q} />
            </span>
          </div>
          <div className={styles.side}>
            <StatusBadge status={s.status} />
            <span className={styles.total}>{formatCurrency(s.total)}</span>
          </div>
        </div>
      )}
    />
  );
}
