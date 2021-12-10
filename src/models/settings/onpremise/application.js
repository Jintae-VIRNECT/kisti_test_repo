import Model from '@/models/Model'
export default class Application extends Model {
  /**
   * 파일 구조
   * @param {Object} json
   */
  constructor(json) {
    super()
    this.category = json.deviceType ?? json.category
    this.format = json.appUrl ? this.generatorFormat(json.appUrl) : json.format
    this.version = json.version
    this.released = json.releaseTime ?? json.released

    // APK 파일이 필요한 값
    this.signingKey = json.signingKey ?? ''

    // 공통으로 필요한 값
    this.deviceModel = json.deviceModel ?? ''
    this.operationSystem = json.operationSystem ?? ''
    this.productName = json.productName ?? ''
  }

  generatorFormat(appUrl) {
    const array = appUrl.split('/')
    return decodeURI(array[array.length - 1])
  }
}

export const productList = {
  remote: [
    new Application({
      category: 'Mobile',
      format: ['{file_name}.apk'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'SMARTPHONE_TABLET',
      operationSystem: 'ANDROID',
      productName: 'REMOTE',
    }),
    new Application({
      category: 'iOS',
      format: ['{file_name}.plist', '{file_name}.ipa'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'SMARTPHONE_TABLET',
      operationSystem: 'IOS',
      productName: 'REMOTE',
    }),
    new Application({
      category: 'Hololens',
      format: ['{file_name}.appx'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'HOLOLENS_2',
      operationSystem: 'WINDOWS_UWP',
      productName: 'REMOTE',
    }),
    new Application({
      category: 'Realwear',
      format: ['{file_name}.apk'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'SMARTPHONE_TABLET',
      operationSystem: 'ANDROID',
      productName: 'REMOTE',
    }),
    new Application({
      category: 'Linkflow',
      format: ['{file_name}.apk'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'FITT/NEXX',
      operationSystem: 'ANDROID',
      productName: 'REMOTE',
    }),
  ],
  make: [
    new Application({
      category: 'PC',
      format: ['{file_name}.exe'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'WINDOWS_10',
      operationSystem: 'WINDOWS',
      productName: 'MAKE',
    }),
  ],
  view: [
    new Application({
      category: 'Mobile',
      format: ['{file_name}.apk'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'SMARTPHONE_TABLET',
      operationSystem: 'ANDROID',
      productName: 'MAKE',
    }),
    new Application({
      category: 'Hololens',
      format: ['{file_name}.appx'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'HOLOLENS_2',
      operationSystem: 'WINDOWS_UWP',
      productName: 'MAKE',
    }),
    new Application({
      category: 'Rearwear',
      format: ['{file_name}.apk'],
      version: '2.5.10',
      released: 'YY.MM.DD HH:DD',
      deviceModel: 'HTM-1',
      operationSystem: 'ANDROID',
      productName: 'MAKE',
    }),
  ],
}
