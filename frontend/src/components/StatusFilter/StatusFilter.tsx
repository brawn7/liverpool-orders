import { ORDER_STATUSES, type OrderStatus } from '../../types/order';
import { STATUS_LABEL } from '../../utils/orderStatus';
import styles from './StatusFilter.module.css';

interface StatusFilterProps {
  value: OrderStatus | undefined;
  onChange: (status: OrderStatus | undefined) => void;
}

const OPTIONS: { value: OrderStatus | undefined; label: string }[] = [
  { value: undefined, label: 'Todos' },
  ...ORDER_STATUSES.map((status) => ({ value: status, label: STATUS_LABEL[status] })),
];

export function StatusFilter({ value, onChange }: StatusFilterProps) {
  return (
    <div className={styles.group} role="group" aria-label="Filtrar por estatus">
      {OPTIONS.map((option) => (
        <button
          key={option.label}
          type="button"
          className={styles.chip}
          aria-pressed={value === option.value}
          onClick={() => onChange(option.value)}
        >
          {option.label}
        </button>
      ))}
    </div>
  );
}
