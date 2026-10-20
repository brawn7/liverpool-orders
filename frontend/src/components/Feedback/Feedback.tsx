import { useEffect, useState, type ReactNode } from 'react';
import styles from './Feedback.module.css';

interface EmptyStateProps {
  title: string;
  description?: ReactNode;
  action?: ReactNode;
}

export function EmptyState({ title, description, action }: EmptyStateProps) {
  return (
    <div className={styles.box} role="status">
      <svg className={styles.icon} viewBox="0 0 24 24" aria-hidden="true">
        <circle cx="11" cy="11" r="7" />
        <path d="m20 20-3.5-3.5M8.5 11h5" />
      </svg>
      <p className={styles.title}>{title}</p>
      {description && <p className={styles.description}>{description}</p>}
      {action}
    </div>
  );
}

interface ErrorStateProps {
  message: string;
  onRetry?: () => void;
}

export function ErrorState({ message, onRetry }: ErrorStateProps) {
  return (
    <div className={`${styles.box} ${styles.error}`} role="alert">
      <p className={styles.title}>Algo salió mal</p>
      <p className={styles.description}>{message}</p>
      {onRetry && (
        <button type="button" className={styles.button} onClick={onRetry}>
          Reintentar
        </button>
      )}
    </div>
  );
}

const SLOW_NOTICE_DELAY_MS = 4000;

export function LoadingRows({ rows = 6 }: { rows?: number }) {
  const [slow, setSlow] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => setSlow(true), SLOW_NOTICE_DELAY_MS);
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className={styles.skeleton} aria-busy="true" aria-label="Cargando">
      {slow && (
        <p className={styles.slowNotice} role="status">
          El servidor se está activando, la primera carga puede tardar hasta un par de minutos…
        </p>
      )}
      {Array.from({ length: rows }, (_, index) => (
        <div key={index} className={styles.skeletonRow} />
      ))}
    </div>
  );
}

export function TextButton({ children, onClick }: { children: ReactNode; onClick: () => void }) {
  return (
    <button type="button" className={styles.button} onClick={onClick}>
      {children}
    </button>
  );
}
