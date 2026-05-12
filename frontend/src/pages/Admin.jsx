import { useState, useEffect } from 'react'
import { api } from '../api/client'

function LocationAdminSection({ locations, onAdd, onDelete }) {
  const [newName, setNewName] = useState('')
  const [newPhotoUrl, setNewPhotoUrl] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleAdd = async () => {
    if (!newName.trim()) return
    setLoading(true)
    setError(null)
    try {
      await onAdd(newName.trim(), newPhotoUrl.trim() || null)
      setNewName('')
      setNewPhotoUrl('')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    setError(null)
    try { await onDelete(id) } catch (err) { setError(err.message) }
  }

  return (
    <div className="admin-section">
      <h2 className="section-title">Locations</h2>
      {error && <div className="error-banner">{error}</div>}
      <div className="card">
        <ul className="admin-list">
          {locations.length === 0 && (
            <li className="admin-list-item" style={{ color: 'var(--color-text-muted)', fontStyle: 'italic' }}>
              None added yet.
            </li>
          )}
          {locations.map(loc => (
            <li key={loc.id} className="admin-list-item">
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                {loc.photoUrl && (
                  <img src={loc.photoUrl} alt={loc.name} className="admin-location-thumb" />
                )}
                <span>{loc.name}</span>
              </div>
              <button className="btn btn-danger" onClick={() => handleDelete(loc.id)}>Remove</button>
            </li>
          ))}
        </ul>
        <div className="admin-add-form" style={{ flexDirection: 'column' }}>
          <input
            className="form-input"
            placeholder="Location name…"
            value={newName}
            onChange={e => setNewName(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleAdd()}
          />
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <input
              className="form-input"
              placeholder="Photo URL (optional)…"
              value={newPhotoUrl}
              onChange={e => setNewPhotoUrl(e.target.value)}
            />
            <button className="btn btn-primary" onClick={handleAdd} disabled={loading || !newName.trim()}>
              Add
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

function AdminSection({ title, items, onAdd, onDelete, placeholder }) {
  const [newName, setNewName] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleAdd = async () => {
    if (!newName.trim()) return
    setLoading(true)
    setError(null)
    try {
      await onAdd(newName.trim())
      setNewName('')
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id) => {
    setError(null)
    try { await onDelete(id) } catch (err) { setError(err.message) }
  }

  return (
    <div className="admin-section">
      <h2 className="section-title">{title}</h2>
      {error && <div className="error-banner">{error}</div>}
      <div className="card">
        <ul className="admin-list">
          {items.length === 0 && (
            <li className="admin-list-item" style={{ color: 'var(--color-text-muted)', fontStyle: 'italic' }}>
              None added yet.
            </li>
          )}
          {items.map(item => (
            <li key={item.id} className="admin-list-item">
              <span>{item.name}</span>
              <button className="btn btn-danger" onClick={() => handleDelete(item.id)}>Remove</button>
            </li>
          ))}
        </ul>
        <div className="admin-add-form">
          <input
            className="form-input"
            placeholder={placeholder}
            value={newName}
            onChange={e => setNewName(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleAdd()}
          />
          <button className="btn btn-primary" onClick={handleAdd} disabled={loading || !newName.trim()}>
            Add
          </button>
        </div>
      </div>
    </div>
  )
}

export default function Admin() {
  const [locations, setLocations] = useState([])
  const [categories, setCategories] = useState([])

  useEffect(() => {
    api.get('/api/locations').then(setLocations)
    api.get('/api/categories').then(setCategories)
  }, [])

  return (
    <div style={{ maxWidth: '680px' }}>
      <h1 className="page-title" style={{ marginBottom: '2rem' }}>Admin</h1>

      <LocationAdminSection
        locations={locations}
        onAdd={async (name, photoUrl) => {
          const location = await api.post('/api/locations', { name, photoUrl })
          setLocations(prev => [...prev, location])
        }}
        onDelete={async (id) => {
          await api.delete(`/api/locations/${id}`)
          setLocations(prev => prev.filter(l => l.id !== id))
        }}
      />

      <AdminSection
        title="Categories"
        items={categories}
        onAdd={async (name) => {
          const category = await api.post('/api/categories', { name })
          setCategories(prev => [...prev, category])
        }}
        onDelete={async (id) => {
          await api.delete(`/api/categories/${id}`)
          setCategories(prev => prev.filter(c => c.id !== id))
        }}
        placeholder="New category name…"
      />
    </div>
  )
}
