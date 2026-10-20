import { useState } from 'react';
import { Link, useLocation, useNavigate, useSearchParams } from 'react-router';
import { DataTable, type Column } from '../components/DataTable/DataTable';
import { EmptyState, ErrorState, LoadingRows, TextButton } from '../components/Feedback/Feedback';
import { OrderSearch } from '../components/OrderSearch/OrderSearch';
import { Pagination } from '../components/Pagination/Pagination';
import { StatusBadge } from '../components/StatusBadge/StatusBadge';
import { StatusFilter } from '../components/StatusFilter/StatusFilter';
import { useOrderSearch } from '../hooks/useOrders';
import { isOrderStatus, type Order, type OrderStatus } from '../types/order';
import { countUnits, formatCurrency, formatDate } from '../utils/format';
import styles from './OrdersPage.module.css';

const PAGE_SIZE = 10;

interface FilterChanges {
  q?: string;
  status?: OrderStatus;
  page?: number;
}

export function OrdersPage() {
  const [params, setParams] = useSearchParams();
  const navigate = useNavigate();
  const location = useLocation();

  const q = params.get('q') ?? '';
  const statusParam = params.get('status');
  const status = isOrderStatus(statusParam) ? statusParam : undefined;
  const page = Math.max(0, Number(params.get('page')) || 0);

  const [searchText, setSearchText] = useState(q);
  const { data, error, loading, reload } = useOrderSearch({ q, status, page, size: PAGE_SIZE });

  function updateFilters(changes: FilterChanges) {
    setParams((prev) => {
      const next = new URLSearchParams(prev);
      if ('q' in changes) setOrDelete(next, 'q', changes.q?.trim());
      if ('status' in changes) setOrDelete(next, 'status', changes.status);
      setOrDelete(next, 'page', changes.page ? String(changes.page) : undefined);
      return next;
    });
  }

  function clearFilters() {
    setSearchText('');
    setParams(new URLSearchParams());
  }

  const detailState = { from: location.search };
  const openOrder = (order: Order) => navigate(`/orders/${order.id}`, { state: detailState });

  const columns: Column<Order>[] = [
    {
      key: 'folio',
      header: 'Folio',
      render: (o) => (
        <Link
          to={`/orders/${o.id}`}
          state={detailState}
          className={styles.folio}
          onClick={(event) => event.stopPropagation()}
        >
          {o.orderNumber}
        </Link>
      ),
    },
    {
      key: 'customer',
      header: 'Cliente',
      render: (o) => (
        <span className={styles.customer}>
          <span>{o.customer.fullName}</span>
          <span className={styles.muted}>{o.customer.email}</span>
        </span>
      ),
    },
    { key: 'date', header: 'Fecha', render: (o) => formatDate(o.createdAt) },
    { key: 'units', header: 'Artículos', align: 'end', render: (o) => countUnits(o.items) },
    {
      key: 'total',
      header: 'Total',
      align: 'end',
      render: (o) => <strong>{formatCurrency(o.total)}</strong>,
    },
    { key: 'status', header: 'Estatus', render: (o) => <StatusBadge status={o.status} /> },
  ];

  const hasFilters = Boolean(q || status);

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <div>
          <h1 className={styles.title}>Pedidos</h1>
          <p className={styles.subtitle}>
            {data ? `${data.totalElements} ${data.totalElements === 1 ? 'pedido' : 'pedidos'}` : 'Cargando…'}
            {hasFilters && ' con los filtros aplicados'}
          </p>
        </div>
      </header>

      <section className={styles.toolbar} aria-label="Búsqueda y filtros">
        <OrderSearch value={searchText} onValueChange={setSearchText} onFilter={(term) => updateFilters({ q: term })} />
        <StatusFilter value={status} onChange={(next) => updateFilters({ status: next })} />
      </section>

      {q && (
        <div className={styles.activeFilter}>
          <span>
            Resultados para <strong>«{q}»</strong>
          </span>
          <button
            type="button"
            className={styles.removeFilter}
            aria-label="Quitar filtro de búsqueda"
            onClick={() => {
              setSearchText('');
              updateFilters({ q: '' });
            }}
          >
            ×
          </button>
        </div>
      )}

      {error && !data ? (
        <ErrorState message={error.message} onRetry={reload} />
      ) : !data ? (
        <LoadingRows />
      ) : data.content.length === 0 ? (
        <EmptyState
          title="No hay resultados"
          description="No se encontraron pedidos que coincidan con los criterios de búsqueda."
          action={hasFilters ? <TextButton onClick={clearFilters}>Limpiar filtros</TextButton> : undefined}
        />
      ) : (
        <div className={loading ? styles.refreshing : undefined} aria-busy={loading}>
          <DataTable
            caption="Lista de pedidos"
            columns={columns}
            rows={data.content}
            getRowKey={(o) => o.id}
            onRowClick={openOrder}
          />
          <Pagination
            page={data.page}
            size={data.size}
            totalElements={data.totalElements}
            totalPages={data.totalPages}
            onPageChange={(next) => updateFilters({ page: next })}
          />
        </div>
      )}
    </div>
  );
}

function setOrDelete(params: URLSearchParams, key: string, value: string | undefined) {
  if (value) {
    params.set(key, value);
  } else {
    params.delete(key);
  }
}
