import styles from './Pagination.module.css';

interface PaginationProps {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export function Pagination({ page, size, totalElements, totalPages, onPageChange }: PaginationProps) {
  if (totalElements === 0) return null;

  const from = page * size + 1;
  const to = Math.min((page + 1) * size, totalElements);

  return (
    <nav className={styles.pagination} aria-label="Paginación">
      <p className={styles.summary}>
        Mostrando <strong>{from}–{to}</strong> de <strong>{totalElements}</strong>
      </p>
      <div className={styles.controls}>
        <button type="button" className={styles.button} disabled={page === 0} onClick={() => onPageChange(page - 1)}>
          ← Anterior
        </button>
        <span className={styles.current} aria-current="page">
          {page + 1} / {totalPages}
        </span>
        <button
          type="button"
          className={styles.button}
          disabled={page >= totalPages - 1}
          onClick={() => onPageChange(page + 1)}
        >
          Siguiente →
        </button>
      </div>
    </nav>
  );
}
