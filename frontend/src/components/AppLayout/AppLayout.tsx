import { Link, Outlet } from 'react-router';
import styles from './AppLayout.module.css';

export function AppLayout() {
  return (
    <div className={styles.shell}>
      <header className={styles.header}>
        <div className={styles.headerInner}>
          <Link to="/" className={styles.brand}>
            <span className={styles.brandMark} aria-hidden="true" />
            <span>
              <span className={styles.brandTitle}>Gestión de pedidos</span>
              <span className={styles.brandSubtitle}>Ventas · App y sitio web</span>
            </span>
          </Link>
        </div>
      </header>

      <main className={styles.main}>
        <Outlet />
      </main>
    </div>
  );
}
