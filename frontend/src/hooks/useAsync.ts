import { useCallback, useEffect, useState, type DependencyList } from 'react';
import { isAbortError } from '../api/client';

interface AsyncState<T> {
  data: T | undefined;
  error: Error | undefined;
  loading: boolean;
}

export function useAsync<T>(
  fetcher: ((signal: AbortSignal) => Promise<T>) | null,
  deps: DependencyList,
) {
  const [state, setState] = useState<AsyncState<T>>({
    data: undefined,
    error: undefined,
    loading: fetcher !== null,
  });
  const [reloadToken, setReloadToken] = useState(0);

  useEffect(() => {
    if (!fetcher) {
      setState({ data: undefined, error: undefined, loading: false });
      return;
    }

    const controller = new AbortController();
    setState((prev) => ({ ...prev, loading: true, error: undefined }));

    fetcher(controller.signal)
      .then((data) => setState({ data, error: undefined, loading: false }))
      .catch((error: unknown) => {
        if (isAbortError(error)) return;
        setState((prev) => ({
          data: prev.data,
          error: error instanceof Error ? error : new Error(String(error)),
          loading: false,
        }));
      });

    return () => controller.abort();
  }, [...deps, reloadToken]);

  const reload = useCallback(() => setReloadToken((token) => token + 1), []);
  const setData = useCallback((data: T) => setState({ data, error: undefined, loading: false }), []);

  return { ...state, reload, setData };
}
