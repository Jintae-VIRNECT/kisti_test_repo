import Model from '@/models/Model'
export default class Application extends Model {
  /**
   * 파일 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.uuid = json.uuid
    this.category = json.deviceType ?? json.category
    this.name = json.appUrl ? this.generatorFileName(json.appUrl) : json.name
    this.extensionList = json.extensionList ?? json.extensionList
    this.version = json.version ?? ''
    this.versionCode = json.versionCode ?? ''
    this.released = json.releaseTime ?? json.released

    // APK 파일이 필요한 값
    this.signingKey = json.signingKey ?? ''

    // 공통으로 필요한 값
    this.deviceModel = json.deviceModel ?? ''
    this.operationSystem = json.operationSystem ?? ''
    this.productName = json.productName ?? ''

    // 업데이트 알림
    this.updateStatus = json.deviceSupportUpdateStatus ?? 'INACTIVE'
  }

  generatorFileName(appUrl) {
    const array = appUrl.split('/')
    return decodeURI(array[array.length - 1])
  }
}

/**
 * @description 클라이언트에서 관리하는 서비스 리스트입니다.
 * @todo 구축형으로 나갈때 서버와 리스트를 맞춰야 한다.
 */
export const productList = {
  remote: [
    new Application({
      uuid: '',
      category: 'Mobile',
      name: ['{file_name}.apk'],
      extensionList: ['apk'],
      version: '2.5.10',
      versionCode: '',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'SMARTPHONE_TABLET',
      operationSystem: 'ANDROID',
      productName: 'REMOTE',
    }),
    // new Application({
    //   category: 'iOS',
    //   name: ['{file_name}.plist', '{file_name}.ipa'],
    //   extensionList: ['plist', 'ipa'],
    //   version: '2.5.10',
    //   released: 'YY.MM.DD HH:DD',
    //   deviceModel: 'SMARTPHONE_TABLET',
    //   operationSystem: 'IOS',
    //   productName: 'REMOTE',
    // }),
    new Application({
      uuid: '',
      category: 'Hololens',
      name: ['{file_name}.appx', '{file_name}.appxbundle'],
      extensionList: ['appx', 'appxbundle'],
      version: '2.5.10',
      versionCode: '',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'HOLOLENS_2',
      operationSystem: 'WINDOWS_UWP',
      productName: 'REMOTE',
    }),
    new Application({
      uuid: '',
      category: 'Realwear',
      name: ['{file_name}.apk'],
      extensionList: ['apk'],
      version: '2.5.10',
      versionCode: '',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'HMT_SERIES',
      operationSystem: 'ANDROID',
      productName: 'REMOTE',
    }),
    new Application({
      uuid: '',
      category: 'Linkflow',
      name: ['{file_name}.apk'],
      extensionList: ['apk'],
      version: '2.5.10',
      versionCode: '',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'FITT/NEXX',
      operationSystem: 'ANDROID',
      productName: 'REMOTE',
    }),
  ],
  make: [
    new Application({
      uuid: '',
      category: 'PC',
      name: ['{file_name}.exe'],
      extensionList: ['exe'],
      version: '2.5.10',
      versionCode: '',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'WINDOWS_10',
      operationSystem: 'WINDOWS',
      productName: 'MAKE',
    }),
  ],
  view: [
    new Application({
      uuid: '',
      category: 'Mobile',
      name: ['{file_name}.apk'],
      extensionList: ['apk'],
      version: '2.5.10',
      versionCode: '',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'SMARTPHONE_TABLET',
      operationSystem: 'ANDROID',
      productName: 'VIEW',
    }),
    new Application({
      uuid: '',
      category: 'Hololens',
      name: ['{file_name}.appx'],
      extensionList: ['appx', 'appxbundle'],
      version: '2.5.10',
      versionCode: '',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'HOLOLENS_2',
      operationSystem: 'WINDOWS_UWP',
      productName: 'VIEW',
    }),
    new Application({
      uuid: '',
      category: 'Rearwear',
      name: ['{file_name}.apk'],
      extensionList: ['apk'],
      version: '2.5.10',
      versionCode: '',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'HTM-1',
      operationSystem: 'ANDROID',
      productName: 'VIEW',
    }),
  ],
}
