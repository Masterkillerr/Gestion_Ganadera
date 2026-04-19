import React from 'react';

export default function Dashboard() {
  return (
    <div style={{ padding: '2rem', color: 'white', background: '#0a0f0a', minHeight: '100vh' }}>
      <h1 style={{ fontSize: '2rem', fontWeight: 'bold', color: '#4eba4e' }}>Gestión Ganadera</h1>
      <p>Dashboard loading...</p>
      <div style={{ marginTop: '2rem', padding: '1rem', background: '#182118', borderRadius: '8px' }}>
        <h2 style={{ color: '#82d682' }}>Total Ganado: 200</h2>
        <h2 style={{ color: '#82d682' }}>Producción Hoy: 185L</h2>
      </div>
    </div>
  );
}