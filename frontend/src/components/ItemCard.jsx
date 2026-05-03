import { Link } from 'react-router-dom'

const formatPrice = (price) =>
  `£${parseFloat(price).toLocaleString('en-GB', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`

export default function ItemCard({ item }) {
  return (
    <Link to={`/items/${item.id}`} className="item-card">
      <div className="item-card-photo">
        {item.photoUrl
          ? <img src={item.photoUrl} alt={item.description} />
          : <span>No photo</span>}
      </div>
      <div className="item-card-body">
        <div className="item-card-id">Item #{item.id}</div>
        <div className="item-card-description">{item.description}</div>
        <div className="item-card-meta">
          <span className="badge">{item.categoryName}</span>
          {item.locationName}
        </div>
        <div className="item-card-price">{formatPrice(item.price)}</div>
      </div>
    </Link>
  )
}
