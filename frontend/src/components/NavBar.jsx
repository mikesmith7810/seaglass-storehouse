import { useState } from 'react'
import { NavLink } from 'react-router-dom'

const NAV_LINKS = [
  { to: '/',       label: 'Dashboard', end: true },
  { to: '/browse', label: 'Browse' },
  { to: '/search', label: 'Search' },
  { to: '/admin',  label: 'Admin' }
]

export default function NavBar() {
  const [menuOpen, setMenuOpen] = useState(false)
  const close = () => setMenuOpen(false)

  return (
    <nav className="navbar">
      <NavLink to="/" onClick={close}>
        <img src="/logo.png" alt="Seaglass Home Designs" className="navbar-logo" />
      </NavLink>

      <button
        className="navbar-hamburger"
        onClick={() => setMenuOpen(prev => !prev)}
        aria-label="Toggle navigation"
      >
        {menuOpen ? '✕' : '☰'}
      </button>

      <div className={`navbar-links${menuOpen ? ' open' : ''}`}>
        {NAV_LINKS.map(({ to, label, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) => `navbar-link${isActive ? ' active' : ''}`}
            onClick={close}
          >
            {label}
          </NavLink>
        ))}
      </div>
    </nav>
  )
}
