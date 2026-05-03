import { useState, useEffect } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { api } from '../api/client'

const fmt = (price) =>
  `£${parseFloat(price).toLocaleString('en-GB', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`

export default function ItemDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [item, setItem] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.get(`/api/items/${id}`)
      .then(setItem)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false))
  }, [id])

  if (loading) return <div className="loading">Loading item…</div>
  if (error)   return <div className="error-banner">{error}</div>
  if (!item)   return null

  const dims = [
    item.heightCm && `H ${item.heightCm}`,
    item.widthCm  && `W ${item.widthCm}`,
    item.depthCm  && `D ${item.depthCm}`
  ].filter(Boolean).join(' · ')

  return (
    <div style={{ maxWidth: '760px' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '1.5rem', flexWrap: 'wrap' }}>
        <button className="btn btn-secondary btn-sm" onClick={() => navigate(-1)}>← Back</button>
        <span style={{ color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>Item #{item.id}</span>
        <Link to={`/items/${id}/edit`} className="btn btn-primary btn-sm" style={{ marginLeft: 'auto' }}>Edit</Link>
      </div>

      <div className="item-detail-photo-wrap">
        {item.photoUrl
          ? <img src={item.photoUrl} alt={item.description} />
          : <span>No photo yet</span>}
      </div>

      <div className="card">
        <h1 style={{ fontSize: '1.6rem', marginBottom: '1.5rem' }}>{item.description}</h1>

        <div className="item-detail-grid">
          <div>
            <div className="detail-label">Category</div>
            <div className="detail-value"><span className="badge">{item.categoryName}</span></div>
          </div>
          <div>
            <div className="detail-label">Location</div>
            <div className="detail-value">{item.locationName}</div>
          </div>
          <div>
            <div className="detail-label">Price</div>
            <div className="detail-value" style={{ fontFamily: 'var(--font-heading)', fontWeight: 700, fontSize: '1.3rem', color: 'var(--color-teal)' }}>
              {fmt(item.price)}
            </div>
          </div>
          <div>
            <div className="detail-label">Label ID</div>
            <div className="detail-value" style={{ fontFamily: 'var(--font-heading)', fontWeight: 700, fontSize: '1.5rem', color: 'var(--color-teal)' }}>
              #{item.id}
            </div>
          </div>
          {dims && (
            <div style={{ gridColumn: '1 / -1' }}>
              <div className="detail-label">Dimensions (cm)</div>
              <div className="detail-value">{dims}</div>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
