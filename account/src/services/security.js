import LoggedInDevice from '@/models/security/LoggedInDevice'

export default {
  getLoggedInDeviceList() {
    return []
    const data = [0, 1, 2, 3, 4]
    return data.map(device => new LoggedInDevice(device))
  },
}
