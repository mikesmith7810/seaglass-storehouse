import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { api } from '../api/client'

export default function ItemForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id)

  const [categories, setCategories] = useState([])
  const [locations, setLocations] = useState([])
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  const [form, setForm] = useState({
    description: '',
    categoryId: '',
    locationId: '',
    price: '',
    heightCm: '',
    widthCm: '',
    depthCm: ''
  })

  useEffect(() => {
    Promise.all([api.get('/api/categories'), api.get('/api/locations')])
      .then(([cats, locs]) => { setCategories(cats); setLocations(locs) })
  }, [])

  useEffect(() => {
    if (!isEdit) return
    api.get(`/api/items/${id}`)
      .then(item => setForm({
        description: item.description    ?? '',
        categoryId:  item.categoryId     ?? '',
        locationId:  item.locationId     ?? '',
        price:       item.price          ?? '',
        heightCm:    item.heightCm       ?? '',
        widthCm:     item.widthCm        ?? '',
        depthCm:     item.depthCm        ?? ''
      }))
      .catch(err => setError(err.message))
  }, [id, isEdit])

  const update = (field) => (e) => setForm(prev => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    setError(null)

    const body = {
      description: form.description,
      categoryId:  parseInt(form.categoryId),
      locationId:  parseInt(form.locationId),
      price:       parseFloat(form.price),
      heightCm:    form.heightCm ? parseFloat(form.heightCm) : null,
      widthCm:     form.widthCm  ? parseFloat(form.widthCm)  : null,
      depthCm:     form.depthCm  ? parseFloat(form.depthCm)  : null
    }

    try {
      if (isEdit) {
        await api.put(`/api/items/${id}`, body)
        navigate(`/items/${id}`)
      } else {
        const created = await api.post('/api/items', body)
        navigate(`/items/${created.id}`)
      }
    } catch (err) {
      setError(err.message)
      setSubmitting(false)
    }
  }

  return (
    <div style={{ maxWidth: '620px' }}>
      <h1 className="page-title" style={{ marginBottom: '1.75rem' }}>
        {isEdit ? 'Edit Item' : 'Add Item'}
      </h1>

      {error && <div className="error-banner">{error}</div>}

      <form onSubmit={handleSubmit} className="card">
        <div className="form-group">
          <label className="form-label">Description *</label>
          <input
            className="form-input"
            required
            placeholder="e.g. Grey corner sofa"
            value={form.description}
            onChange={update('description')}
          />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label className="form-label">Category *</label>
            <select className="form-select" required value={form.categoryId} onChange={update('categoryId')}>
              <option value="">Select…</option>
              {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div className="form-group">
            <label className="form-label">Location *</label>
            <select className="form-select" required value={form.locationId} onChange={update('locationId')}>
              <option value="">Select…</option>
              {locations.map(l => <option key={l.id} value={l.id}>{l.name}</option>)}
            </select>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Price (£) *</label>
          <input
            className="form-input"
            type="number" step="0.01" min="0"
            required
            placeholder="0.00"
            value={form.price}
            onChange={update('price')}
          />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem' }}>
          {[['heightCm','Height'], ['widthCm','Width'], ['depthCm','Depth']].map(([field, label]) => (
            <div key={field} className="form-group">
              <label className="form-label">{label} (cm)</label>
              <input
                className="form-input"
                type="number" step="0.1" min="0"
                placeholder="—"
                value={form[field]}
                onChange={update(field)}
              />
            </div>
          ))}
        </div>

        <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end', marginTop: '0.5rem' }}>
          <button type="button" className="btn btn-secondary" onClick={() => navigate(-1)}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Saving…' : 'Save Item'}
          </button>
        </div>
      </form>
    </div>
  )
}
