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
      <NavLink to="/" onClick={close} className="navbar-brand">
        <svg className="navbar-brand-icon" width="22" height="22" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M3 11L12 3l9 8v10h-5v-6H8v6H3V11z" fill="currentColor" />
          <rect x="9.5" y="14" width="5" height="4" rx="0.5" fill="var(--color-sand)" />
        </svg>
        <span className="navbar-brand-text">Stager Hub</span>
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
