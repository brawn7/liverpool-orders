import { useState } from 'react';
import { Link, useLocation, useParams } from 'react-router';
import { ApiError } from '../api/client';
import { ordersApi } from '../api/orders';
import { Card } from '../components/Card/Card';
import { DataTable, type Column } from '../components/DataTable/DataTable';
import { EmptyState, ErrorState, LoadingRows } from '../components/Feedback/Feedback';
import { StatusBadge } from '../components/StatusBadge/StatusBadge';
import { useOrder } from '../hooks/useOrders';
import type { OrderItem, OrderStatus } from '../types/order';
import { countUnits, formatCurrency, formatDateTime } from '../utils/format';
import { NEXT_STATUSES, STATUS_ACTION_LABEL, STATUS_FLOW, STATUS_LABEL } from '../utils/orderStatus';
import styles from './OrderDetailPage.module.css';

const itemColumns: Column<OrderItem>[] = [
  { key: 'code', header: 'Código de producto', render: (i) => <span className={styles.code}>{i.productCode}</span> },
  { key: 'qty', header: 'Cantidad', align: 'end', render: (i) => i.quantity },
  { key: 'price', header: 'Precio unitario', align: 'end', render: (i) => formatCurrency(i.price) },
  { key: 'subtotal', header: 'Subtotal', align: 'end', render: (i) => <strong>{formatCurrency(i.subtotal)}</strong> },
];

export function OrderDetailPage() {
  const { id } = useParams();
  const location = useLocation();
  const from = (location.state as { from?: string } | null)?.from ?? '';
  const backTo = `/${from}`;

  const { data: order, error, reload, setData } = useOrder(id);
  const [updatingTo, setUpdatingTo] = useState<OrderStatus | null>(null);
  const [actionError, setActionError] = useState<string>();

  const backLink = (
    <Link to={backTo} className={styles.back}>
      ← Volver a pedidos
    </Link>
  );

  if (error && !order) {
    const notFound = error instanceof ApiError && error.status === 404;
    return (
      <div className={styles.page}>
        {backLink}
        {notFound ? (
          <EmptyState title="Pedido no encontrado" description="El pedido no existe o fue eliminado." />
        ) : (
          <ErrorState message={error.message} onRetry={reload} />
        )}
      </div>
    );
  }

  if (!order) {
    return (
      <div className={styles.page}>
        {backLink}
        <LoadingRows rows={4} />
      </div>
    );
  }

  async function changeStatus(next: OrderStatus) {
    if (!order) return;
    if (next === 'CANCELLED' && !window.confirm('¿Cancelar este pedido? Esta acción no se puede deshacer.')) return;

    setUpdatingTo(next);
    setActionError(undefined);
    try {
      setData(await ordersApi.changeStatus(order.id, next));
    } catch (e) {
      setActionError(e instanceof Error ? e.message : 'No se pudo actualizar el estatus');
    } finally {
      setUpdatingTo(null);
    }
  }

  const nextStatuses = NEXT_STATUSES[order.status];
  const { shippingAddress: address, customer } = order;

  return (
    <div className={styles.page}>
      {backLink}

      <header className={styles.header}>
        <div className={styles.heading}>
          <p className={styles.eyebrow}>Pedido</p>
          <div className={styles.titleRow}>
            <h1 className={styles.title}>{order.orderNumber}</h1>
            <StatusBadge status={order.status} size="md" />
          </div>
          <p className={styles.meta}>Creado el {formatDateTime(order.createdAt)}</p>
        </div>

        {nextStatuses.length > 0 && (
          <div className={styles.actions}>
            {nextStatuses.map((next) => (
              <button
                key={next}
                type="button"
                className={next === 'CANCELLED' ? styles.secondaryButton : styles.primaryButton}
                disabled={updatingTo !== null}
                onClick={() => changeStatus(next)}
              >
                {updatingTo === next ? 'Actualizando…' : STATUS_ACTION_LABEL[next]}
              </button>
            ))}
          </div>
        )}
      </header>

      {actionError && (
        <p className={styles.actionError} role="alert">
          {actionError}
        </p>
      )}

      <StatusTimeline status={order.status} />

      <div className={styles.grid}>
        <Card title="Cliente">
          <strong>{customer.fullName}</strong>
          <a href={`mailto:${customer.email}`} className={styles.link}>
            {customer.email}
          </a>
          <span className={styles.muted}>ID {customer.userId}</span>
        </Card>

        <Card title="Dirección de envío">
          <span>{address.street}</span>
          <span>Col. {address.neighborhood}</span>
          <span>
            {address.city}, {address.state}
          </span>
          <span>C.P. {address.zipCode}</span>
        </Card>

        <Card title="Resumen">
          <dl className={styles.summary}>
            <dt>Productos</dt>
            <dd>{order.items.length}</dd>
            <dt>Artículos</dt>
            <dd>{countUnits(order.items)}</dd>
            <dt>Última actualización</dt>
            <dd>{formatDateTime(order.updatedAt)}</dd>
            <dt className={styles.totalLabel}>Total</dt>
            <dd className={styles.totalValue}>{formatCurrency(order.total)}</dd>
          </dl>
        </Card>
      </div>

      <section className={styles.items}>
        <h2 className={styles.sectionTitle}>Productos</h2>
        <DataTable
          caption="Productos del pedido"
          columns={itemColumns}
          rows={order.items}
          getRowKey={(i) => i.productCode}
          footer={
            <tr>
              <td colSpan={3} className={styles.footerLabel}>
                Total
              </td>
              <td data-label="Total" className={styles.footerTotal}>
                {formatCurrency(order.total)}
              </td>
            </tr>
          }
        />
      </section>
    </div>
  );
}

function StatusTimeline({ status }: { status: OrderStatus }) {
  if (status === 'CANCELLED') {
    return <p className={styles.cancelled}>Este pedido fue cancelado.</p>;
  }

  const currentIndex = STATUS_FLOW.indexOf(status);
  return (
    <ol className={styles.timeline} aria-label="Progreso del pedido">
      {STATUS_FLOW.map((step, index) => (
        <li
          key={step}
          className={`${styles.step} ${index <= currentIndex ? styles.stepDone : ''}`}
          aria-current={index === currentIndex ? 'step' : undefined}
        >
          <span className={styles.stepDot} aria-hidden="true" />
          {STATUS_LABEL[step]}
        </li>
      ))}
    </ol>
  );
}
