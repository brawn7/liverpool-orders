import { useState, type KeyboardEvent, type ReactNode } from 'react';
import styles from './Typeahead.module.css';

export interface TypeaheadProps<T> {
  id: string;
  label: string;
  value: string;
  onValueChange: (value: string) => void;
  items: T[];
  loading: boolean;
  getKey: (item: T) => string;
  renderItem: (item: T, query: string) => ReactNode;
  onSelect: (item: T) => void;
  onSubmit: (text: string) => void;
  minChars?: number;
  placeholder?: string;
  emptyMessage?: (query: string) => ReactNode;
  errorMessage?: string;
  hint?: ReactNode;
}

export function Typeahead<T>({
  id,
  label,
  value,
  onValueChange,
  items,
  loading,
  getKey,
  renderItem,
  onSelect,
  onSubmit,
  minChars = 2,
  placeholder,
  emptyMessage = () => 'Sin resultados',
  errorMessage,
  hint,
}: TypeaheadProps<T>) {
  const [open, setOpen] = useState(false);
  const [activeIndex, setActiveIndex] = useState(-1);

  const listboxId = `${id}-listbox`;
  const query = value.trim();
  const panelVisible = open && query.length >= minChars;
  const hasItems = items.length > 0;
  const optionId = (index: number) => `${id}-option-${index}`;

  function close() {
    setOpen(false);
    setActiveIndex(-1);
  }

  function choose(item: T) {
    onSelect(item);
    close();
  }

  function handleKeyDown(event: KeyboardEvent<HTMLInputElement>) {
    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        setOpen(true);
        if (hasItems) setActiveIndex((i) => (i + 1) % items.length);
        break;
      case 'ArrowUp':
        event.preventDefault();
        if (hasItems) setActiveIndex((i) => (i <= 0 ? items.length - 1 : i - 1));
        break;
      case 'Enter':
        event.preventDefault();
        if (panelVisible && activeIndex >= 0 && items[activeIndex]) {
          choose(items[activeIndex]);
        } else {
          onSubmit(query);
          close();
        }
        break;
      case 'Escape':
        close();
        break;
    }
  }

  function clear() {
    onValueChange('');
    onSubmit('');
    close();
  }

  return (
    <div className={styles.root}>
      <label htmlFor={id} className="visually-hidden">
        {label}
      </label>

      <div className={styles.field}>
        <svg className={styles.icon} viewBox="0 0 24 24" aria-hidden="true">
          <circle cx="11" cy="11" r="7" />
          <path d="m20 20-3.5-3.5" />
        </svg>

        <input
          id={id}
          className={styles.input}
          type="text"
          role="combobox"
          autoComplete="off"
          spellCheck={false}
          aria-expanded={panelVisible}
          aria-controls={listboxId}
          aria-autocomplete="list"
          aria-activedescendant={panelVisible && activeIndex >= 0 ? optionId(activeIndex) : undefined}
          placeholder={placeholder}
          value={value}
          onChange={(event) => {
            onValueChange(event.target.value);
            setOpen(true);
            setActiveIndex(-1);
          }}
          onFocus={() => setOpen(true)}
          onBlur={close}
          onKeyDown={handleKeyDown}
        />

        {loading && query.length >= minChars && <span className={styles.spinner} aria-hidden="true" />}

        {value && (
          <button
            type="button"
            className={styles.clear}
            aria-label="Limpiar búsqueda"
            onMouseDown={(event) => event.preventDefault()}
            onClick={clear}
          >
            ×
          </button>
        )}
      </div>

      {panelVisible && (
        <div className={styles.panel} onMouseDown={(event) => event.preventDefault()}>
          <ul id={listboxId} role="listbox" aria-label={label} className={styles.list}>
            {!errorMessage &&
              items.map((item, index) => (
                <li
                  key={getKey(item)}
                  id={optionId(index)}
                  role="option"
                  aria-selected={index === activeIndex}
                  className={`${styles.option} ${index === activeIndex ? styles.active : ''}`}
                  onMouseEnter={() => setActiveIndex(index)}
                  onClick={() => choose(item)}
                >
                  {renderItem(item, query)}
                </li>
              ))}
          </ul>

          {errorMessage ? (
            <p className={`${styles.message} ${styles.error}`} role="alert">
              {errorMessage}
            </p>
          ) : !hasItems && loading ? (
            <p className={styles.message}>Buscando…</p>
          ) : !hasItems ? (
            <p className={styles.message} role="status">
              {emptyMessage(query)}
            </p>
          ) : (
            hint && <p className={styles.hint}>{hint}</p>
          )}
        </div>
      )}
    </div>
  );
}
