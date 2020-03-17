import api from '@/api/gateway'
import { Content } from '@/models/content'

export default {
  async getDefaultContentsList() {
    const data = await api('CONTENTS_LIST', {
      params: {
        size: 20,
      },
    })
    return data.contentInfo.map(content => Content(content))
  },
}
