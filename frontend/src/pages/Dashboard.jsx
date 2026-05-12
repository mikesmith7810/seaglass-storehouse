import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'

const formatValue = (n) =>
  `£${Math.round(n).toLocaleString('en-GB')}`

export default function Dashboard() {
  const [items, setItems] = useState([])
  const [locations, setLocations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    Promise.all([api.get('/api/items'), api.get('/api/locations')])
      .then(([itemList, locationList]) => {
        setItems(itemList)
        setLocations(locationList)
      })
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="loading">Loading dashboard…</div>
  if (error)   return <div className="error-banner">{error}</div>

  const totalValue = items.reduce((sum, item) => sum + parseFloat(item.price || 0), 0)

  const locationStats = locations.map(location => {
    const locationItems = items.filter(item => item.locationId === location.id)
    const locationValue = locationItems.reduce((sum, item) => sum + parseFloat(item.price || 0), 0)
    return { ...location, itemCount: locationItems.length, value: locationValue }
  })

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Inventory</h1>
        <Link to="/items/new" className="btn btn-primary">+ Add Item</Link>
      </div>

      <div className="stats-grid">
        <Link to="/browse" className="stat-card" style={{ textDecoration: 'none' }}>
          <div className="stat-number">{items.length}</div>
          <div className="stat-label">Items</div>
        </Link>
        <div className="stat-card">
          <div className="stat-number">{locations.length}</div>
          <div className="stat-label">Locations</div>
        </div>
        <div className="stat-card">
          <div className="stat-number">{formatValue(totalValue)}</div>
          <div className="stat-label">Value</div>
        </div>
      </div>

      <h2 className="section-title">By Location</h2>

      {locations.length === 0 ? (
        <div className="empty-state">
          No locations yet. <Link to="/admin" style={{ color: 'var(--color-teal)', fontStyle: 'normal' }}>Add one in Admin.</Link>
        </div>
      ) : (
        <div className="grid">
          {locationStats.map(location => (
            <Link
              key={location.id}
              to={`/browse?locationId=${location.id}`}
              className="card"
              style={{ textDecoration: 'none', display: 'block', transition: 'box-shadow 0.18s, transform 0.18s' }}
              onMouseEnter={e => { e.currentTarget.style.boxShadow = 'var(--shadow-lg)'; e.currentTarget.style.transform = 'translateY(-2px)' }}
              onMouseLeave={e => { e.currentTarget.style.boxShadow = ''; e.currentTarget.style.transform = '' }}
            >
              {location.photoUrl && (
                <img
                  src={location.photoUrl}
                  alt={location.name}
                  style={{ width: '100%', height: '130px', objectFit: 'cover', borderRadius: '8px', marginBottom: '0.85rem' }}
                />
              )}
              <h3 style={{ marginBottom: '0.5rem' }}>{location.name}</h3>
              <p style={{ color: 'var(--color-text-muted)', fontSize: '0.9rem' }}>
                {location.itemCount} {location.itemCount === 1 ? 'item' : 'items'}
              </p>
              <p style={{ color: 'var(--color-teal)', fontFamily: 'var(--font-heading)', fontWeight: 700, fontSize: '1.3rem', marginTop: '0.6rem' }}>
                {formatValue(location.value)}
              </p>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
