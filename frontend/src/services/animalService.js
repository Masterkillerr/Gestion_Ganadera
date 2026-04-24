import api from './api';

const ganadoService = {
  // Get all ganado
  getAll: async () => {
    const response = await api.get('/api/ganado');
    return response.data;
  },

  // Get ganado by ID
  getById: async (id) => {
    const response = await api.get(`/api/ganado/${id}`);
    return response.data;
  },

  // Create new ganado
  create: async (ganadoData) => {
    const response = await api.post('/api/ganado', ganadoData);
    return response.data;
  },

  // Update ganado
  update: async (id, ganadoData) => {
    const response = await api.put(`/api/ganado/${id}`, ganadoData);
    return response.data;
  },

  // Delete ganado
  delete: async (id) => {
    const response = await api.delete(`/api/ganado/${id}`);
    return response.data;
  },

  // Get ganado by lote
  getByLote: async (loteId) => {
    const response = await api.get(`/api/ganado/lote/${loteId}`);
    return response.data;
  },

  // Get ganado by finca
  getByFinca: async (fincaId) => {
    const response = await api.get(`/api/ganado/finca/${fincaId}`);
    return response.data;
  },
};

export default ganadoService;
