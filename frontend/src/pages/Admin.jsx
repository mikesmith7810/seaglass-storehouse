import { useState, useEffect } from 'react'
import { api } from '../api/client'

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
    try {
      await onDelete(id)
    } catch (err) {
      setError(err.message)
    }
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
              <button className="btn btn-danger" onClick={() => handleDelete(item.id)}>
                Remove
              </button>
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

      <AdminSection
        title="Locations"
        items={locations}
        onAdd={async (name) => {
          const location = await api.post('/api/locations', { name })
          setLocations(prev => [...prev, location])
        }}
        onDelete={async (id) => {
          await api.delete(`/api/locations/${id}`)
          setLocations(prev => prev.filter(l => l.id !== id))
        }}
        placeholder="New location name…"
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
