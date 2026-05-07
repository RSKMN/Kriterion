import React from 'react'

export default function MainLayout({ children }: { children: React.ReactNode }) {
  return (
    <div>
      <header>Top nav (placeholder)</header>
      <main>{children}</main>
    </div>
  )
}
