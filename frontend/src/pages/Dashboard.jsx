import React from 'react';
import {
  LineChart, Line, AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer,
  PieChart, Pie, Cell, BarChart, Bar, Legend
} from 'recharts';

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
  { id: 'TR-1029', date: '17 Abr, 08:30', animal: 'Lote Terneros', from: 'Corral 1', to: 'lote Este', status: 'Completado' },
  { id: 'TR-1028', date: '16 Abr, 16:45', animal: 'Toro #005', from: 'Lote Norte', to: 'Cuarentena', status: 'Completado' },
  { id: 'TR-1027', date: '16 Abr, 09:15', animal: 'Vacas Lecheras (x15)', from: 'Lote Sur', to: 'Ordeño', status: 'Completado' },
];

export default function Dashboard() {
  return (
    <div className="p-8 space-y-8 max-w-[1600px] mx-auto">
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl font-bold text-gray-100 tracking-tight">Visión General</h1>
          <p className="text-gray-400 mt-1">Resumen del estado actual de la hacienda</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div className="glass-card p-5">
          <p className="text-gray-400 text-sm font-medium mb-1">Total Ganado</p>
          <h3 className="text-3xl font-bold text-gray-100">200<span className="text-lg font-normal text-gray-500 ml-1">cabezas</span></h3>
        </div>
        <div className="glass-card p-5">
          <p className="text-gray-400 text-sm font-medium mb-1">Producción Hoy</p>
          <h3 className="text-3xl font-bold text-gray-100">185<span className="text-lg font-normal text-gray-500 ml-1">L</span></h3>
        </div>
        <div className="glass-card p-5">
          <p className="text-gray-400 text-sm font-medium mb-1">Partos Próximos</p>
          <h3 className="text-3xl font-bold text-gray-100">4</h3>
        </div>
        <div className="glass-card p-5">
          <p className="text-gray-400 text-sm font-medium mb-1">En Tratamiento</p>
          <h3 className="text-3xl font-bold text-gray-100">12</h3>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="glass-card p-6 lg:col-span-2">
          <h2 className="text-lg font-bold text-gray-100 mb-6">Producción de Leche</h2>
          <div className="h-72 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={productionData}>
                <defs>
                  <linearGradient id="colorLeche" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#2d9c2d" stopOpacity={0.4}/>
                    <stop offset="95%" stopColor="#2d9c2d" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#2e3d2e" vertical={false} />
                <XAxis dataKey="name" stroke="#6b7280" tick={{fill: '#9ca3af'}} axisLine={false} />
                <YAxis stroke="#6b7280" tick={{fill: '#9ca3af'}} axisLine={false} />
                <RechartsTooltip contentStyle={{ backgroundColor: '#182118', borderColor: '#2e3d2e', borderRadius: '8px' }} />
                <Area type="monotone" dataKey="leche" stroke="#4eba4e" strokeWidth={3} fill="url(#colorLeche)" />
                <Line type="monotone" dataKey="meta" stroke="#9ca3af" strokeWidth={2} strokeDasharray="5 5" dot={false} />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="glass-card p-6">
          <h2 className="text-lg font-bold text-gray-100 mb-6">Distribución por Edad</h2>
          <div className="h-56 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={distributionData} cx="50%" cy="50%" innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                  {distributionData.map((entry, index) => (<Cell key={`cell-${index}`} fill={entry.color} />))}
                </Pie>
                <RechartsTooltip contentStyle={{ backgroundColor: '#182118', borderColor: '#2e3d2e', borderRadius: '8px' }} />
              </PieChart>
            </ResponsiveContainer>
          </div>
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

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="glass-card p-6">
          <h2 className="text-lg font-bold text-gray-100 mb-6">Alertas del Sistema</h2>
          <div className="space-y-4">
            {alerts.map(alert => (
              <div key={alert.id} className="flex gap-4 items-start p-3 rounded-xl">
                <div className={`p-2 rounded-lg mt-1 ${
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
                  <p className="text-xs text-gray-400">{alert.desc}</p>
                  <span className="text-xs text-gray-500">{alert.time}</span>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div className="glass-card p-0 lg:col-span-2 overflow-hidden">
          <div className="p-6 border-b border-dark-400">
            <h2 className="text-lg font-bold text-gray-100">Últimos Movimientos</h2>
          </div>
          <table className="w-full">
            <thead className="bg-dark-800/80">
              <tr>
                <th className="text-left p-3">ID</th>
                <th className="text-left p-3">Fecha</th>
                <th className="text-left p-3">Animal</th>
                <th className="text-left p-3">Origen</th>
                <th className="text-left p-3">Destino</th>
                <th className="text-left p-3">Estado</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-dark-500">
              {recentMovements.map((mov, i) => (
                <tr key={i}>
                  <td className="p-3 font-medium text-gray-200">{mov.id}</td>
                  <td className="p-3">{mov.date}</td>
                  <td className="p-3">{mov.animal}</td>
                  <td className="p-3">{mov.from}</td>
                  <td className="p-3">{mov.to}</td>
                  <td className="p-3"><span className="badge-green">{mov.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}