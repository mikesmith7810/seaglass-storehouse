import { useState, useEffect } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { api } from '../api/client'
import ItemCard from '../components/ItemCard'

export default function Browse() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [locations, setLocations] = useState([])
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [dropdownOpen, setDropdownOpen] = useState(false)

  const activeLocationId = searchParams.get('locationId')
    ? parseInt(searchParams.get('locationId'))
    : null

  const activeLocation = locations.find(l => l.id === activeLocationId) || null

  useEffect(() => {
    api.get('/api/locations').then(setLocations).catch(err => setError(err.message))
  }, [])

  useEffect(() => {
    setLoading(true)
    const url = activeLocationId
      ? `/api/items/search?locationId=${activeLocationId}`
      : '/api/items'
    api.get(url)
      .then(setItems)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [activeLocationId])

  const selectLocation = (id) => {
    id === null ? setSearchParams({}) : setSearchParams({ locationId: id })
    setDropdownOpen(false)
  }

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Browse</h1>
        <Link to="/items/new" className="btn btn-primary">+ Add Item</Link>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div className="location-filter">
        <button
          className={`location-filter-btn${activeLocation ? ' active' : ''}`}
          onClick={() => setDropdownOpen(prev => !prev)}
        >
          {activeLocation ? activeLocation.name : 'All'}
          <span className="location-filter-chevron">{dropdownOpen ? '▲' : '▼'}</span>
        </button>
        {dropdownOpen && (
          <div className="location-dropdown">
            <button
              className={`location-dropdown-item${!activeLocationId ? ' active' : ''}`}
              onClick={() => selectLocation(null)}
            >
              All
            </button>
            {locations.map(loc => (
              <button
                key={loc.id}
                className={`location-dropdown-item${activeLocationId === loc.id ? ' active' : ''}`}
                onClick={() => selectLocation(loc.id)}
              >
                {loc.name}
              </button>
            ))}
          </div>
        )}
      </div>

      {loading ? (
        <div className="loading">Loading items…</div>
      ) : items.length === 0 ? (
        <div className="empty-state">No items found.</div>
      ) : (
        <div className="grid">
          {items.map(item => <ItemCard key={item.id} item={item} />)}
        </div>
      )}
    </div>
  )
}
