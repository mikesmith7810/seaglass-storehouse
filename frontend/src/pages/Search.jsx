import { useState, useEffect } from 'react'
import { api } from '../api/client'
import ItemCard from '../components/ItemCard'

export default function Search() {
  const [categories, setCategories] = useState([])
  const [locations, setLocations] = useState([])
  const [results, setResults] = useState([])
  const [searched, setSearched] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const [filters, setFilters] = useState({
    q: '', categoryId: '', locationId: '', minPrice: '', maxPrice: ''
  })

  useEffect(() => {
    Promise.all([api.get('/api/categories'), api.get('/api/locations')])
      .then(([cats, locs]) => { setCategories(cats); setLocations(locs) })
  }, [])

  const update = (field) => (e) => setFilters(prev => ({ ...prev, [field]: e.target.value }))

  const handleSearch = (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)

    const params = new URLSearchParams()
    if (filters.q)          params.set('q', filters.q)
    if (filters.categoryId) params.set('categoryId', filters.categoryId)
    if (filters.locationId) params.set('locationId', filters.locationId)
    if (filters.minPrice)   params.set('minPrice', filters.minPrice)
    if (filters.maxPrice)   params.set('maxPrice', filters.maxPrice)

    api.get(`/api/items/search?${params}`)
      .then(data => { setResults(data); setSearched(true) })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }

  return (
    <div>
      <h1 className="page-title" style={{ marginBottom: '1.75rem' }}>Search</h1>

      <form onSubmit={handleSearch}>
        <div className="search-filters">
          <div className="form-group" style={{ margin: 0 }}>
            <label className="form-label">Keyword</label>
            <input className="form-input" placeholder="Search description…" value={filters.q} onChange={update('q')} />
          </div>
          <div className="form-group" style={{ margin: 0 }}>
            <label className="form-label">Category</label>
            <select className="form-select" value={filters.categoryId} onChange={update('categoryId')}>
              <option value="">All</option>
              {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div className="form-group" style={{ margin: 0 }}>
            <label className="form-label">Location</label>
            <select className="form-select" value={filters.locationId} onChange={update('locationId')}>
              <option value="">All</option>
              {locations.map(l => <option key={l.id} value={l.id}>{l.name}</option>)}
            </select>
          </div>
          <div className="form-group" style={{ margin: 0 }}>
            <label className="form-label">Min £</label>
            <input className="form-input" type="number" step="1" min="0" placeholder="0" value={filters.minPrice} onChange={update('minPrice')} />
          </div>
          <div className="form-group" style={{ margin: 0 }}>
            <label className="form-label">Max £</label>
            <input className="form-input" type="number" step="1" min="0" placeholder="Any" value={filters.maxPrice} onChange={update('maxPrice')} />
          </div>
          <div>
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? '…' : 'Search'}
            </button>
          </div>
        </div>
      </form>

      {error && <div className="error-banner">{error}</div>}

      {searched && !loading && (
        <>
          <p style={{ color: 'var(--color-text-muted)', marginBottom: '1.25rem', fontSize: '0.9rem' }}>
            {results.length} result{results.length !== 1 ? 's' : ''}
          </p>
          {results.length === 0 ? (
            <div className="empty-state">No items match your search.</div>
          ) : (
            <div className="grid">
              {results.map(item => <ItemCard key={item.id} item={item} />)}
            </div>
          )}
        </>
      )}
    </div>
  )
}
