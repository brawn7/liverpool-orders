import { Link } from 'react-router';
import { EmptyState } from '../components/Feedback/Feedback';

export function NotFoundPage() {
  return (
    <EmptyState
      title="Página no encontrada"
      description={
        <>
          La ruta no existe. <Link to="/">Ir a pedidos</Link>
        </>
      }
    />
  );
}
