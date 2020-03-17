import api from '@/api/gateway'
import { Process } from '@/models/process'

export default {
  async getDefaultProcessList() {
    const data = await api('PROCESS_LIST', {
      params: {
        size: 20,
      },
    })
    return data.processes.map(process => Process(process))
  },
}
