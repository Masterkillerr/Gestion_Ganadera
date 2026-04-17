import React from 'react';
import {
  LineChart, Line, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer,
  PieChart, Pie, Cell, BarChart, Bar, Legend
} from 'recharts';

// --- Synthetic Data ---
const productionData = [
  { name: 'Ene', leche: 4000, meta: 3800 },
  { name: 'Feb', leche: 3800, meta: 3800 },
  { name: 'Mar', leche: 4200, meta: 3900 },
  { name: 'Abr', leche: 4500, meta: 4000 },
  { name: 'May', leche: 4800, meta: 4200 },
  { name: 'Jun', leche: 5100, meta: 4500 },
  { name: 'Jul', leche: 4900, meta: 4600 },
];

const distributionData = [
  { name: 'Adultos (>24m)', value: 120, color: '#2d9c2d' },
  { name: 'Novillos (12-24m)', value: 45, color: '#4eba4e' },
  { name: 'Terneros (0-12m)', value: 35, color: '#82d682' },
];

const alerts = [
  { id: 1, type: 'warning', title: 'Parto inminente', desc: 'Vaca #402 "Lola" (Lote Norte)', time: 'Hace 2 hrs' },
  { id: 2, type: 'critical', title: 'Vacunación retrasada', desc: 'Lote Sur (Fiebre Aftosa)', time: 'Ayer' },
  { id: 3, type: 'info', title: 'Baja producción', desc: 'Vaca #128 bajó 15% esta semana', time: 'Hace 5 hrs' },
];

const recentMovements = [
  { id: 'TR-1029', date: '17 Abr, 08:30', animal: 'Lote Terneros', from: 'Corral 1', to: 'Lote Este', status: 'Completado' },
  { id: 'TR-1028', date: '16 Abr, 16:45', animal: 'Toro #005', from: 'Lote Norte', to: 'Cuarentena', status: 'Completado' },
  { id: 'TR-1027', date: '16 Abr, 09:15', animal: 'Vacas Lecheras (x15)', from: 'Lote Sur', to: 'Ordeño', status: 'Completado' },
];

export default function Dashboard() {
  return (
    <div className="p-8 space-y-8 max-w-[1600px] mx-auto animate-fade-up">
      
      {/* Header Info */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-100 tracking-tight">Visión General</h1>
          <p className="text-gray-400 mt-1">Resumen del estado actual de la hacienda</p>
        </div>
        <div className="flex items-center gap-3">
          <button className="btn-primary">
            <svg className="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4"/></svg>
            Registrar Movimiento
          </button>
          <button className="px-4 py-2 rounded-xl bg-dark-600 hover:bg-dark-500 text-gray-200 text-sm font-medium transition-colors border border-dark-400">
            Exportar Reporte
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {/* Card 1 */}
        <div className="stat-card animate-fade-up-d1">
          <div className="flex justify-between items-start">
            <div>
              <p className="text-gray-400 text-sm font-medium mb-1">Total Ganado</p>
              <h3 className="text-3xl font-bold text-gray-100">200<span className="text-lg font-normal text-gray-500 ml-1">cabezas</span></h3>
            </div>
            <div className="p-2.5 bg-brand-900/50 rounded-lg border border-brand-800">
              <svg className="w-6 h-6 text-brand-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 7v10l8 4 8-4V7l-8-4-8 4z" /></svg>
            </div>
          </div>
          <div className="mt-4 flex items-center gap-2 text-sm">
            <span className="text-brand-400 bg-brand-900/50 px-2 py-0.5 rounded flex items-center gap-1 font-medium">
              <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 10l7-7m0 0l7 7m-7-7v18"/></svg>
              5.2%
            </span>
            <span className="text-gray-500">vs mes anterior</span>
          </div>
        </div>

        {/* Card 2 */}
        <div className="stat-card animate-fade-up-d2">
          <div className="flex justify-between items-start">
            <div>
              <p className="text-gray-400 text-sm font-medium mb-1">Producción Hoy</p>
              <h3 className="text-3xl font-bold text-gray-100">185<span className="text-lg font-normal text-gray-500 ml-1">L</span></h3>
            </div>
            <div className="p-2.5 bg-blue-900/40 rounded-lg border border-blue-800">
              <svg className="w-6 h-6 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z"/></svg>
            </div>
          </div>
          <div className="mt-4 flex items-center gap-2 text-sm">
            <span className="text-brand-400 bg-brand-900/50 px-2 py-0.5 rounded flex items-center gap-1 font-medium">
               <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 10l7-7m0 0l7 7m-7-7v18"/></svg>
               2.1%
            </span>
            <span className="text-gray-500">sobre el promedio</span>
          </div>
        </div>

        {/* Card 3 */}
        <div className="stat-card animate-fade-up-d3 relative overflow-hidden">
          <div className="absolute top-0 right-0 w-32 h-32 bg-amber-500/10 rounded-full blur-3xl -mr-10 -mt-10"></div>
          <div className="flex justify-between items-start relative z-10">
            <div>
              <p className="text-gray-400 text-sm font-medium mb-1">Partos Próximos</p>
              <h3 className="text-3xl font-bold text-gray-100">4</h3>
            </div>
            <div className="p-2.5 bg-amber-900/40 rounded-lg border border-amber-800">
              <svg className="w-6 h-6 text-amber-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/></svg>
            </div>
          </div>
          <div className="mt-4 flex items-center gap-2 text-sm relative z-10">
            <span className="text-gray-300">En los próximos 15 días</span>
          </div>
        </div>

        {/* Card 4 */}
        <div className="stat-card animate-fade-up-d4">
          <div className="flex justify-between items-start">
            <div>
              <p className="text-gray-400 text-sm font-medium mb-1">En Tratamiento</p>
              <h3 className="text-3xl font-bold text-gray-100">12</h3>
            </div>
            <div className="p-2.5 bg-red-900/30 rounded-lg border border-red-800">
              <svg className="w-6 h-6 text-red-400" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/></svg>
            </div>
          </div>
          <div className="mt-4 flex items-center gap-2 text-sm">
             <span className="text-red-400 bg-red-900/30 px-2 py-0.5 rounded flex items-center gap-1 font-medium">
               <svg className="w-3 h-3" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3"/></svg>
               3
            </span>
            <span className="text-gray-500">menos que la semana pasada</span>
          </div>
        </div>
      </div>

      {/* Main Charts Area */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fade-up-d2">
        {/* Main Chart */}
        <div className="glass-card p-6 lg:col-span-2">
          <div className="flex justify-between items-center mb-6">
            <div>
              <h2 className="text-lg font-bold text-gray-100">Producción de LecheMensual</h2>
              <p className="text-sm text-gray-400">Total en litros (Últimos 7 meses)</p>
            </div>
            <select className="bg-dark-600 border border-dark-400 rounded-lg px-3 py-1.5 text-sm text-gray-200 outline-none focus:border-brand-500">
              <option>Año 2026</option>
              <option>Año 2025</option>
            </select>
          </div>
          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={productionData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorLeche" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#2d9c2d" stopOpacity={0.4}/>
                    <stop offset="95%" stopColor="#2d9c2d" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#2e3d2e" vertical={false} />
                <XAxis dataKey="name" stroke="#6b7280" tick={{fill: '#9ca3af', fontSize: 12}} axisLine={false} tickLine={false} />
                <YAxis stroke="#6b7280" tick={{fill: '#9ca3af', fontSize: 12}} axisLine={false} tickLine={false} />
                <RechartsTooltip 
                  contentStyle={{ backgroundColor: '#182118', borderColor: '#2e3d2e', borderRadius: '8px', color: '#f3f4f6' }}
                  itemStyle={{ color: '#82d682' }}
                />
                <Area type="monotone" dataKey="leche" name="Producción Real" stroke="#4eba4e" strokeWidth={3} fillOpacity={1} fill="url(#colorLeche)" />
                <Line type="monotone" dataKey="meta" name="Meta Esperada" stroke="#9ca3af" strokeWidth={2} strokeDasharray="5 5" dot={false} />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Donut Chart */}
        <div className="glass-card p-6">
          <h2 className="text-lg font-bold text-gray-100 mb-6">Distribución por Edad</h2>
          <div className="h-56 w-full relative">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={distributionData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={80}
                  paddingAngle={5}
                  dataKey="value"
                  stroke="none"
                >
                  {distributionData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <RechartsTooltip 
                  contentStyle={{ backgroundColor: '#182118', borderColor: '#2e3d2e', borderRadius: '8px', color: '#f3f4f6' }}
                  itemStyle={{ color: '#fff' }}
                />
              </PieChart>
            </ResponsiveContainer>
            {/* Center Text */}
            <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none">
              <span className="text-3xl font-bold text-gray-100">200</span>
              <span className="text-xs text-gray-400">Total</span>
            </div>
          </div>
          {/* Custom Legend */}
          <div className="mt-4 space-y-3">
            {distributionData.map((item, idx) => (
              <div key={idx} className="flex justify-between items-center text-sm">
                <div className="flex items-center gap-2">
                  <span className="w-3 h-3 rounded-full" style={{ backgroundColor: item.color }}></span>
                  <span className="text-gray-300">{item.name}</span>
                </div>
                <span className="font-medium text-gray-100">{item.value}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom Section (Tables) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 animate-fade-up-d3">
        {/* Alerts List */}
        <div className="glass-card p-6">
          <div className="flex justify-between items-center mb-6">
            <h2 className="text-lg font-bold text-gray-100">Alertas del Sistema</h2>
            <button className="text-brand-400 text-sm hover:text-brand-300 font-medium">Ver todas</button>
          </div>
          <div className="space-y-4">
            {alerts.map(alert => (
              <div key={alert.id} className="flex gap-4 items-start p-3 rounded-xl hover:bg-dark-600 border border-transparent hover:border-dark-400 transition-colors">
                <div className={`p-2 rounded-lg mt-1 shrink-0 ${
                  alert.type === 'warning' ? 'bg-amber-900/30 text-amber-500' :
                  alert.type === 'critical' ? 'bg-red-900/30 text-red-500' :
                  'bg-blue-900/30 text-blue-500'
                }`}>
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                  </svg>
                </div>
                <div>
                  <h4 className="text-sm font-semibold text-gray-200">{alert.title}</h4>
                  <p className="text-xs text-gray-400 line-clamp-1 mt-0.5">{alert.desc}</p>
                  <span className="text-xs text-gray-500 mt-1 block">{alert.time}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Recent Movements Table */}
        <div className="glass-card p-0 lg:col-span-2 overflow-hidden flex flex-col">
          <div className="p-6 border-b border-dark-400 flex justify-between items-center bg-dark-700/50">
            <h2 className="text-lg font-bold text-gray-100">Últimos Movimientos</h2>
            <button className="text-brand-400 text-sm hover:text-brand-300 font-medium">Historial completo</button>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full data-table">
              <thead className="bg-dark-800/80">
                <tr>
                  <th>ID Movimiento</th>
                  <th>Fecha</th>
                  <th>Animal/Lote</th>
                  <th>Origen</th>
                  <th>Destino</th>
                  <th>Estado</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-dark-500">
                {recentMovements.map((mov, i) => (
                  <tr key={i}>
                    <td className="font-medium text-gray-200">{mov.id}</td>
                    <td>{mov.date}</td>
                    <td>{mov.animal}</td>
                    <td>{mov.from}</td>
                    <td>{mov.to}</td>
                    <td>
                      <span className="badge-green">{mov.status}</span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
